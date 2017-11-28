package core.packets;

import core.*;
import java.io.*;

/**
 * Chat-viesti
 */
public class ChatPacket implements ModuleNetworkPacket {

	protected String message;

	/** For serialization */
	protected ChatPacket() { }

	protected ChatPacket(String message) { 
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

	/** 
	 * Luo uuden viestipaketin. Koska viestipaketti t‰ytyy laittaa
	 * ClientBroadcastPacket:in sis‰‰n ChatPacket:illa ei ole julkista
	 * konstruktoria.
	 */
	public static Packet createChatPacket(String message) {
		ChatPacket p = new ChatPacket(message);
		return new ClientBroadcastPacket(p);
	}

}
