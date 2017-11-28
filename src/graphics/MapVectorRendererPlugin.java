package graphics;

import core.*;
import util.*;
import vectorizer.*;

import java.awt.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
 * Piirt‰‰ kartan vektorit
 */
public class MapVectorRendererPlugin extends DefaultRendererPluginBase {

	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Piirt‰‰ kartan.
	 */
	public void render(Graphics2D g, GameData gameData) {
		Vector origin = getOriginPosition(gameData, g.getClipBounds());
		ImageVector[] ivs = gameData.getMapImage().getVectorization();
		ImageVector iv;
		Vector start;
		Vector end;
		g.setColor(Color.orange);
		for(int i=0; i< ivs.length; i++) {
			iv = ivs[i];
			start = iv.getStartPosition().sub(origin);
			end = iv.getStartPosition()
				.add(iv.getVector())
				.sub(origin);
			g.drawLine
				((int)start.getX(),
			 	 (int)start.getY(),
				 (int)end.getX(),
				 (int)end.getY());
		}
	}

}
