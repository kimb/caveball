package core;

import java.awt.Color;
import java.io.*;

/**
 * M‰‰rittelee pelaajan asetukset. Toimii pelkk‰n‰ tietovarastona.
 */
public class PlayerProperties implements Serializable {

	/** Pelaajan nimi */
	protected String name;

	/** Pelaajan v‰ri */
	protected Color color;

	/** Pelaajan id. Saadan serverilt‰. */
	protected int id;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
