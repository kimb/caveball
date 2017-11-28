package network;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Clienteja etsiv‰ s‰ie, jonka server k‰ynnist‰‰.
 * L‰hett‰‰ verkkoon serverin kuvauksen sis‰lt‰vi‰
 * etsint‰paketteja multicast-yhteyden yli.
 * Clientit vastaanottavat n‰it‰ paketteja etsiess‰‰n serveri‰,
 * ja voivat sitten ottaa yhteytt‰ serveriin.
 */
public class ClientSearch extends Thread {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Etsij‰n multicast-yhteys. */
    protected MulticastConnection mcc = null;
    /** Etsij‰n yhteyskuuntelija. */
    protected SearchConnectionHandler sHandler = null;
    /** Serverin kuvaus. */
    protected Serializable definition = null;
    /** Etsint‰paketti. */
    protected ClientSearchPacket packet = null;
    /** Ryhm‰ johon paketit l‰hetet‰‰n. */
    protected String group;
    /** Portti jota k‰ytet‰‰n. */
    protected int port = -1;
    /** Ollaanko luomassa yhteytt‰. */
    protected boolean connecting = false;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** L‰hetett‰vien pakettien aikav‰li. */
    protected final int interval = 5000;
    
    /**
     * Luo uusi etsij‰ ja k‰ynnist‰ multicast-yhteys.
     * @param definition Serverin kuvaus.
     * @param group Multicast-ryhm‰.
     * @param port K‰ytett‰v‰ portti.
     */
    public ClientSearch(Serializable definition, String group, int port) {
	super("ClientSearch");
	this.definition = definition;
	this.group = group;
	this.port = port;
	connecting = true;
	MulticastConnection mcc = new UdpMulticastConnection();
	sHandler = new SearchConnectionHandler(this);
	mcc.setMulticastConnectionListener(sHandler);
	mcc.connectTo(group, port);
    }
    
    /**
     * Kutsutaan kun clientien etsint‰ on k‰ynnistetty.
     * @param started Onnistuiko etsimisen k‰ynnistys.
     * @param source Olio jossa etsint‰ k‰ynnistyi.
     */
    public void clientSearchStarted(boolean started, Object source) {
	if (!connecting)
	    throw new RuntimeException("Not trying to connect. ");
	if (connected)
	    throw new RuntimeException("Not trying to connect: "
				       + "Already connected.");
	else {
	    connecting = false;
	    if (started) {
		connected = true;
		this.start();
	    }
	    else throw new RuntimeException("Client search failed.");
	}
    }
    
    /**
     * Yhteydess‰ tapahtui virhe. Etsint‰ pys‰ytet‰‰n.
     * @param message Virheen syy tai muu viesti.
     * @param source Olio josta virhe saadaan.
     */
    public void connectionError(String message, Object source) {
	log.warn("Connection error: " + message + source);
	this.stopClientSearch();
	throw new RuntimeException("Client search stopped. "
				   + message + source.toString());
    }
    
    /**
     * Lopeta clientien etsiminen ja katkaise yhteys.
     */
    public void stopClientSearch() {
	if (connected) {
	    connected = false;
	    if (packet != null)
		synchronized (packet) {
		    try {
			packet.notify();
		    } catch (Exception e) {
			log.warn(
			"Send packet interruption caused exception.", e);
		    }
		}
	    sHandler.disconnect();
	}
    }
    
    /**
     * Etsij‰s‰ikeen p‰‰silmukan sis‰lt‰v‰ metodi,
     * ei kutsuta eksplisiittisesti.
     * L‰hett‰‰ etsint‰paketteja.
     */
    public void run() {
	ClientSearchPacket packet = new ClientSearchPacket();
	packet.setData(definition);
	while (connected) {
	    sHandler.sendPacket(packet);
	    synchronized (packet) {
		try {
		    packet.wait(interval);
		} catch (Exception e) {
		    log.warn("Send packet waiting caused exception.", e);
		}   
	    }
	}
    }
}
