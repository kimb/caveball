package core.modules;

import core.*;
import core.packets.*;
import util.*;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Hoitaa klientin partikkelien sijaintip‰ivitykset.
 */
public class PlayerParticleSynchronizer implements Module {

	private final Logger log = Logger.getLogger(this.getClass());

	/** T‰m‰n controlleri */
	protected Controller controller;
	/** gameData */
	protected GameData gameData;
	/** verkko. */
	protected Network network;
	/** oma pakettien vastaanottaja */
	protected NetworkPacketListener packetListener;


	public PlayerParticleSynchronizer(Controller controller,
			NetworkPacketListener packetListener) {
		this.controller = controller;
		this.gameData = controller.getGameData();
		this.network = controller.getNetwork();
		this.packetListener=packetListener;
	}

	private int count=0;
	
	/** L‰hett‰‰ p‰ivityksen serverille */
	public void tic(GameData gameData, int timestep) {
		if(count++%5 == 0) {
			PlayerParticlePacket ppp = new PlayerParticlePacket(gameData);
			synchronized(gameData) {
				network.sendPacket(ppp);
			}
		}
	}

	/** K‰sittelee klienttien l‰hett‰m‰t ServerParticlePacket */
	public void packetRecieved(NetworkPacket packet) { 
		if(packet.getData() instanceof ServerParticlePacket) {
			Particle[] particles = 
				((ServerParticlePacket)packet.getData()).particles;
			Particle pOld = gameData.getPlayerParticle();
			Particle pNew = null;
			if(pOld!=null) {
				for(int i=0; i<particles.length ; i++) {
					if(particles[i].getId().equals(pOld.getId())) {
						pNew=particles[i];
						break;
					}
				}
			}
			synchronized(gameData) {
				gameData.setParticles(particles);
				if(pOld==null || pNew == null || 
						pOld.getBounceCount()<pNew.getBounceCount()) {
					// Serverin particle on oikeassa.
				} else {
					gameData.addParticle(pOld);
				}
			}
		} else if(packet.getData() instanceof ClientBroadcastPacket) {
			ClientBroadcastPacket cp = (ClientBroadcastPacket) packet.getData();
			// client won't echo to the server
			// network.sendPacket(cp.getPacketToBroadcast());
			packetListener.handlePacket(new LoopbackPacket(
						cp.getPacketToBroadcast()));
		}
	}

	/**
	 * Tulostaa modulin nimen.
	 */
	public String toString() {
		return "PlayerParticleSynchronizer";
	}
}
