package network;

/**
 * Pakettien kuuntelija.
 */
public interface PacketListener {
    
    /**
     * K�sittele verkosta saatu paketti.
     * @param data Vastaanotettu paketti.
     */
    public void handlePacket(Object data);

    /**
     * Yhteydess� tapahtui virhe.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    public void connectionError(String message, Object source);

}
