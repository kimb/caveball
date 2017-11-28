package core.packets;

import core.*;

/**
 * Serverin vastaus liittymispyyntöön.
 */
public class PlayerParticlePacket implements ModuleNetworkPacket {

	/** Pelaajan huikkanan. */
	public Particle playerParticle;

	/** For serialization */
	protected PlayerParticlePacket() { }

	/** Luo uuden paketin käyttäen pelaajan partikkelia */
	public PlayerParticlePacket(GameData gameData) {
		this.playerParticle = gameData.getPlayerParticle();
	}

}
