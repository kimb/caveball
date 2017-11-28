package network;

/**
 * Mahdollisia tapahtumia kun yritet‰‰n luoda yhteys.
 */
public interface ClientConnectionListener extends ConnectionListener {
    
    /**
     * Yritetty yhteys ep‰onnistui.
     * @param reason Ep‰onnistumisen syy.
     * @param source Client, jossa yhteydenotto ep‰onnistui.
     */
    public void connectionFailed(String reason, Object source);

    /**
     * Yhtydenotto onnistui.
     * @param source Luotu yhteys.
     */
    public void connectionOpened(Object source);

}
