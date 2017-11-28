package core.packets;

import core.*;

/**
 * Serverin vastaus liittymispyynt��n.
 */
public class PlayerParticlePacket implements ModuleNetworkPacket {

	/** Pelaajan huikkanan. */
	public Particle playerParticle;

	/** For serialization */
	protected PlayerParticlePacket() { }

	/** Luo uuden paketin k�ytt�en pelaajan partikkelia */
	public PlayerParticlePacket(GameData gameData) {
		this.playerParticle = gameData.getPlayerParticle();
	}

}
