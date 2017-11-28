package graphics;

import core.*;
import core.packets.*;
import util.*;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Piirt‰‰ tulevat viestit
 */
public class ChatRendererPlugin implements core.Module, RendererPlugin
{
	private final Logger log = Logger.getLogger(this.getClass());

	/** Kertoo kuinka kauan yht‰ viesti‰ n‰ytet‰‰n. */
	public long MESSAGE_LIFE = 5000;

	/** N‰ytett‰v‰t viestit, AgingMessage-tyyppi‰. */
	protected ArrayList messages = new ArrayList();

	/** T‰m‰n kontrolleri */
	protected Controller controller;

	public ChatRendererPlugin(Controller controller) {
		this.controller=controller;
	}

	/**
	 * Piirt‰‰ kartan.
	 */
	public void render(Graphics2D g, GameData gameData) {
		StringBuffer s = new StringBuffer();
		g.setColor(new Color(255,255,5,150));
		synchronized(messages) {
			Iterator i = messages.iterator();
			long currtime = System.currentTimeMillis();
			int count=0;
			for(; i.hasNext();) {
				AgingMessage a = (AgingMessage) i.next();
				if(a.created + MESSAGE_LIFE < currtime) {
					i.remove();
				} else {
					g.drawString(a.getMessage(), 2, 20+(count++)*12);
				}
			}
		}
	}

	public void sendChat(String m) {
		String message =
			controller.getGameData().getPlayerProperties().getName()
			+": "
			+m;
		Packet p = core.packets.ChatPacket.createChatPacket(message);
		controller.getNetwork().sendPacket(p);
		if(controller.getState()==Controller.STATE_SERVER) {
			addMessage(message);
		}
	}

	/** Lis‰t‰‰n kaikkissa chat-paketeissa tulleet viestit n‰ytett‰v‰ksi */
	public void packetRecieved(NetworkPacket packet) {
		if(packet.getData() instanceof ChatPacket) {
			ChatPacket cp = (ChatPacket) packet.getData();
			log.debug("Received message: "+cp.getMessage());
			addMessage(cp.getMessage());
		}
	}

	protected void addMessage(String message) {
		synchronized(messages) {
			messages.add(new AgingMessage(message));
		}
	}

	/** Piirett‰v‰ viesti joka myˆs vanhenee */
	public class AgingMessage {
		public long created = System.currentTimeMillis();
		protected String message;
		public AgingMessage(String message) {
			this.message=message;
		}
		public String getMessage() {
			return message;
		}
	}

	public void tic(GameData gameData, int timestep) { }

	public String toString() {
		return "ChatRendererPlugin";
	}

}
