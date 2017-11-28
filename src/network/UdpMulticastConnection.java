package network;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

/**
 * UDP Multicast-yhteys.
 * Voi sek‰ vastaanottaa ett‰ l‰hett‰‰ paketteja osoitteen ja portin
 * m‰‰rittelem‰n ryhm‰n sis‰ll‰.
 */
public class UdpMulticastConnection
    extends Thread implements MulticastConnection {

    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Yhteyden yhteyskuuntelija. */
    protected ClientConnectionListener ccl = null;
    /** Yhteyden pakettikuuntelija. */
    protected PacketListener pl = null;
    /** Vastaanotetaanko paketteja verkosta. */
    protected boolean running = false;
    
    /** Yhteyden ryhm‰osoite. */
    protected InetAddress group = null;
    /** K‰ytett‰v‰ portti. */
    protected int port = -1;
    
    /** Yhteyden UDP Multicast -socket. */
    protected MulticastSocket socket = null;
    /** L‰hetett‰v‰n datan puskurin sis‰lt‰v‰ tulostusvirta. */
    protected ByteArrayOutputStream outBytes = null;
    /** Tulostusvirta l‰hetett‰ville olioille. */
    protected ObjectOutputStream out = null;
    
    /** Luo UdpMulticastConnection-s‰ie. */
    public UdpMulticastConnection() {
	super("UdpMulticastClient");
    }
    
    /**
     * Aseta yhteyskuuntelija. Voidaan kutsua vain kerran.
     * @param ccl Yhteyskuuntelija.
     */
    public void setMulticastConnectionListener(
		ClientConnectionListener ccl) {
	// Tarkistetaan ett‰ pakettikuuntelija on kelvollinen,
	// eik‰ ole viel‰ asetettu
	if ((this.ccl == null) && (ccl != null))
	    this.ccl = ccl;
	else throw new RuntimeException(
		       "Setting MulticastConnectionListener failed.");
    }
    
    /**
     * Liity annetuun ryhm‰‰n.
     * Ryhm‰n osoite yritet‰‰n selvitt‰‰ annetun nimen perusteella.
     * Ennen kutsua on pakettikuuntelijan oltava asetettu.
     * @param group Ryhm‰ johon liityt‰‰n.
     * @param port Portti jota k‰ytet‰‰n, oltava &gt;0.
     * @throws NoListenerRegisteredException
     * Jos ei ole asetettu pakettikuuntelijaa.
     */
    // Toteuttaa MulticastConnection-rajapinnan connectTo-metodin
    // Kutsuu osoiteparametrin ottavaa connectTo-metodia
    public void connectTo(String group, int port)
	throws NoListenerRegisteredException {
	try {
	    connectTo(InetAddress.getByName(group), port);
	} catch(UnknownHostException e) {
	    log.error("Unable to resolve group name.", e);
	    throw new RuntimeException("No such group. " + e);
	}
    }
    
    /** 
     * Liity annettuun ryhm‰‰n.
     * Ennen kutsua on pakettikuuntelijan oltava asetettu.
     * @param group Ryhm‰n osoite, jota k‰ytet‰‰n tiedonv‰litykseen.
     * @param port Portti, jota k‰ytet‰‰n. Oltava &gt;0.
     * @throws NoListenerRegisteredException
     * Jos ei ole asetettu pakettikuuntelijaa.
     */
    // Asettaa osoitteen ja portin, ja k‰ynnist‰‰ s‰ikeen
    public void connectTo(InetAddress group, int port)
    	throws NoListenerRegisteredException {
	if (socket != null) // On jo liitytty ryhm‰‰n
	    throw new RuntimeException("Connection already exists.");
	else if (ccl == null)
	    throw new NoListenerRegisteredException(
		      "No multicast connection listener registered.");
	else {
	    this.group = group;
	    this.port = port;
	    this.start();
	}
    }
    
    /**
     * UdpMulticastConnection-s‰ikeen p‰‰silmukan sis‰lt‰v‰ metodi,
     * ei kutsuta eksplisiittisesti.
     * Luo tarvittavan socketin ja vastaanottaa verkosta saapuvia paketteja.
     * Vastaanotetut paketit eli oliot, joiden maksimikoko on
     * vastaanottopuskurin koko, annetaan pakettikuuntelijalle k‰sittelyyn.
     */
    // Verkosta saatu paketti tallennetaan tavupuskuriin,
    // josta luetaan vastaanotettu olio
    public void run() {
	try {
	    socket = new MulticastSocket(port);
	    socket.joinGroup(group);
	    byte[] buf = new byte[socket.getReceiveBufferSize()];
	    pl = ccl.newConnection(this, this);
	    running = true;
	    ccl.connectionOpened(this);
	    log.info("Multicast connection opened.");
	    Object data;
	    DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    while (running) {
		try {
		    socket.receive(packet);
		    pl.handlePacket(packet);
		} catch (IOException e) {
		    if (running) {
			log.error("Error receiving packet while running.", e);
			pl.connectionError("Error receiving packet. "
					   + e.toString(), this);
		    }
		    else log.warn("Error receiving packet, not running.", e);
		}
	    }
	} catch (IOException e) {
	    log.error("Multicast connection failed.", e);
	    ccl.connectionFailed("Multicast connection failed. "
				 + e.toString(), this);
	}
    }
    
    /**
     * L‰het‰ paketti verkon yli.
     * Ei tee mit‰‰n, jos yhteytt‰ ei ole avattu tai se on katkaistu.
     * @param data L‰hetett‰v‰ olio. Olion maksimikoko on l‰hetyspuskurin koko.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (running) {
	    try {
		outBytes = new ByteArrayOutputStream();
		out = new ObjectOutputStream(outBytes);
		out.writeObject(data);
		byte[] buf = outBytes.toByteArray();
		DatagramPacket packet =
		    new DatagramPacket(buf, buf.length, group, port);
		socket.send(packet);
	    } catch (IOException e) {
		if (running) {
		    log.error("Error sending packet while running.", e);
		    pl.connectionError("Error sending packet. "
				       + e.toString(), this);
		}
		else log.warn("Error sending packet, not running.", e);
	    }
	}
	else throw new RuntimeException(
		       "Unable to send packet, connection closed.");
    }
    
    // Toteuttaa Connection-rajapinnan disconnect-metodin.
    // Lopettaa run-metodin silmukan, mik‰ sulkee yhteyden ja
    // pys‰ytt‰‰ s‰ikeen ajon.
    public void disconnect() {
	log.info("Multicast disconnect called.");
	if (running) {
	    running = false;
	    try {
		if (out != null)
		    out.close();
		socket.leaveGroup(group);
		socket.close();
		log.info("Multicast connection closed.");
	    } catch (IOException e) {
		log.error("Close multicast connection failed.", e);
		throw new RuntimeException("Close connection failed. " + e);
	    }
	}
    }
    
    // Toteuttaa Connection-rajapinnan isConnected-metodin
    public boolean isConnected() {
	return running;
    }

}
