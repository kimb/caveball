package network;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Servereitä tai clienteja etsittäessä käytettävä yhetys-
 * sekä pakettikäsittelijä.
 */
public class SearchConnectionHandler
    implements ClientConnectionListener, ConnectionHandler {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Client-yhteys. */
    protected Connection connection = null;
    /** Yhteyden pakettikuuntelija. */
    protected PacketHandler pHandler = null;
    /** Yhteyskäsittelijän luonut verkkoluokka. */
    protected NetworkImpl sourceNetwork = null;
    /** Yhteyskäsittelijän luonut etsijä. */
    protected ClientSearch searcher = null;
    /** Löydetyt serverit. */
    protected List foundServers = 
	Collections.synchronizedList(new ArrayList());
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Toimitaanko serverinä. */
    protected boolean isServer = false;
    
    /**
     * Luo uusi servereitä etsivä yhteyskäsittelijä.
     * @param sourceNetwork Käsittelijän luonut verkkoluokka.
     */
    public SearchConnectionHandler(NetworkImpl sourceNetwork) {
	// Tarkistetaan että asetettava verkkoluokka on kelvollinen
        if (sourceNetwork != null) {
            this.sourceNetwork = sourceNetwork;
	    this.isServer = false;
	}
        else throw new RuntimeException("Source Network assignment failed.");
    }    
    
    /**
     * Luo uusi clienteja etsivä yhteyskäsittelijä.
     * @param searcher Käsittelijän luonut etsijä.
     */
    public SearchConnectionHandler(ClientSearch searcher) {
	// Tarkistetaan että asetettava etsijä on kelvollinen
	if (searcher != null) {
	    this.searcher = searcher;
	    this.isServer = true;
	}
        else throw new RuntimeException("Client searcher assignment failed.");
    }
    
    /**
     * On saatu uusi multicast-yhteys. Voidaan kutsua vain kerran.
     * Yhteyden pakettikäsittelijänä toimii luokka itse.
     * @return Uudesta yhteydestä tulevat paketit käsittelevä olio.
     * @param c Saatu yhteys.
     * @param source Olio, jossa yhteys on luotu.
     */
    // Toteuttaa ConnectionListener-rajapinnan newConnection-metodin
    public PacketListener newConnection(Connection c, Object source) {
	// Tarkistetaan että yhteys on kelvollinen eikä ole vielä asetettu.
	if ((c == null) || (!(c instanceof MulticastConnection)) ||
	    (connection != null))
	    throw new RuntimeException("Connection assignment failed.");
	else {
	    connection = c;
	    return this;
	}
    }
    
    /**
     * Yritetty yhteys epäonnistui.
     * Epäonnistumisesta ilmoitetaan joko käsittelijän luoneelle
     * verkolle tai etsijälle.
     * @param reason Epäonnistumisen syy.
     * @param source Yhteys, jossa yhteydenotto epäonnistui.
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
     * Yhteyden avaamisesta ilmoitetaan joko käsittelijän luoneelle
     * verkolle tai etsijälle.
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
     * Yhteydessä tapahtui virhe.
     * Virheestä ilmoitetaan joko käsittelijän luoneelle
     * verkolle tai etsijälle.
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
     * Lähetä paketti verkon yli.
     * @param data Lähetettävä olio, oltava ClientSearchPacket.
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
     * Käsittele verkosta saatu paketti.
     * Paketista haetaan serverin tiedot mikäli etsitään
     * servereitä, muuten pakettia ei käsitellä mitenkään.
     * @param data Vastaanotettu paketti, oltava
     * clientinetsintäpaketin sisältävä datagrammi.
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
     * Serveri löytyi verkosta. Se lisätään löytyneiden serverien listaan
     * mikäli sen osoitetta ei aikaisemmin ole tunnettu.
     * Serverin tiedot annetaan verkkoluokalle mikäli uusi serveri löytyi.
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
