package network;

/**
 * Pakettien kuuntelija.
 */
public interface PacketListener {
    
    /**
     * Käsittele verkosta saatu paketti.
     * @param data Vastaanotettu paketti.
     */
    public void handlePacket(Object data);

    /**
     * Yhteydessä tapahtui virhe.
     * @param message Virheen syy tai muu viesti.
     * @param source Yhteys jossa virhe tapahtui.
     */
    public void connectionError(String message, Object source);

}
