package core;

import core.packets.*;

/**
 * M��rittelee l�ytyneiden palvelimien k�sittelij�n.
 * T�m� on implementoitava jos aikoo k�ytt�� controlleria uusien pelien l�yt�miseen.
 */
public interface SearchResultHandler {

	/**
	 * Kutsutaan kun l�hiverkosta l�ytyi uusi palvelin.
	 * @param address Osoite, jonka toString()-metodi palauttaa jotain j�rkev��
	 */
	public void serverFound(
			String address,
			GamePreview preview);

	/** Kutsutaan jos liittyminen peliin ep�onnistuu */
	public void connectFailed(String reason);
		
}
