package core.packets;

import core.*;

import javax.swing.ImageIcon;

/**
 * Serverin vastaus liittymispyynt��n.
 */
public class JoinReply implements ModuleNetworkPacket {

	/** Serverill� k�ytett�v� kartta */
	public ImageIcon map;

	/** Pelaajan id. */
	public int playerId;

	public JoinReply(GameData gameData) {
		map = new ImageIcon(gameData.getMapImage().getImage());
		playerId = gameData.getServerProperties().getNextPlayerId();
	}

}
