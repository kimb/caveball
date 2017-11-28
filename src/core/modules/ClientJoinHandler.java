package core.modules;

import core.*;
import core.packets.*;
import util.*;

import org.apache.log4j.Logger;

/**
 * K‰ytet‰‰n klientill‰ vastaanottamaan serverilt‰ tulevia JoinReply-paketteja.
 */
public class ClientJoinHandler implements Module {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Minne saatu kartta pistet‰‰n */
	private Controller c;

	/** Kertoo ollanko jo saatu vastaus palvelimelta */
	private boolean connected = false;

	public ClientJoinHandler(Controller c) {
		this.c = c;
	}
		
	/** Ei tee mit‰‰n */
	public void tic(GameData gameData, int timestep) { }

	/** Vastaa JoinRequest paketteihin JoinReply-paketeilla. */
	synchronized public void packetRecieved(NetworkPacket packet) { 
		if(connected) {
			log.info("Client rejected a server: "+packet.getSource()+
					" We already have a server");
			return;
		}
		if(packet.getData() instanceof JoinReply) {
			JoinReply j = (JoinReply) packet.getData();
			log.info("Client accepted a server: "+packet.getSource()
					+" with id: "+j.playerId);
			c.loadMap(new MapImageImpl(
						ImageOperations.asBufferedImage(j.map.getImage())));
			c.getGameData().getPlayerProperties().setId(j.playerId);
			c.startClient();
		}
	}

	/**
	 * Tulostaa modulin nimen.
	 */
	public String toString() {
		return "ServerJoinHandlerModule";
	}
}
