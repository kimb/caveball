package core;

import java.io.Serializable;

/**
 * Määrittelee verkon yli lähetettävän paketin. Ainoastaan vastaanotetuissa
 * paketeissa on getPing-metodi määriteltävä.
 */
public interface NetworkPacket extends Serializable {

	/**
	 * Paketin sisältö.
	 */
	public Serializable getData();

	/**
	 * Palauttaa vastaanotetun paketin viiveen lähetyshetkestä.
	 */
	public int getPing();

	/**
	 * Yhteys josta paketti on tullut.
	 */
	public Object getSource();

}
