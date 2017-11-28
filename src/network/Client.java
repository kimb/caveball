package network;

/**
 * Verkon yli luotavan yhteyden Client-osa.
 * Client ottaa yhteyden palvelimena toimivaan tietokoneen Server-osaan.
 */
public interface Client extends Connection {
    
    /**
     * Yrit‰ avata yhteys annettuun koneeseen. 
     * Ennen kutsua pit‰‰ ainakin yksi kuuntelija olla lis‰ttyn‰.
     * @param host Is‰nt‰ johon kytkeydyt‰‰n.
     * @param port Portti johon kytkeydyt‰‰n, oltava &gt;0.
     * @throws NoListenerRegisteredException
     * Jos ei ole asetettu yht‰k‰‰n yhteyskuuntelijaa.
     */
    public void connectTo(String host, int port)
	throws NoListenerRegisteredException;
    
    /**
     * Aseta yhteyskuuntelija.
     * @param ccl Yhteyskuuntelija.
     */
    public void setClientConnectionListener(ClientConnectionListener ccl);
}
