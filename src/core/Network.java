package core;

import java.io.Serializable;

/**
 * M‰‰rittelee caveballin serverille ett‰ klientille vaadittavat verkko-osan
 * funktiot.
 */
public interface Network {

	/**
	 * Asettaa paketteja vastaanottavan osan.
	 * T‰m‰ t‰ytyy asettaa ennenkuin mit‰‰n muita implementaation
	 * funktiota kutsutaan.
	 * @param npListener Tulleiden pakettien ja erikoistapahtumien
	 * k‰sittelij‰.
	 */
	public void setNetworkPacketListener(
		NetworkPacketListener npListener);

	/**
	 * Hakee servereit‰ l‰hiverkosta. Tulokset ilmoitetaan asynkroonisesti
	 * NetworkPacketListener:ille. Tuloksista lakataan ilmoittamasta heti
	 * kun jotain rajapinnan toisista metodeista kutsutaan.
	 */
	public void searchForServers();

	/**
	 * K‰ynnist‰‰ uusien yhteyksien vastaanottamisen. 
	 * Alkaa lis‰ksi kertomaan olemassaolostaan l‰hiverkkoon.
	 * @param definition Serverin tunnistava m‰‰ritelm‰. T‰m‰ l‰hetet‰‰n
	 * tunnistavana tietona jos jokin pelaaja 
	 */
	public void startServer(Serializable definition);

	/**
	 * Yhdist‰ytyy annettuun serveriin.
	 * @param serverAddress Joko ip-osoite tai dns-nimi
	 */
	public void connectTo(String serverAddress);

	/**
	 * L‰hett‰‰ paketin kaikille yhdistetyille koneille.
	 * Ei blokkaa, vaan palaa heti.
	 * @param packet L‰hetett‰v‰ paketti.
	 */
	public void sendPacket(Serializable data);

	/**
	 * L‰hett‰‰ paketin m‰‰r‰tylle koneelle.
	 * Ei blokkaa, vaan palaa heti.
	 * @param destination Yhteys johon paketti l‰hetet‰‰n.
	 */
	public void sendPacket(Serializable data, Object destination);

	/**
	 * Katkaisee yhteyden tai lopettaa serverin‰ toimimisen
	 */
	public void disconnect();

}
