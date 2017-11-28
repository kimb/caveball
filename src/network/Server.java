package network;

/**
 * Verkon yli luotavan yhteyden Server-osa.
 * Server kuuntelee Client-osien yhteyspyynt�j� ja luo uusia yhteyksi�.
 */
public interface Server {
    /**
     * Kuuntele jotain porttia.
     * @throws NoListenerRegisteredException Jos ei ole rekister�ity
     * ainakin yht� tulevia yhteyksi� k�sittelev�� kuuntelijaa.
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
