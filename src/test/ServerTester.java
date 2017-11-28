package test;

import core.*;
import network.*;

public class ServerTester extends Thread implements NetworkTester {
	
    private Network network = null;
    private TestNetworkPacketListener npListener = null;
    private boolean running = false;
	
    public ServerTester(Network network) {
	super("ServerTester");
	this.network = network;
	this.npListener = new TestNetworkPacketListener(this, network);
	this.network.setNetworkPacketListener(npListener);
	System.out.println("Network created: " + network.toString());
	System.out.print("Starting server ... ");
	network.startServer("TestServer");
    }

    public void run() {
	System.out.println("Sending packets ... ");
	running = true;
	String packet;
	for (int i=0; i<10 && running; i++) {
	    packet = "Server " + i;
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
	System.out.println("Disconnecting ... ");
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
