package core;

import core.packets.*;

/**
 * Määrittelee löytyneiden palvelimien käsittelijän.
 * Tämä on implementoitava jos aikoo käyttää controlleria uusien pelien löytämiseen.
 */
public interface SearchResultHandler {

	/**
	 * Kutsutaan kun lähiverkosta löytyi uusi palvelin.
	 * @param address Osoite, jonka toString()-metodi palauttaa jotain järkevää
	 */
	public void serverFound(
			String address,
			GamePreview preview);

	/** Kutsutaan jos liittyminen peliin epäonnistuu */
	public void connectFailed(String reason);
		
}
