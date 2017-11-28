package core.modules;

import core.*;
import core.packets.*;
import util.*;

import org.apache.log4j.Logger;

/**
 * K‰ytet‰‰n serverill‰ ottamaan vastaan klientin liittymispyyntˆj‰.
 * L‰hett‰‰ takaisin kartan.
 */
public class ServerJoinHandlerModule implements Module {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Mist‰ saa paketteja l‰hetetty‰ */
	private Network network;

	/** Mist‰ voi hakea nykyisen kartan ja muuta tietoa vastusta varten */
	private GameData gameData;

	public ServerJoinHandlerModule(Network network, GameData gameData) {
		this.network = network;
		this.gameData = gameData;
	}
		
	/** Ei tee mit‰‰n */
	public void tic(GameData gameData, int timestep) { }

	/** Vastaa JoinRequest paketteihin JoinReply-paketeilla. */
	public void packetRecieved(NetworkPacket packet) { 
		if(packet.getData() instanceof JoinRequest) {
			log.info("Accepting new client: "+packet.getSource());
			network.sendPacket(
					new JoinReply(gameData),
					packet.getSource());
		}
	}

	/**
	 * Tulostaa modulin nimen.
	 */
	public String toString() {
		return "ServerJoinHandlerModule";
	}
}
