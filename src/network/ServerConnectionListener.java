package network;

/**
 * Mahdollisia tapahtumia kun vastaanotetaan yhteyksi�.
 */
public interface ServerConnectionListener extends ConnectionListener {
    
    /**
     * Kuuntelu ep�onnistui.
     * @param reason Ep�onnistumisen syy.
     * @param source Server, jossa kuuntelu ep�onnistui.
     */
    public void listeningFailed(String reason, Object source);
    
    /**
     * Kuuntelun aloittaminen onnistui.
     * @param source Luotu serveri.
     */
    public void listeningStarted(Object source);
    
}
