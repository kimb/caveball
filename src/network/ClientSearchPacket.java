package network;

import java.io.Serializable;

/**
 * Clientien etsimisess� k�ytett�v� paketti.
 * Clienteja etsiv� server l�hett�� paketin ryhm�lle, ja
 * ryhm�� kuunteleva client tunnistaa saapuneen paketin luokan.
 */
public class ClientSearchPacket implements Serializable {

    /** Etsint�paketin sis�lt�. */
    protected Serializable data = null;
    
    /**
     * Palauttaa etsint�paketin sis�ll�n.
     * @return Paketin sis�lt�m� data.
     */
    public Serializable getData() {
	return data;
    }
    
    /**
     * Aseta paketin sis�lt�.
     * @param data L�hetett�v� data. Oltava Serializable.
     */
    public void setData(Serializable data) {
	this.data = data;
    }
    
}
