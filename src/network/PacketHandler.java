package network;

import core.*;
import java.io.Serializable;

/**
 * Pakettien käsittelijä.
 * Asettaa verkosta tulleiden pakettien viiveen ja lähettää ne
 * eteenpäin verkkopakettikäsittelijälle.
 * Lähettää myös olioita verkkopaketteina verkon yli,
 * ja voi katkaista yhteytensä.
 */
public class PacketHandler implements PacketListener, Connection {
    
    /** Pakettikuuntelijan yhteys. */
    protected Connection connection = null;
    /** Pakettikäsittelijän luonut yhteyskäsittelijä. */
    protected ConnectionHandler cHandler = null;
    /** Viivelaskija. */
    protected PingController pingController = null;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Onko yhteys Server-tyyppinen. */
    protected boolean isServer = false;
    
    /**
     * Luo uusi pakettikäsittelijä.
     * @param connection Pakettikuuntelijan yhteys.
     * @param cHandler Pakettikäsittelijän luonut yhteyskäsittelijä.
     * @param s Onko yhteys Server-tyyppinen.
     */
    public PacketHandler(Connection c, ConnectionHandler cHandler, boolean s) {
	// Tarkistetaan että yhteys ja yhteyskuuntelija ovat kelvollisia
	if ((c == null) || (cHandler == null))
	    throw new RuntimeException("Packet handler assignment failed.");
	this.connection = c;
	this.cHandler = cHandler;
	this.pingController = new PingController(c);
	this.isServer = s;
	this.connected = true;
    }
    
    /**
     * Käsittele verkosta saatu paketti.
     * Verkkopaketille asetetaan viive, ja paketti siirretään
     * yhteyskäsittelijälle.
     * @param data Vastaanotettu paketti.
     */
    // Toteuttaa PacketListener-rajapinnan handlePacket-metodin
    public void handlePacket(Object data) {
	if (data instanceof NetworkPacketImpl) {
	    NetworkPacketImpl nPacket;
	    nPacket = (NetworkPacketImpl)data;
	    nPacket.setSource(this);
	    nPacket.setPing(0); // Tässä pitäisi laskea oikea ping
	    cHandler.handlePacket(nPacket);
	}
	else throw new RuntimeException(
		       "Received packet is not a NetworkPacket: "
		       + data.toString());
    }
    
    /**
     * Yhteydessä tapahtui virhe.
     * Ilmoitetaan eteenpäin yhteyskäsittelijälle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	cHandler.connectionError(message, this);
    }
    
    /**
     * Lähetä paketti verkon yli.
     * Lähetettävästä oliosta tehdään verkkopaketti.
     * @param data Lähetettävä olio, oltava Serializable.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (connected) {
	    NetworkPacketImpl nPacket = new NetworkPacketImpl();
	    nPacket.setTime(0); // Tässä pitäisi asettaa oikea aika
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
