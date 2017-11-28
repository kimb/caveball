package network;

/**
 * Verkon yli luotavan yhteyden Server-osa.
 * Server kuuntelee Client-osien yhteyspyyntöjä ja luo uusia yhteyksiä.
 */
public interface Server {
    /**
     * Kuuntele jotain porttia.
     * @throws NoListenerRegisteredException Jos ei ole rekisteröity
     * ainakin yhtä tulevia yhteyksiä käsittelevää kuuntelijaa.
     * @param port Kuunneltava portti. Oltava &gt;0.
     */
    public void listenPort(int port) 
	throws NoListenerRegisteredException;
    
    /**
     * Lopeta portin kuunteleminen.
     */
    public void stopPortListening();
    
    /**
     * Aseta yhteyskuuntelija.
     * @param scl Yhteyskuuntelija.
     */
    public void setServerConnectionListener(ServerConnectionListener scl);
    
}
