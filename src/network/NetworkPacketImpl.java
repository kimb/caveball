package network;

import core.*;
import java.io.Serializable;

/**
 * Verkon yli l‰hetett‰v‰ paketti.
 * Sis‰lt‰‰ l‰hetett‰v‰n datan sek‰ tiedot l‰hetysajasta ja viiveest‰.
 */
public class NetworkPacketImpl implements core.NetworkPacket {
    
    /** L‰hetetett‰v‰ data. */
    protected Serializable data;
    /** Aika, jolloin paketti on luotu. */
    protected int time = 0;
    /** Matkalla syntynyt viive. */
    protected int ping = 0;
    /** Yhteys josta paketti on tullut. */
    protected Object source = null;
    
    // Toteuttaa NetworkPacketin getData-metodin
    public Serializable getData() {
	return data;
    }
    // Toteuttaa NetworkPacketin getPing-metodin
    public int getPing() {
	return ping;
    }
    // Toteuttaa NetworkPacketin getSource-metodin
    public Object getSource() {
	return source;
    }
    /** Paketin luomisaika (l‰hett‰j‰n kellon mukaan). */
    public int getTime() {
	return time;
    }
    
    /**
     * Aseta l‰hetett‰v‰ data. Asetetetaan ennen paketin l‰hett‰mist‰.
     * @param data L‰hetett‰v‰ data, oltava Serializable.
     */
    public void setData(Serializable data) {
	this.data = data;
    }
    /**
     * Aseta luomisaika. Aika asetetaan ennen paketin l‰hett‰mist‰.
     * @param time Aika, jolloin paketti on luotu.
     */
    public void setTime(int time) {
	this.time = time;
    }
    /**
     * Aseta paketin yhteys. Aseteaan kun paketti on vastaanotettu.
     * @param source Yhteys josta paketti on tullut.
     */
    public void setSource(Object source) {
	this.source = source;
    }
    /**
     * Aseta syntynyt viive. Ping asetetaan kun paketti on vastaanotettu.
     * @param ping Paketille laskettu viive. 
     */
    public void setPing(int ping) {
	this.ping = ping;
    }
    
}
