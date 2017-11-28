package network;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

/**
 * TCP-protokollaa k‰ytt‰v‰ Server.
 * Kuuntelee uusia yhteydenottopyyntˆj‰ ja luo tarvittavat server-yhteydet.
 */
public class TcpServer extends Thread implements Server {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Serverin yhteyskuuntelija.  */
    protected ServerConnectionListener scl = null;
    /** Kuunnellaanko uusia yhteyksi‰. */
    protected boolean listening = false;
    
    /** Kuunneltava portti. */
    protected int port = -1;
    
    /** Serverin TCP-socket. */
    protected ServerSocket serverSocket = null;
    
    /** Luo TcpServer-s‰ie. */
    public TcpServer() {
	super("TcpServer");
    }
    
    // Toteuttaa Server-rajapinnan setServerConnectionListener-metodin
    public void setServerConnectionListener(ServerConnectionListener scl) {
	if (scl != null) // Yhteyskuuntelija ei ole kelvollinen
	    this.scl = scl;
	else throw new RuntimeException(
		       "Setting ServerConnectionListener failed.");
    }
    
    /**
     * Kuuntele jotain porttia.
     * Ennen kutsua pit‰‰ kuuntelijan olla lis‰ttyn‰.
     * Kuuntelu voidaan aloittaa vain kerran.
     * @throws NoListenerRegisteredException Jos ei ole rekisterˆity
     * tulevia yhteyksi‰ k‰sittelev‰‰ kuuntelijaa.
     * @param port Kuunneltava portti. Oltava &gt;0.
     */
    // Toteuttaa Server-rajapinnan listenPort-metodin
    // Asettaa osoitteen ja portin, ja k‰ynnist‰‰ s‰ikeen
    public void listenPort(int port) throws NoListenerRegisteredException {
	if (serverSocket != null) // Kuuntelu on jo kerran aloitettu
	    throw new RuntimeException("Already listening.");
	else if (scl == null)
	    throw new NoListenerRegisteredException(
		      "No server connection listener registered.");
	else {
	    this.port = port;
	    this.start();
	}
    }
    
    /** 
     * TcpServer-s‰ikeen p‰‰silmukan sis‰lt‰v‰ metodi,
     * ei kutsuta eksplisiittisesti.
     * Luo tarvittavan socketin ja vastaanottaa uusia yhteyksi‰,
     * joille luodaan omat socketit.
     */
    public void run() {
	try {
	    serverSocket = new ServerSocket(port);
	    listening = true;
	    scl.listeningStarted(this);
	    log.info("Server started.");
	    int count = 0;
	    while (listening) {
		try {
		    new TcpServerConnection(
			serverSocket.accept(), this, count++);
		} catch (SocketException e) {
		    if (listening) {
			log.warn("Error when listening to port.");
			listening = false;
			throw e;
		    }
		    else log.warn("Error when listening to port, "
				  + "not running.", e);
		}
	    }
	} catch (IOException e) {
	    log.error("Failed to listen.", e);
	    scl.listeningFailed("Failed to listen/connect. "
				+ e.toString(), this);
	}
    }
    
    // Toteuttaa Server-rajapinnan stopPortListening-metodin
    // Lopettaa run-metodin silmukan
    public void stopPortListening() {
	log.info("Stop port listening called.");
	if (listening) {
	    listening = false;
	    try {
		serverSocket.close();
		log.info("Server socket closed.");
	    } catch (IOException e) {
		log.error("Close server socket failed.", e);
		throw new RuntimeException("Stop listening failed. " + e);
	    }
	}
    }
    
    /**
     * Serverin yhteyss‰ikeen toteuttava aliluokka.
     * Vastaanottaa olioita verkosta, ja l‰hett‰‰ niit‰ pyydett‰ess‰.
     * Vastaanotetut paketit annetaan pakettikuuntelijalle jatkok‰sittelyyn.
     */
    private class TcpServerConnection extends Thread implements Connection {
	
	/** Olio, jossa yhteyss‰ie on luotu. */
	protected Object creator;
	/** Yhteyss‰ikeen pakettikuuntelija */
	protected PacketListener pl = null;
	
	/** Yhteyss‰ikeen TCP-socket. */
	protected Socket socket = null;
	/** Tulostusvirta l‰hetett‰ville olioille. */
	protected ObjectOutputStream out = null;
	/** Syˆtevirta vastaanotettaville olioille. */
	protected ObjectInputStream in = null;
	
	/** Vastaanotetaanko paketteja verkosta. */
	protected boolean running = false;
	
	/**
	 * Luo uusi yhteyss‰ie.
	 * @param socket K‰ytett‰v‰ TCP-socket.
	 * @param creator Olio, jossa yhteyss‰ie on luotu
	 (tavallisesti Server-s‰ie)
	 */
	public TcpServerConnection(Socket socket, Object creator, int count) {
	    super("TcpServerConnection " + count);
	    this.creator = creator;
	    this.socket = socket;
	    try {
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	    } catch (IOException e) { 
		log.error("Server connection creation failed.", e);
		throw new RuntimeException(
			  "TcpServerConnection creation failed." + e);
	    }
	    this.start();
	}
	
	/** 
	 * TcpServerConnection-s‰ikeen p‰‰silmukan sis‰lt‰v‰ metodi,
	 * ei kutsuta eksplisiittisesti.
	 * Asettaa pakettikuuntelijan ja vastaanottaa
	 * verkosta saapuvia paketteja.
	 */
	public void run() {
	    pl = scl.newConnection(this, creator);
	    running = true;
	    log.info("Server connection opened.");
	    Object packet;
	    while (running) {
		try {
		    packet = in.readObject();
		    pl.handlePacket(packet);
		} catch (ClassNotFoundException e) {
		    log.warn("Packet with unknown class received.", e);
		    throw new RuntimeException(
			      "Packet with unknown class received: " + e);
		} catch(IOException e) {
		    if (running) {
			log.error("Error receiving packet while running.", e);
			pl.connectionError("Error receiving packet. " 
					   + e.toString(), this);
		    }
		    else log.warn("Error receiving packet, not running.", e);
		}
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
		    else log.warn("Error sending packet, not running.", e);
		}
	    else throw new RuntimeException(
			    "Unable to send packet, connection closed.");
	}
	
	// Toteuttaa Connection-rajapinnan disconnect-metodin.
	// Lopettaa run-metodin silmukan, mik‰ sulkee yhteyden ja
	// pys‰ytt‰‰ yhteyss‰ikeen ajon.
	public void disconnect() {
	    log.info("Server connection disconnect called.");
	    if (running) {
		running = false;
		try {
		    out.close();
		    in.close();
		    socket.close();
		    log.info("Server connection closed.");
		} catch (IOException e) {
		    log.error("Close server connection failed.", e);
		    throw new RuntimeException(
			      "Close connection failed. " + e);
		}
	    }
	}
	
	// Toteuttaa Connection-rajapinnan isConnected-metodin
	public boolean isConnected() {
	    return running;
	}
    }
   
}
