package test;

import core.*;
import network.*;

public class ClientTester extends Thread implements NetworkTester {
    
    private Network network = null;
    private TestNetworkPacketListener npListener = null;
    private boolean running = false;
    
    public ClientTester(Network network, String host) {
	super("ClientTester");
	this.network = network;
	this.npListener = new TestNetworkPacketListener(this, network);
	this.network.setNetworkPacketListener(npListener);
	System.out.println("Network created: " + network.toString());
	System.out.print("Connecting to server ... ");
	network.connectTo(host);
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
	   this.disconnect();
    }
    
    public void connected() {
	this.start();
    }
    
    public void disconnect() {
	System.out.print("\nDisconnecting ... ");
	running = false;
	synchronized (network) {
	    try {
		network.notify();
	    } catch (Exception e) { }
	}
	network.disconnect();
	System.out.println("Disconnected");
	
    }
}
