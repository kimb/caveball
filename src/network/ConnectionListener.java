package network;

/**
 * Uusien yhteyksien kuuntelija.
 */
public interface ConnectionListener {
    
    /**
     * On saatu uusi yhteys.
     * @return Uudesta yhteydestä tulevat paketit käsittelevä olio.
     * @param c Saatu yhteys.
     * @param source Olio, jossa yhteys on luotu.
     */
    public PacketListener newConnection(Connection c, Object source);
    
}
