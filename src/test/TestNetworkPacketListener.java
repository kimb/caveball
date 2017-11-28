package test;

import core.*;
import network.*;

import java.io.*;

public class TestNetworkPacketListener
    implements core.NetworkPacketListener {
    
    private NetworkTester tester = null;
    private Network network = null;
    private boolean searching = true;
	
    public TestNetworkPacketListener(NetworkTester tester, Network network) {
	this.tester = tester;
	this.network = network;
    }
	
    public void handlePacket(NetworkPacket packet) {
	System.out.println("Packet received: " +
			   packet.getData().toString());
    }
    public void serverFound(String address, Serializable definition) {
	System.out.println("Server Found: " + definition.toString());
	
	/*
	if (searching) {
	    searching = false;
	    network.connectTo(address);
	}
	*/
    }
    public void connectionEstablished(Object source) {
	System.out.println("Connection established");
	tester.connected();
    }
    public void connectionAttemptFailed(String reason, Object source) {
	System.out.println("Connection attempt failed: " + reason);
    }
    public void connectionLost(String reason, Object source) {
	System.out.println("Connecton lost: " + reason);
	tester.disconnect();
    }
}
