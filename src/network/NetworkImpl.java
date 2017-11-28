package network;

import core.*;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Verkon toteuttava luokka.
 * Toimii rajapintana verkon ja muun ohjelman v‰lill‰.
 * Ottaa vastaan kutsuja Controllerilta, sek‰
 * k‰sittelee verkon muilta luokilta tulleita kutsuja.
 */
public class NetworkImpl implements core.Network {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Ryhm‰ josta etsit‰‰n servereit‰. */
    protected String group = "230.0.0.1";
    /** Portti jota k‰ytet‰‰n multicast-tietoliikenteeseen. */
    protected int mPort = 6500;
    /** Portti jota k‰ytet‰‰n tietoliikenteeseen. */
    protected int port = 6501;
    
    /** Kuvaus, kun toimitaan palvelimena, */
    protected Serializable serverDefinition = null;
    
    /** Verkon serveriyhteysk‰sittelij‰. */
    protected ServerConnectionHandler scHandler = null;
    /** Verkon clientyhteysk‰sittelij‰. */
    protected ClientConnectionHandler ccHandler = null;
    /** Verkon yhteysk‰sittelij‰ kun etsit‰‰n servereit‰. */
    protected SearchConnectionHandler searchHandler = null;
    /** Verkon client-etsij‰. */
    protected ClientSearch searcher = null;
    /** Verkon verkkopakettikuuntelija. */
    protected NetworkPacketListener npListener = null;
    
    /** Toimitaanko serverin‰. */
    protected boolean isServer = false;
    /** Ollaanko kytkettyn‰ verkkoon. */
    protected boolean connected = false;
    /** Ollaanko kytkeytym‰ss‰ verkkoon. */
    protected boolean connecting = false;
    /** Ollaanko etsim‰ss‰ palvelimia. */
    protected boolean searching = false;
    
    // Toteuttaa Network-rajapinnan setNetworkPacketListener-metodin
    public void setNetworkPacketListener(NetworkPacketListener npListener) {
	if (searching)
	    this.stopServerSearch();
	this.npListener = npListener;
    }
    
    // Toteuttaa Network-rajapinnan searchForServers-metodin
    public void searchForServers() {
	// Tarkistetaan ett‰ yhteys ei ole auki, tai sit‰ ei olla yritt‰m‰ss‰,
	// ja ett‰ ei jo etsit‰ servereit‰
	if (connected || connecting)
	    throw new RuntimeException("Already connected or connecting.");
	else if (searching)
	    throw new RuntimeException("Already searching.");
	else {
	    connecting = true;
	    MulticastConnection mcc = new UdpMulticastConnection();
	    searchHandler = new SearchConnectionHandler(this);
	    mcc.setMulticastConnectionListener(searchHandler);
	    mcc.connectTo(group, mPort);
	}
    }
    
    // Toteuttaa Network-rajapinnan startServer-metodin
    public void startServer(Serializable definition) {
	// Tarkistetaan ett‰ yhteys ei ole auki, tai sit‰ ei olla yritt‰m‰ss‰
	if (connected || connecting)
	    throw new RuntimeException("Already connected or connecting.");
	else { 
	    if (searching)
		this.stopServerSearch();
	    connecting = true;
	    isServer = true;
	    this.serverDefinition = definition;
	    Server server = new TcpServer();
	    scHandler = new ServerConnectionHandler(this, server);
	    server.setServerConnectionListener(scHandler);
	    server.listenPort(port);
	}
    }
    
    // Toteuttaa Network-rajapinnan connectTo-metodin
    public void connectTo(String serverAddress) {
	// Tarkistetaan ett‰ yhteys ei ole auki, tai sit‰ ei olla yritt‰m‰ss‰
	if (connected || connecting)
	    throw new RuntimeException("Already connected or connecting.");
	else {
	    if (searching)
		this.stopServerSearch();
	    connecting = true;
	    Client client = new TcpClient();
	    ccHandler = new ClientConnectionHandler(this);
	    client.setClientConnectionListener(ccHandler);
	    client.connectTo(serverAddress, port);
	}
    }
    
    // Toteuttaa Network-rajapinnan sendPacket-metodin
    // L‰hett‰‰ paketin kaikkiin yhteyksiin
    public void sendPacket(Serializable data) {
	// Tarkistetaan ett‰ yhteys on auki
	if (connected)
	    if (isServer)
		scHandler.sendPacket(data);
	    else ccHandler.sendPacket(data);
	else throw new RuntimeException(
		       "Unable to send packet, not connected: "
		       + data.toString());
    }
    
    // Toteuttaa Network-rajapinnan sendPacket-metodin
    // L‰hett‰‰ paketin tietty‰ yhteytt‰ pitkin
    public void sendPacket(Serializable data, Object destination) {
	// Tarkistetaan ett‰ yhteys on auki ja ett‰ se on kelvollinen
	if (connected)
	    if (destination instanceof PacketHandler)
		((Connection)destination).sendPacket(data);
	    else throw new RuntimeException(
			   "Unable to send packet, bad destination: "
			   + data.toString() + destination.toString());
	else throw new RuntimeException(
		       "Unable to send packet, not connected: "
		       + data.toString() + destination.toString());
    }
    
