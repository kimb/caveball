package network;

import core.*;
import java.io.Serializable;

/**
 * Pakettien k‰sittelij‰.
 * Asettaa verkosta tulleiden pakettien viiveen ja l‰hett‰‰ ne
 * eteenp‰in verkkopakettik‰sittelij‰lle.
 * L‰hett‰‰ myˆs olioita verkkopaketteina verkon yli,
 * ja voi katkaista yhteytens‰.
 */
public class PacketHandler implements PacketListener, Connection {
    
    /** Pakettikuuntelijan yhteys. */
    protected Connection connection = null;
    /** Pakettik‰sittelij‰n luonut yhteysk‰sittelij‰. */
    protected ConnectionHandler cHandler = null;
    /** Viivelaskija. */
    protected PingController pingController = null;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Onko yhteys Server-tyyppinen. */
    protected boolean isServer = false;
    
    /**
     * Luo uusi pakettik‰sittelij‰.
     * @param connection Pakettikuuntelijan yhteys.
     * @param cHandler Pakettik‰sittelij‰n luonut yhteysk‰sittelij‰.
     * @param s Onko yhteys Server-tyyppinen.
     */
    public PacketHandler(Connection c, ConnectionHandler cHandler, boolean s) {
	// Tarkistetaan ett‰ yhteys ja yhteyskuuntelija ovat kelvollisia
	if ((c == null) || (cHandler == null))
	    throw new RuntimeException("Packet handler assignment failed.");
	this.connection = c;
	this.cHandler = cHandler;
	this.pingController = new PingController(c);
	this.isServer = s;
	this.connected = true;
    }
    
    /**
     * K‰sittele verkosta saatu paketti.
     * Verkkopaketille asetetaan viive, ja paketti siirret‰‰n
     * yhteysk‰sittelij‰lle.
     * @param data Vastaanotettu paketti.
     */
    // Toteuttaa PacketListener-rajapinnan handlePacket-metodin
    public void handlePacket(Object data) {
	if (data instanceof NetworkPacketImpl) {
	    NetworkPacketImpl nPacket;
	    nPacket = (NetworkPacketImpl)data;
	    nPacket.setSource(this);
	    nPacket.setPing(0); // T‰ss‰ pit‰isi laskea oikea ping
	    cHandler.handlePacket(nPacket);
	}
	else throw new RuntimeException(
		       "Received packet is not a NetworkPacket: "
		       + data.toString());
    }
    
    /**
     * Yhteydess‰ tapahtui virhe.
     * Ilmoitetaan eteenp‰in yhteysk‰sittelij‰lle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	cHandler.connectionError(message, this);
    }
    
    /**
     * L‰het‰ paketti verkon yli.
     * L‰hetett‰v‰st‰ oliosta tehd‰‰n verkkopaketti.
     * @param data L‰hetett‰v‰ olio, oltava Serializable.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (connected) {
	    NetworkPacketImpl nPacket = new NetworkPacketImpl();
	    nPacket.setTime(0); // T‰ss‰ pit‰isi asettaa oikea aika
	    nPacket.setData((Serializable)data);
	    connection.sendPacket(nPacket);
	}
	else throw new RuntimeException(
		       "Unable to send packet, not connected: "
		       + data.toString());
    }
    
    /**
     * Katkaise yhteys.
     */
    // Toteuttaa Connection-rajapinnan disconnect-metodin
    public void disconnect() {
	connected = false;
	connection.disconnect();
    }
    
    /**
     * Onko yhteys auki.
     * @return Onko yhteys auki.
     */
    // Toteuttaa Connection-rajapinnan isConnected-metodin
    public boolean isConnected() {
	return connected;
    }

}
