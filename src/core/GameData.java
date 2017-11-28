package core;

/**
 * Julkisen pelidatan käsittelemiseen tarkoitettu rajapinta.
 * Controller ei käytä pelkästään tätä rajapintaa.
 */
public interface GameData {

	/**
	 * Palauttaa nykyisen kartan kuvan.
	 * Kartan vaihtuessa taataan, että myös MapImage:n instanssi vaihtuu.
	 */
	public MapImage getMapImage();

	/**
	 * Palauttaa kaikki partikkelit.
	 */
	public Particle[] getParticles();

	/** Lisää partikkelin */
	public void addParticle(Particle p);

	/** Poistaa partikkelin */
	public void delParticle(Particle p);

	/** Hakee määrätyn partikkelin, tai null jos ei löydy */
	public Particle getParticle(Integer id);

	/**
	 * Palauttaa pelaajan partikkelin.
	 * @return null jos pelaajalle ei ole partikkelia
	 */
	public Particle getPlayerParticle();

	/** Asettaa pelaajan partikkelin */
	public void setPlayerParticle(Particle p);

	/** Asettaa kaikki pelin partikkelit */
	public void setParticles(Particle[] particles);

	/** Hakee pelaajan asetukset */
	public PlayerProperties getPlayerProperties();

	/** Hakee serverin asetukset */
	public ServerProperties getServerProperties();
}

