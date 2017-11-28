package core.modules;

import core.*;
import util.*;

import org.apache.log4j.Logger;

/**
 * Luo pelaajalle pallon, ja regeneroi sen kun pelaaja kuolee.
 */
public class PlayerParticleHandlerModule implements Module {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Pelaajan odotusaika, ennen uuden pallon saamista. Millisekunttia. */
	protected long respawnDelay = 3000;

	/** Koska pelaajalla oli viimeksi pallo */
	private long lastExisted = System.currentTimeMillis();

	/** Mihin pelaajalle luodaan partikkeleita. */
	private Controller controller;

	public PlayerParticleHandlerModule(Controller controller) {
		this.controller = controller;
	}

	/** iteroi aloituspaikkojen yli */
	private int startLocIter = 0;
		
	/** Lis‰‰ pelaajalle pallon jos h‰nell‰ ei ole sellaista */
	public void tic(GameData gameData, int timestep) {
		if(gameData.getPlayerParticle() != null) {
			lastExisted = System.currentTimeMillis();
			return;
		}
		if( System.currentTimeMillis() - lastExisted < respawnDelay) {
			return;
		}
		log.info("Adding a playerParticle");
		PlayerParticle player = new PlayerParticle("pomppija");
		player.setId(controller.getGameData().getPlayerProperties().getId());
		synchronized(gameData) {
			gameData.addParticle(player);
			if (controller.getGameData().getMapImage().
					getStartLocations().length==0) 
			{
				player.setPosition(new Vector
						(gameData.getMapImage().getWidth()/2,
						 gameData.getMapImage().getHeight()/2));
			} else {
				Vector[] startLocations=controller.getGameData().
					getMapImage().getStartLocations();
				player.setPosition(startLocations[
						startLocIter%startLocations.length]);
			}
			player.setColor(gameData.getPlayerProperties().getColor());
			gameData.setPlayerParticle(player);
		}
		// send a server info about the new packet.
	}

	/** Ei tee mit‰‰n */
	public void packetRecieved(NetworkPacket packet) { 
	}

	/**
	 * Tulostaa modulin nimen ja tiedot.
	 */
	public String toString() {
		return "PlayerParticleHandlerModule";
	}
}
