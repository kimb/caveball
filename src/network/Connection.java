package network;

/**
 * Yhteys verkon yli.
 */
public interface Connection {
    
    /**
     * L�het� paketti verkon yli.
     * @param data L�hetett�v� olio.
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
