package core.packets;

import core.*;

import javax.swing.*;
import java.io.*;

/**
 * M‰‰rittelee kevyen paketin dataa joka l‰hetet‰‰n pelist‰ kiinnostuville
 * muttei siihen viel‰ liittyneille.
 */
public class GamePreview implements Packet {

	/** pelidata josta init() p‰ivitt‰‰ tiedot */
	transient private GameData g;

	/** Pikkukuva kartasta */
	public ImageIcon mapThumbnail;

	/** Serverin nimi */
	public String gameName;

	/** klienttien m‰‰r‰ */
	public int clients;

	/**
	 * Luo uuden peli-esikatselun.
	 * @param g GameData josta esikatselu tehd‰‰n.
	 */
	public GamePreview(GameData g) {
		this.g = g;
		init();
	}

	/**
	 * P‰ivitt‰‰ julkiset tiedot konstruktorissa annetusta GameData:sta.
	 * Olion serialisointi kutsuu t‰t‰ automaattisesti.
	 */
	protected void init() {
		mapThumbnail = new ImageIcon(g.getMapImage().getThumbnail());
		gameName = g.getServerProperties().getServerName();
		clients = g.getParticles().length;
	}

	/** Kutsuu init():i‰ ennen serialisointia. */
	private void writeObject(ObjectOutputStream s) throws IOException {
		init();
		s.defaultWriteObject();
	}
}
