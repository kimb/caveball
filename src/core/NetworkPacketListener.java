package core;

import java.io.*;

/**
 * M‰‰rittelee caveballin verko-osalta saapuvat tapahtumat.
 * Toisin kuin nimest‰ voi kuvitella, voi tapahtuma olla muukin kuin
 * NetworkPacket:in saapuminen.
 */
public interface NetworkPacketListener {

	/** Toisen pelaajaan tai serverin l‰hett‰m‰ paketti saapuu */
	public void handlePacket(NetworkPacket packet);
	
	/**
	 * Kutsutaan kun l‰hiverkosta lˆytyy caveball palvelin.
	 * @param address osoite jossa peli on
	 * @param definition Serverin m‰‰ritelm‰.
	 */
	public void serverFound(String address, Serializable definition);

	/**
	 * Kutsutaan kun uusi yhteys on muodostunut. T‰m‰n j‰lkeen voi
	 * paketteja ryhty‰ l‰hettelem‰‰n. Ennen t‰m‰n kutsua ei paketteja voi
	 * l‰hett‰‰kk‰‰n.
	 * @param source Luotu yhteys.
	 */
	public void connectionEstablished(Object source);

	/**
	 * Kutsutaan jos client-yhteyden tai serverin 
	 * muodostaminen ep‰onnistui.
	 * @param reason Ep‰onnistumisen syy.
	 * @param source Yhteys tai server jossa ep‰onnistuminen tapahtui.
	 */
	public void connectionAttemptFailed(String reason, Object source);

	/**
	 * Kutsutaan kun yhteys serveriin katkeaa.
	 * Ei kutsuta kun toimitaan serverin‰.
	 * T‰m‰n j‰lkeen ei voi en‰‰ l‰hett‰‰ paketteja.
	 * @param reason Syy yhteyden katkeamiseen.
	 * @param source Katkennut yhteys.
	 */
	public void connectionLost(String reason, Object source);

}
