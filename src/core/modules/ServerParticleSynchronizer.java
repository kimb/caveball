package core.modules;

import core.*;
import core.packets.*;
import util.*;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * Hoitaa serverin partikkelien sijaintip‰ivitykset.
 * Lis‰ksi l‰hett‰‰ kaikille (ja itselleen) Broadcast-paketit.
 */
public class ServerParticleSynchronizer implements Module {

	private final Logger log = Logger.getLogger(this.getClass());

	/** T‰m‰n controlleri */
	protected Controller controller;
	/** gameData */
	protected GameData gameData;
	/** verkko. */
	protected Network network;
	/** oma pakettien vastaanottaja */
	protected NetworkPacketListener packetListener;

	public ServerParticleSynchronizer(Controller controller,
			NetworkPacketListener packetListener) {
		this.controller = controller;
		this.gameData = controller.getGameData();
		this.network = controller.getNetwork();
		this.packetListener = packetListener;
	}
		
	private int count=1;

	/** L‰hett‰‰ p‰ivityksen kaikille pelaajille aina silloin t‰llˆin */
	public void tic(GameData gameData, int timestep) {
		if(count++%5 == 0) {
			ServerParticlePacket spp = new ServerParticlePacket(gameData);
			synchronized(gameData) {
				network.sendPacket(spp);
			}
		}
	}

	/** K‰sittelee klienttien l‰hett‰m‰t PlayerParticlePacket */
	public void packetRecieved(NetworkPacket packet) { 
		if(packet.getData() instanceof PlayerParticlePacket) {
			Particle pNew = 
				((PlayerParticlePacket) packet.getData()).playerParticle;
			Particle pOld = gameData.getParticle(pNew.getId());
			if(pOld != null && pOld.getBounceCount() > pNew.getBounceCount()) {
				// Pallo on pompannut serverill‰, klientin pit‰‰ saada p‰ivitys
				return;
			}
			synchronized(gameData) {
				gameData.addParticle(pNew);
			}
		} else if(packet.getData() instanceof ClientBroadcastPacket) {
			ClientBroadcastPacket cp = (ClientBroadcastPacket) packet.getData();
			network.sendPacket(cp.getPacketToBroadcast());
			packetListener.handlePacket(new LoopbackPacket(
						cp.getPacketToBroadcast()));
		}
	}

	/**
	 * Tulostaa modulin nimen.
	 */
	public String toString() {
		return "ServerParticleSynchronizer";
	}

}
