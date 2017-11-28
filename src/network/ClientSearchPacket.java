package network;

import java.io.Serializable;

/**
 * Clientien etsimisessä käytettävä paketti.
 * Clienteja etsivä server lähettää paketin ryhmälle, ja
 * ryhmää kuunteleva client tunnistaa saapuneen paketin luokan.
 */
public class ClientSearchPacket implements Serializable {

    /** Etsintäpaketin sisältö. */
    protected Serializable data = null;
    
    /**
     * Palauttaa etsintäpaketin sisällön.
     * @return Paketin sisältämä data.
     */
    public Serializable getData() {
	return data;
    }
    
    /**
     * Aseta paketin sisältö.
     * @param data Lähetettävä data. Oltava Serializable.
     */
    public void setData(Serializable data) {
	this.data = data;
    }
    
}
