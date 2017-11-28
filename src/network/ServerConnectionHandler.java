package network;

import core.*;
import java.util.*;

/**
 * Server-yhteyksien k�sittelij�.
 * Toimii yhteyskuuntelijana ja asettaa yhteyksille pakettikuuntelijat.
 * Voi my�s l�hett�� paketteja yhteyksien kautta sek� katkaista ne.
 */
public class ServerConnectionHandler
    implements ServerConnectionListener, ConnectionHandler {
    
    /** Yhteyksien pakettik�sittelij�t sis�lt�v� synkronoitu lista. */
    protected List pHandlers = 
	Collections.synchronizedList(new ArrayList());
    /** Yhteysk�sittelij�n luonut verkkoluokka. */
    protected NetworkImpl sourceNetwork = null;
    /** Yhtysk�sittelij�n serveriluokka */
    protected Server server = null;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Kuunnellaanko uusia yhteyksi�. */
    protected boolean listening = false;
    /** Kertoo ett� kyseess� on palvelinyhteyskuuntelija */
    protected final boolean isServer = true;
    
    /**
     * Luo Server-yhteyksien k�sittelij�.
     * @param sourceNetwork Yhteysk�sittelij�n luonut verkkoluokka.
     * @param server Serveriluokka, josta uudet yhteydet tulevat.
     */
    public ServerConnectionHandler(NetworkImpl sourceNetwork, Server server) {
	// Tarkistetaan ett� asetettava verkkoluokka on kelvollinen
        if (sourceNetwork != null) {
            this.sourceNetwork = sourceNetwork;
	    this.server = server;
	}
        else throw new RuntimeException("Source Network assignment failed.");
    }
    
    /**
     * On saatu uusi yhteys.
     * Antaa uudelle yhteydelle pakettik�sittelij�n
     * joka lis�t��n yhteyslistaan.
     * @return Uudesta yhteydest� tulevat paketit k�sittelev� olio.
     * @param c Saatu yhteys.
     * @param source Olio, jossa yhteys on luotu.
     */
    // Toteuttaa ConnectionListener-rajapinnan newConnection-metodin
    public PacketListener newConnection(Connection c, Object source) {
	// Tarkistetaan ett� yhteys on kelvollinen ja
	// ett� uusia yhteyksi� otetaan vastaan
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
     * Kuuntelu ep�onnistui.
     * Ilmoitetaan eteenp�in verkkoluokalle.
     * @param reason Ep�onnistumisen syy.
     * @param source Server, jossa kuuntelu ep�onnistui.
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
     * Ilmoitetaan eteenp�in verkkoluokalle.
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
     * Yhteydess� tapahtui virhe.
     * Ilmoitetaan eteenp�in verkkoluokalle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */    
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	sourceNetwork.connectionError(message, source);
    }
    
    /**
     * K�sittele verkosta saatu paketti.
     * Paketti siirret��n verkkoluokan k�sitelt�v�ksi.
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
     * L�het� paketti verkon yli kaikkiin yhteyksiin
     * pakettikuuntelijoiden avulla.
     * @param data L�hetett�v� olio.
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
     * Pyyt�� pakettik�sittelij�it� katkaisemaan yhteydet.
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
