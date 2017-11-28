package test;

import core.*;
import network.*;

public class SearchTester extends Thread implements NetworkTester {
    
    private Network network = null;
    private TestNetworkPacketListener npListener = null;
    private boolean running = false;
    
    public SearchTester(Network network) {
	super("SearchTester");
	this.network = network;
	this.npListener = new TestNetworkPacketListener(this, network);
	this.network.setNetworkPacketListener(npListener);
	System.out.println("Network created: " + network.toString());
	System.out.print("Searching server ... ");
	network.searchForServers();
    }
    
    public void run() {
       System.out.println("Sending packets ... ");
       running = true;
       String packet;
       for (int i=0; i<3 && running; i++) {
	   packet = "Client " + i;
	   System.out.println("Sending packet: " + packet);
	   network.sendPacket(packet);
	   synchronized (network) {
	       try {
		   network.wait(2000);
		} catch (Exception e) { }
	   }
       }
       if (running)
          npListener.connectionLost("Calling connection lost ... ", this);
    }
    
    public void connected() {
	this.start();
    }
    
    public void disconnect() {
	System.out.print("\nDisconnecting ... ");
	if (running) {
	    running = false;
	    synchronized (network) {
		try {
		    network.notify();
		} catch (Exception e) { }
	    }
	    network.disconnect();
	    System.out.println("Disconnected");
	}
	else System.out.println("Already disconnected");
    }
}
