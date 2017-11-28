package core;

/**
 * Julkisen pelidatan k�sittelemiseen tarkoitettu rajapinta.
 * Controller ei k�yt� pelk�st��n t�t� rajapintaa.
 */
public interface GameData {

	/**
	 * Palauttaa nykyisen kartan kuvan.
	 * Kartan vaihtuessa taataan, ett� my�s MapImage:n instanssi vaihtuu.
	 */
	public MapImage getMapImage();

	/**
	 * Palauttaa kaikki partikkelit.
	 */
	public Particle[] getParticles();

	/** Lis�� partikkelin */
	public void addParticle(Particle p);

	/** Poistaa partikkelin */
	public void delParticle(Particle p);

	/** Hakee m��r�tyn partikkelin, tai null jos ei l�ydy */
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

