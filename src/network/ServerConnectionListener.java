package network;

/**
 * Mahdollisia tapahtumia kun vastaanotetaan yhteyksiä.
 */
public interface ServerConnectionListener extends ConnectionListener {
    
    /**
     * Kuuntelu epäonnistui.
     * @param reason Epäonnistumisen syy.
     * @param source Server, jossa kuuntelu epäonnistui.
     */
    public void listeningFailed(String reason, Object source);
    
    /**
     * Kuuntelun aloittaminen onnistui.
     * @param source Luotu serveri.
     */
    public void listeningStarted(Object source);
    
}
