package network;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Servereit� tai clienteja etsitt�ess� k�ytett�v� yhetys-
 * sek� pakettik�sittelij�.
 */
public class SearchConnectionHandler
    implements ClientConnectionListener, ConnectionHandler {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Client-yhteys. */
    protected Connection connection = null;
    /** Yhteyden pakettikuuntelija. */
    protected PacketHandler pHandler = null;
    /** Yhteysk�sittelij�n luonut verkkoluokka. */
    protected NetworkImpl sourceNetwork = null;
    /** Yhteysk�sittelij�n luonut etsij�. */
    protected ClientSearch searcher = null;
    /** L�ydetyt serverit. */
    protected List foundServers = 
	Collections.synchronizedList(new ArrayList());
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Toimitaanko serverin�. */
    protected boolean isServer = false;
    
    /**
     * Luo uusi servereit� etsiv� yhteysk�sittelij�.
     * @param sourceNetwork K�sittelij�n luonut verkkoluokka.
     */
    public SearchConnectionHandler(NetworkImpl sourceNetwork) {
	// Tarkistetaan ett� asetettava verkkoluokka on kelvollinen
        if (sourceNetwork != null) {
            this.sourceNetwork = sourceNetwork;
	    this.isServer = false;
	}
        else throw new RuntimeException("Source Network assignment failed.");
    }    
    
    /**
     * Luo uusi clienteja etsiv� yhteysk�sittelij�.
     * @param searcher K�sittelij�n luonut etsij�.
     */
    public SearchConnectionHandler(ClientSearch searcher) {
	// Tarkistetaan ett� asetettava etsij� on kelvollinen
	if (searcher != null) {
	    this.searcher = searcher;
	    this.isServer = true;
	}
        else throw new RuntimeException("Client searcher assignment failed.");
    }
    
    /**
     * On saatu uusi multicast-yhteys. Voidaan kutsua vain kerran.
     * Yhteyden pakettik�sittelij�n� toimii luokka itse.
     * @return Uudesta yhteydest� tulevat paketit k�sittelev� olio.
     * @param c Saatu yhteys.
     * @param source Olio, jossa yhteys on luotu.
     */
    // Toteuttaa ConnectionListener-rajapinnan newConnection-metodin
    public PacketListener newConnection(Connection c, Object source) {
	// Tarkistetaan ett� yhteys on kelvollinen eik� ole viel� asetettu.
	if ((c == null) || (!(c instanceof MulticastConnection)) ||
	    (connection != null))
	    throw new RuntimeException("Connection assignment failed.");
	else {
	    connection = c;
	    return this;
	}
    }
    
    /**
     * Yritetty yhteys ep�onnistui.
     * Ep�onnistumisesta ilmoitetaan joko k�sittelij�n luoneelle
     * verkolle tai etsij�lle.
     * @param reason Ep�onnistumisen syy.
     * @param source Yhteys, jossa yhteydenotto ep�onnistui.
     */
    // Toteuttaa ClientConnectionListener-rajapinnan connectionFailed-metodin
    public void connectionFailed(String reason, Object source) {
	if (isServer)
	    searcher.clientSearchStarted(false, this); // connection ?
	else
	    sourceNetwork.serverSearchStarted(false, this);
    }
    
    /**
     * Yhtydenotto onnistui.
     * Yhteyden avaamisesta ilmoitetaan joko k�sittelij�n luoneelle
     * verkolle tai etsij�lle.
     * @param source Luotu yhteys.
     */
    // Toteuttaa ClientConnectionListener-rajapinnan connectionOpened-metodin
    public void connectionOpened(Object source) {
	connected = true;
	if (isServer)
	    searcher.clientSearchStarted(true, this);
	else
	    sourceNetwork.serverSearchStarted(true, this);
    }
    
    /**
     * Yhteydess� tapahtui virhe.
     * Virheest� ilmoitetaan joko k�sittelij�n luoneelle
     * verkolle tai etsij�lle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	if (isServer)
	    searcher.connectionError(message, this);
	else
	    sourceNetwork.connectionError(message, this);
    }
    
    /**
     * L�het� paketti verkon yli.
     * @param data L�hetett�v� olio, oltava ClientSearchPacket.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (connected) {
	    if (data instanceof ClientSearchPacket)
		connection.sendPacket(data);
	    else throw new RuntimeException(
			   "Unable to send packet, "
			   + "data is not a client search packet. " + data);
	}    
	else throw new RuntimeException(
		       "Unable to send packet, not connected: " + data);
    }
    
    /**
     * K�sittele verkosta saatu paketti.
     * Paketista haetaan serverin tiedot mik�li etsit��n
     * servereit�, muuten pakettia ei k�sitell� mitenk��n.
     * @param data Vastaanotettu paketti, oltava
     * clientinetsint�paketin sis�lt�v� datagrammi.
     */
    // Toteuttaa PacketListener-rajapinnan handlePacket-metodin
    public void handlePacket(Object data) {
	if (isServer) return; // Serveri ei tarvitse paketteja
	if (data instanceof DatagramPacket) {
	    log.info("Datagram received.");
	    byte[] buf = ((DatagramPacket)data).getData();
	    String address =
		(((DatagramPacket)data).getAddress()).getHostAddress();
	    ByteArrayInputStream inBytes;
	    ObjectInputStream in;
	    Object contents;
	    try {
		inBytes = new ByteArrayInputStream(buf);
		in = new ObjectInputStream(inBytes);
		contents = in.readObject();
		if (contents instanceof ClientSearchPacket) {
		    this.serverFound(address, contents);
		}
		else throw new RuntimeException(
			       "Packet is not a client search packet. "
			       + data);
	    } catch (ClassNotFoundException e) {
		log.warn("Packet with unknown class received.", e);
		throw new RuntimeException(
			  "Packet with unknown class received. " + e);
	    } catch (IOException e) {
		log.error("Unable to read object.", e);
		throw new RuntimeException("Unable to read object. " + e);
	    }
	}
	else throw new RuntimeException("Packet is not a datagram. " + data);
    }
    
    /**
     * Serveri l�ytyi verkosta. Se lis�t��n l�ytyneiden serverien listaan
     * mik�li sen osoitetta ei aikaisemmin ole tunnettu.
     * Serverin tiedot annetaan verkkoluokalle mik�li uusi serveri l�ytyi.
     * @param address Serverin osoite.
     * @param data Serverin tiedot.
     */
    protected void serverFound(String address, Object data) {
	synchronized(foundServers) {
	    Iterator i = foundServers.iterator();
	    String sAddress;
	    while (i.hasNext() && connected) {
		sAddress = (String)i.next();
		if (address.equals(sAddress))
		    return;
	    }
	    foundServers.add(address);
	    sourceNetwork.serverFound(address, data);
	}
    }
    
    /**
     * Katkaise yhteys.
     */
    // Toteuttaa Connection-rajapinnan disconnect-metodin
    public void disconnect() {
	if (connected) {
	    connected = false;
	    connection.disconnect();
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
