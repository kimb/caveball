package core;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.ImageIcon;

/**
 * Pelidatan käsittelyn implementaatio.
 */
public class GameDataImpl implements GameData {

	/** Nykyinen kartta */
	protected MapImage mapImage;

	/** Kaikki partikkelit */
	protected Map particles;

	/** Pelaajan partikkelin id-avain */
	protected Integer playerParticleId;
	
	/** Pelaajan nimi */
	protected PlayerProperties playerProperties;

	/** Serverin asetuksia */
	protected ServerProperties serverProperties;

	/**
	 * Palauttaa nykyisen kartan tieto-olion.
	 */
	public MapImage getMapImage() {
		return mapImage;
	}

	public GameDataImpl() {
		reset();
	}

	/** Palauttaa kaikki asetukset alkuarvoihin */
	protected void reset() {
		playerProperties= new PlayerProperties();
		serverProperties= new ServerProperties();
		playerParticleId=null;
		mapImage = null;
		particles  = new HashMap(3, 0.9f);

	}


	/**
	 * Asettaa uuden kartan.
	 */ 
	void setMapImage(MapImage mi) {
		mapImage = mi;
	}

	// Javadoc in interface
	public Particle getPlayerParticle() {
		if(playerParticleId==null) {
			return null;
		} else {
			return (Particle) particles.get(playerParticleId);
		}
	}

	/**
	 * Asettaa nykyisen pelaajaan id:n
	 */
	public void setPlayerParticle(Particle playerParticle) {
		if(playerParticle == null) {
			playerParticleId = null;
		} else {
			this.playerParticleId = playerParticle.getId();
		}
	}

	/** Asettaa kaikki pelin partikkelit */
	public void setParticles(Particle[] particles) {
		this.particles.clear();
		for(int i=0; i<particles.length ; i++) {
			this.particles.put(particles[i].getId(), particles[i]);
		}
	}

	/**
	 * Palauttaa kaikki partikkelit.
	 */
	public Particle[] getParticles() {
		Collection ps = particles.values();
		return (Particle[]) ps.toArray(new Particle[ps.size()]);
	}

	/**
	 * Lisää yhden partikkelin.
	 * Olemassaolevan partikkelin lisäys korvaa vanhan.
	 */
	public void addParticle(Particle part) {
		particles.put(part.getId(),part);
	}

	/**
	 * Poistaa yhden partikkelin
	 */
	public void delParticle(Particle part) {
		particles.remove(part.getId());
	}

	/** Hakee määrätyn partikkelin, tai null jos ei löydy */
	public Particle getParticle(Integer id) {
		return (Particle) particles.get(id);
	}

	/** Hakee pelaajan asetukset. */
	public PlayerProperties getPlayerProperties() {
		return this.playerProperties;
	}

	/** Hakee serverin asetukset. */
	public ServerProperties getServerProperties() {
		return this.serverProperties;
	}

}
	
