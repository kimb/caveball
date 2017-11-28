package network;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

/**
 * TCP-protokollaa k‰ytt‰v‰ Client-yhteys.
 * Vastaanottaa olioita verkosta, ja l‰hett‰‰ niit‰ pyydett‰ess‰.
 * Vastaanotetut paketit annetaan pakettikuuntelijalle jatkok‰sittelyyn.
 */
public class TcpClient extends Thread implements Client {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Clientin yhteyskuuntelija */
    protected ClientConnectionListener ccl = null;
    /** Clientin pakettikuuntelija */
    protected PacketListener pl = null;
    /** Vastaanotetaanko paketteja verkosta */
    protected boolean running = false;
    
    /** Serverin verkko-osoite */
    protected InetAddress host = null;
    /** K‰ytett‰v‰ portti */
    protected int port = -1;
    
    /** Clientin TCP-socket */
    protected Socket socket = null;
    
    /** Tulostusvirta l‰hetett‰ville olioille. */
    protected ObjectOutputStream out = null;
    /** Syˆtevirta vastaanotettaville olioille. */
    protected ObjectInputStream in = null;
    
    /** Luo TcpClient-s‰ie. */
    public TcpClient() {
        super("TcpClient");
    }
    
    // Toteuttaa Client-rajapinnan setClientConnectionListener-metodin
    public void setClientConnectionListener(ClientConnectionListener ccl) {
	if (ccl != null) // Yhteyskuuntelija ei ole kelvollinen
	    this.ccl = ccl;
	else throw new RuntimeException(
		       "Setting ClientConnectionListener failed.");
    }
    
    /**
     * Yrit‰ selvitt‰‰ annettua koneennime‰ vastaava osoite
     * ja avata yhteys annettuun koneeseen.
     * Ennen kutsua pit‰‰ kuuntelijan olla lis‰ttyn‰.
     * Yhteys voidaan avata vain kerran.
     * @throws NoListenerRegisteredException Jos ei ole rekisterˆity
     * saadun yhteyden k‰sittelev‰‰ kuuntelijaa.
     * @param host Is‰nn‰n osoite, johon kytkeydyt‰‰n.
     * @param port Portti johon kytkeydyt‰‰n, oltava &gt;0.
     */
    // Toteuttaa Client-rajapinnan connectTo-metodin
    // Kutsuu osoiteparametrin ottavaa connectTo-metodia
    public void connectTo(String host, int port)
	throws NoListenerRegisteredException {
	try {
	    connectTo(InetAddress.getByName(host), port);
	} catch (UnknownHostException e) {
	    log.error("Unable to resolve host name.", e);
	    throw new RuntimeException("No such host. " + e);
	}
    }
    
    /**
     * Yrit‰ avata yhteys annettuun koneeseen.
     * Ennen kutsua pit‰‰ kuuntelijan olla lis‰ttyn‰.
     * Yhteys voidaan avata vain kerran.
     * @throws NoListenerRegisteredException Jos ei ole rekisterˆity
     * saadun yhteyden k‰sittelev‰‰ kuuntelijaa.
     * @param host Is‰nn‰n osoite, johon kytkeydyt‰‰n.
     * @param port Portti johon kytkeydyt‰‰n, oltava &gt;0.
     */
    // Asettaa osoitteen ja portin, ja k‰ynnist‰‰ s‰ikeen
    public void connectTo(InetAddress host, int port)
	throws NoListenerRegisteredException {
	if (socket != null) // Yhteys on jo kerran avattu
	    throw new RuntimeException(
		      "Connection already exists.");
	else if (ccl == null)
	    throw new NoListenerRegisteredException(
		      "No client connection listener registered.");
	else {
	    this.host = host;
	    this.port = port;
	    this.start();
	}
    }
    
    /** 
     * TcpClient-s‰ikeen p‰‰silmukan sis‰lt‰v‰ metodi,
     * ei kutsuta eksplisiittisesti.
     * Luo tarvittavan socketin ja vastaanottaa verkosta saapuvia paketteja.
     */
    public void run() {
	try {
	    socket = new Socket(host, port);
	    out = new ObjectOutputStream(socket.getOutputStream());
	    in = new ObjectInputStream(socket.getInputStream());
	    pl = ccl.newConnection(this, this); // Yhteyden pakettikuuntelija
	    running = true;
	    ccl.connectionOpened(this);
	    log.info("Client connection opened.");
	    Object packet;
	    while (running) {
		try {
		    packet = in.readObject();
		    pl.handlePacket(packet);
		} catch (ClassNotFoundException e) {
		    log.warn("Packet with unknown class received.", e);
		    throw new RuntimeException(
			      "Packet with unknown class received: " + e);
		} catch (IOException e) {
		    if (running) {
			log.error("Error receiving packet while running.", e);
			pl.connectionError("Error receiving packet. " 
					   + e.toString(), this);
		    }
		    else log.warn("Error receiving packet, not running.", e);
		}
	    }
	} catch(IOException e) {
	    log.error("Failed to connect client.", e);
	    ccl.connectionFailed("Failed to connect. " + e.toString(), this);
	}
    }
    
    /**
     * L‰het‰ paketti verkon yli.
     * Ei tee mit‰‰n, jos yhteytt‰ ei ole avattu tai se on katkaistu.
     * @param data L‰hetett‰v‰ olio.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	// Tarkistetaan onko yhteys auki
	if (running)
	    try {
		synchronized(out) {
		    out.writeObject(data);
		    out.reset();
		}
	    } catch (IOException e) {
		if (running) {
		    log.error("Error sending packet while running.", e);
		    pl.connectionError("Error sending packet. "
				       + e.toString(), this);
		}
		else log.warn("Error sending packet, nor running.", e);
	    }
	else throw new RuntimeException(
		       "Unable to send packet, connection closed.");
    }
    
    // Toteuttaa Connection-rajapinnan disconnect-metodin
    // Lopettaa run-metodin silmukan, mik‰ sulkee yhteyden ja
    // pys‰ytt‰‰ s‰ikeen ajon.
    public void disconnect() {
	log.info("Client disconnect called.");
	if (running) {
	    running = false;
	    try {
		out.close();
		in.close();
		socket.close();
		log.info("Client connection closed.");
	    } catch (IOException e) {
		log.error("Close client connection failed.", e);
		throw new RuntimeException("Close connection failed. " + e);
	    }
	}
    }
    
    // Toteuttaa Connection-rajapinnan isConnected-metodin
    public boolean isConnected() {
	return running;
    }
    
}
