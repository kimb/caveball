package network;

/**
 * Multicast-yhteys.
 * Vastaanotaa ja l‰hett‰‰ paketteja osoitteen ja portin
 * m‰‰rittelem‰n ryhm‰n sis‰ll‰.
 */
public interface MulticastConnection extends Connection {
    
    /**
     * Liity annetuun ryhm‰‰n. 
     * Ennen kutsua on asetettava ainakin yksi kuuntelija.
     * @param group Ryhm‰ johon liityt‰‰n.
     * @param port Portti jota k‰ytet‰‰n, oltava &gt;0.
     * @throws NoListenerRegisteredException
     * Jos ei ole asetettu yht‰k‰‰n pakettikuuntelijaa.
     */
    public void connectTo(String group, int port)
	throws NoListenerRegisteredException;
    
    /**
     * Aseta yhteyskuuntelija.
     * @param mcl Yhteyskuuntelija.
     */
    public void setMulticastConnectionListener(
		ClientConnectionListener ccl);
}
