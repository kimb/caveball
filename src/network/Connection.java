package network;

/**
 * Yhteys verkon yli.
 */
public interface Connection {
    
    /**
     * Lähetä paketti verkon yli.
     * @param data Lähetettävä olio.
     */
    public void sendPacket(Object data);
    
    /**
     * Katkaise yhteys.
     */
    public void disconnect();
    
    /**
     * Onko yhteys auki.
     * @return Onko yhteys auki.
     */
    public boolean isConnected();

}
