package core;

import java.awt.Color;

/**
 * M‰‰rittelee serverin asetukset.
 */
public class ServerProperties {

	/** Seuraavan pelaajan id. */
	protected int nextPlayerId = 0;

	/** Serverin nimi */
	protected String serverName = "noname-server";


	/** Palauttaa seuraavan pelaaja-id:n */
	public int getNextPlayerId () {
		return nextPlayerId++;
	}

	/** Palauttaa palvelimen nimen */
	public String getServerName () {
		return serverName ;
	}
	/** Asettaa palvelimen nimen */
	public void setServerName (String serverName ) {
		this.serverName  = serverName ;
	}
}
