package network;

/**
 * Mahdollisia tapahtumia kun yritetään luoda yhteys.
 */
public interface ClientConnectionListener extends ConnectionListener {
    
    /**
     * Yritetty yhteys epäonnistui.
     * @param reason Epäonnistumisen syy.
     * @param source Client, jossa yhteydenotto epäonnistui.
     */
    public void connectionFailed(String reason, Object source);

    /**
     * Yhtydenotto onnistui.
     * @param source Luotu yhteys.
     */
    public void connectionOpened(Object source);

}
