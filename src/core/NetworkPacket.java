package core;

import java.io.Serializable;

/**
 * M��rittelee verkon yli l�hetett�v�n paketin. Ainoastaan vastaanotetuissa
 * paketeissa on getPing-metodi m��ritelt�v�.
 */
public interface NetworkPacket extends Serializable {

	/**
	 * Paketin sis�lt�.
	 */
	public Serializable getData();

	/**
	 * Palauttaa vastaanotetun paketin viiveen l�hetyshetkest�.
	 */
	public int getPing();

	/**
	 * Yhteys josta paketti on tullut.
	 */
	public Object getSource();

}
