package core.packets;

import core.*;
import java.io.*;

/**
 * Klientin paketti joka toimitetaan serverilt‰ kaikille klienteille.
 * Jos klientti saa t‰m‰n paketin, niin sit‰ ei tarvitse l‰hett‰‰ eteenp‰in.
 */
public class ClientBroadcastPacket implements ModuleNetworkPacket {

	protected Packet packetToBroadcast;

	/** For serialization */
	protected ClientBroadcastPacket() { }

	/** Luo uuden paketin k‰ytt‰en pelaajan partikkelia */
	public ClientBroadcastPacket(Packet packetToBroadcast) {
		this.packetToBroadcast = packetToBroadcast;
	}

	public Packet getPacketToBroadcast() {
		return packetToBroadcast;
	}

}
