package network;

import core.*;
import java.util.*;

/**
 * Server-yhteyksien käsittelijä.
 * Toimii yhteyskuuntelijana ja asettaa yhteyksille pakettikuuntelijat.
 * Voi myös lähettää paketteja yhteyksien kautta sekä katkaista ne.
 */
public class ServerConnectionHandler
    implements ServerConnectionListener, ConnectionHandler {
    
    /** Yhteyksien pakettikäsittelijät sisältävä synkronoitu lista. */
    protected List pHandlers = 
	Collections.synchronizedList(new ArrayList());
    /** Yhteyskäsittelijän luonut verkkoluokka. */
    protected NetworkImpl sourceNetwork = null;
    /** Yhtyskäsittelijän serveriluokka */
    protected Server server = null;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Kuunnellaanko uusia yhteyksiä. */
    protected boolean listening = false;
    /** Kertoo että kyseessä on palvelinyhteyskuuntelija */
    protected final boolean isServer = true;
    
    /**
     * Luo Server-yhteyksien käsittelijä.
     * @param sourceNetwork Yhteyskäsittelijän luonut verkkoluokka.
     * @param server Serveriluokka, josta uudet yhteydet tulevat.
     */
    public ServerConnectionHandler(NetworkImpl sourceNetwork, Server server) {
	// Tarkistetaan että asetettava verkkoluokka on kelvollinen
        if (sourceNetwork != null) {
            this.sourceNetwork = sourceNetwork;
	    this.server = server;
	}
        else throw new RuntimeException("Source Network assignment failed.");
    }
    
    /**
     * On saatu uusi yhteys.
     * Antaa uudelle yhteydelle pakettikäsittelijän
     * joka lisätään yhteyslistaan.
     * @return Uudesta yhteydestä tulevat paketit käsittelevä olio.
     * @param c Saatu yhteys.
     * @param source Olio, jossa yhteys on luotu.
     */
    // Toteuttaa ConnectionListener-rajapinnan newConnection-metodin
    public PacketListener newConnection(Connection c, Object source) {
	// Tarkistetaan että yhteys on kelvollinen ja
	// että uusia yhteyksiä otetaan vastaan
	if ((c == null) || (!listening))
	    throw new RuntimeException("Connection assignment failed.");
	else {
	    PacketHandler pHandler = new PacketHandler(c, this, isServer);
	    synchronized(pHandlers) {
		pHandlers.add(pHandler);
	    }
	    return pHandler;
	}
    }
    
    /**
     * Kuuntelu epäonnistui.
     * Ilmoitetaan eteenpäin verkkoluokalle.
     * @param reason Epäonnistumisen syy.
     * @param source Server, jossa kuuntelu epäonnistui.
     */
    // Toteuttaa ServerConnectionListener-rajapinnan listeningFailed-metodin
    public void listeningFailed(String reason, Object source) {
	if (listening) {
	    this.stopListening();
	    sourceNetwork.connectionError(reason, server);
	}
	else sourceNetwork.serverStarted(false, server);
    }
    
    /**
     * Kuuntelun aloittaminen onnistui.
     * Ilmoitetaan eteenpäin verkkoluokalle.
     * @param source Luotu serveri.
     */
    // Toteuttaa ServerConnectionListener-rajapinnan listeningStarted-metodin
    public void listeningStarted(Object source) {
	listening = true;
	connected = true;
	sourceNetwork.serverStarted(true, server);
    }
    
    /**
     * Lopeta uusien yhteyksien kuunteleminen.
     */
    public void stopListening() {
	if (listening) {
	    listening = false;
	    server.stopPortListening();
	}
    }
    
    /**
     * Yhteydessä tapahtui virhe.
     * Ilmoitetaan eteenpäin verkkoluokalle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */    
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	sourceNetwork.connectionError(message, source);
    }
    
    /**
     * Käsittele verkosta saatu paketti.
     * Paketti siirretään verkkoluokan käsiteltäväksi.
     * @param data Vastaanotettu paketti.
     */
    // Toteuttaa PacketListener-rajapinnan handlePacket-metodin
    public void handlePacket(Object data) {
	if (data instanceof NetworkPacket)
	    sourceNetwork.handlePacket((NetworkPacket)data);
	else throw new RuntimeException(
		       "Packet is not a Network Packet: " + data);
    }
    
    /**
     * Lähetä paketti verkon yli kaikkiin yhteyksiin
     * pakettikuuntelijoiden avulla.
     * @param data Lähetettävä olio.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (!connected)
	    throw new RuntimeException(
		      "Unable to send packet, not connected :"
		      + data.toString());
	synchronized(pHandlers) {
	    Iterator i = pHandlers.iterator();
	    Connection connection = null;
	    while (connected && i.hasNext()) {
		connection = (Connection)i.next();
		if (connection.isConnected())
		    connection.sendPacket(data);
		else i.remove(); // Poista suljettu yhteys listasta
	    }
	}
    }
    
    /**
     * Katkaise kaikki yhteydet.
     * Pyytää pakettikäsittelijöitä katkaisemaan yhteydet.
     */
    // Toteuttaa Connection-rajapinnan disconnect-metodin
    public void disconnect() {
	this.stopListening();
	connected = false;
	synchronized(pHandlers) {
	    Iterator i = pHandlers.iterator();
	    Connection connection = null;
	    while (i.hasNext()) {
		connection = (Connection)i.next();
		connection.disconnect();
	    }
	}
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
