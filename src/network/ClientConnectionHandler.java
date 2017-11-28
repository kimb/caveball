package network;

import core.*;

/**
 * Client-yhteyden k‰sittelij‰.
 * Toimii yhteyskuuntelijana ja asettaa yhteydelle pakettikuuntelijan.
 * Voi myˆs l‰hett‰‰ paketteja yhteyden kautta sek‰ katkaista sen.
 */
public class ClientConnectionHandler
    implements ClientConnectionListener, ConnectionHandler {
    
    /** Client-yhteys. */
    protected Connection connection = null;
    /** Yhteyden pakettikuuntelija */
    protected PacketHandler pHandler = null;
    /** Yhteysk‰sittelij‰n luonut verkkoluokka. */
    protected NetworkImpl sourceNetwork = null;
    /** Onko yhteys auki. */
    protected boolean connected = false;
    /** Kertoo ett‰ kyseess‰ ei ole palvelinyhteyskuuntelija */
    protected final boolean isServer = false;
    
    /**
     * Luo uusi Client-yhteyksien k‰sittelij‰.
     * @param sourceNetwork Yhteysk‰sittelij‰n luonut verkkoluokka.
     */
    public ClientConnectionHandler(NetworkImpl sourceNetwork) {
	// Tarkistetaan ett‰ asetettava verkkoluokka on kelvollinen
        if (sourceNetwork != null)
            this.sourceNetwork = sourceNetwork;
        else throw new RuntimeException("Source Network assignment failed.");
    }
    
    /**
     * On saatu uusi yhteys. Voidaan kutsua vain kerran.
     * @return Uudesta yhteydest‰ tulevat paketit k‰sittelev‰ olio.
     * @param c Saatu yhteys, oltava Client-tyyppinen.
     * @param source Olio, jossa yhteys on luotu.
     */
    // Toteuttaa ConnectionListener-rajapinnan newConnection-metodin
    public PacketListener newConnection(Connection c, Object source) {
	// Tarkistetaan ett‰ yhteys on kelvollinen eik‰ ole viel‰ asetettu.
	if ((c == null) || (!(c instanceof Client)) || (connection != null))
	    throw new RuntimeException("Connection assignment failed.");
	else {
	    connection = c;
	    pHandler = new PacketHandler(c, this, isServer);
	    return pHandler;
	}
    }
    
    /**
     * Yritetty yhteys ep‰onnistui.
     * Ilmoitetaan eteenp‰in verkkoluokalle.
     * @param reason Ep‰onnistumisen syy.
     * @param source Client, jossa yhteydenotto ep‰onnistui.
     */
    // Toteuttaa ClientConnectionListener-rajapinnan connectionFailed-metodin
    public void connectionFailed(String reason, Object source) {
	sourceNetwork.connectionOpened(false, this);
    }
    
    /**
     * Yhtydenotto onnistui.
     * Ilmoitetaan eteenp‰in verkkoluokalle.
     * @param source Luotu yhteys.
     */
    // Toteuttaa ClientConnectionListener-rajapinnan connectionOpened-metodin
    public void connectionOpened(Object source) {
	connected = true;
	sourceNetwork.connectionOpened(true, this);
    }
    
    /**
     * Yhteydess‰ tapahtui virhe.
     * Ilmoitetaan eteenp‰in verkkoluokalle.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    // Toteuttaa PacketListener-rajapinnan connectionError-metodin
    public void connectionError(String message, Object source) {
	sourceNetwork.connectionError(message, this);
    }
    
    /**
     * K‰sittele verkosta saatu paketti.
     * Paketti siirret‰‰n verkkoluokan k‰sitelt‰v‰ksi.
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
     * L‰het‰ paketti verkon yli pakettik‰sittelij‰n avulla.
     * @param data L‰hetett‰v‰ olio.
     */
    // Toteuttaa Connection-rajapinnan sendPacket-metodin
    public void sendPacket(Object data) {
	if (connected)
	    pHandler.sendPacket(data);
	else throw new RuntimeException(
		       "Unable to send packet, not connected: "
		       + data.toString());
    }
    
    /**
     * Katkaise yhteys.
     * Pyyt‰‰ pakettik‰sittelij‰‰ katkaisemaan yhteyden.
     */
    // Toteuttaa Connection-rajapinnan disconnect-metodin
    public void disconnect() {
	if (connected) {
	    connected = false;
	    pHandler.disconnect();
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
