package core.packets;

import core.*;

/**
 * Serverin vastaus liittymispyynt��n.
 */
public class ServerParticlePacket implements ModuleNetworkPacket {

	/** Pelaajan huikkanan. */
	public Particle[] particles;

	/** For serialization */
	protected ServerParticlePacket() { }

	/** Luo uuden paketin k�ytt�en pelaajan partikkelia */
	public ServerParticlePacket(GameData gameData) {
		synchronized(gameData) {
			this.particles = gameData.getParticles();
		}
	}

}