    // Toteuttaa Network-rajapinnan disconnect-metodin
    public void disconnect() {
	log.info("Disconnecting network.");
	if (searching)
	    this.stopServerSearch();
	if (connected) {
	    connected = false;
	    if (isServer) {
		scHandler.disconnect();
		searcher.stopClientSearch();
		scHandler = null;
		searcher = null;
		isServer = false;
	    }
	    else {
		ccHandler.disconnect();
		ccHandler = null;
	    }
	}
    }
    
    /**
     * K‰sittelee verkosta tulleen paketin.
     * Paketti annetaan verkkopakettikuuntelijalle.
     * @param packet Vastaanotettu verkkopaketti.
     */
    public void handlePacket(NetworkPacket packet) {
	npListener.handlePacket(packet);
    }
    
    /**
     * Kutsutaan kun serverin etsint‰ on k‰ynnistetty.
     * @param started Onnistuiko etsinn‰n aloitus.
     * @param source Olio jossa etsint‰ k‰ynnistyi.
     */
    public void serverSearchStarted(boolean started, Object source) {
	connecting = false;
	if (!started)
	    throw new RuntimeException("Server search failed. "
				       + source.toString());
	else {
	    searching = true;
	}
    }
    
    /**
     * Serveri lˆytyi. Annetaan tieto verkkopakettikuuntelijalle.
     * @param address Serverin osoite.
     * @param data Verkosta tullut etsint‰paketti, oltava
     * ClientSearchPacket.
     */
    public void serverFound(String address, Object data) {
	if (!(data instanceof ClientSearchPacket))
	    throw new RuntimeException(
		      "Packet is not a client search packet. "
		      + ((ClientSearchPacket)data).toString());
	else {
	    log.info("Server found: "
		     + address + ((ClientSearchPacket)data).getData()); //
	    npListener.serverFound(address,
				   ((ClientSearchPacket)data).getData());
	}
    }
    
    /**
     * Lopeta palvelimien etsiminen ja katkaise etsint‰yhteys.
     */
    public void stopServerSearch() {
	if (searching) {
	    searching = false;
	    searchHandler.disconnect();
	    searchHandler = null;
	}
    }
    
    /**
     * Kutsutaan kun server on k‰ynnistetty ja
     * uusia yhteyksi‰ ollaan valmiita vastaanottamaan.
     * @param started Onnistuiko serverin k‰ynnist‰minen.
     * @param source K‰ynnistetty serveri.
     */
    public void serverStarted(boolean started, Object source) {
	if (!connecting)
	    throw new RuntimeException("Not trying to connect. ");
	else if (connected)
	    throw new RuntimeException("Not trying to connect: "
				       + "Already connected.");
	else if (!isServer)
	    throw new RuntimeException("Not trying to connect: "
				       + "Not a server.");
	else {
	    connecting = false;
	    if (started) {
		connected = true;
		searcher = new ClientSearch(serverDefinition, group, mPort);
		npListener.connectionEstablished(source); // FIXME: Doc
	    }
	    else {
		isServer = false;
		npListener.connectionAttemptFailed("Unable to start server.",
						    source);
	    }
	}
    }
    
    /**
     * Kutsutaan kun client-yhteys on avattu.
     * @param opened Onnistuiko yhteyden avaaminen.
     * @param source Avattu yhteys.
     */
    public void connectionOpened(boolean opened, Object source) {
	if (!connecting)
	    throw new RuntimeException("Not trying to connect. ");
	else if (connected)
    	    throw new RuntimeException("Not trying to connect: "
				       + "Already connected.");
	else if (isServer)
	    throw new RuntimeException("Not trying to connect: "
				       + "Not a client.");
	else {
	    connecting = false;
	    if (opened) {
		connected = true;
		npListener.connectionEstablished(source);
	    }
	    else npListener.connectionAttemptFailed(
			    "Unable to open connection. ", source);
	}
    }
    
    /**
     * Yhteydess‰ tai yhteyksien kuuntelussa tapahtui virhe.
     * Yhteys katkaistaan.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    public void connectionError(String message, Object source) {
	log.warn("Connection error: " + message + source);
	if (source instanceof Connection) {
	    log.info("Disconnecting: " + source.toString());
	    ((Connection)source).disconnect();
	    if (source instanceof SearchConnectionHandler)
		throw new RuntimeException(
			  "Server search connection error. "
			  + message + source.toString());
	    else { 
		npListener.connectionLost(message, this);
	    }
	}
	if (source instanceof Server) {
	    throw new RuntimeException(
		      "Server error. " + message + source.toString());
	}
    }
    
}
