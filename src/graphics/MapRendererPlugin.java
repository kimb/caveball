package graphics;

import core.*;
import util.*;
import java.awt.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
 * Piirt‰‰ kartan.
 * S‰ilytt‰‰ kuvasta oman kopion siin‰ muodossa, jossa se t‰ytyy piirt‰‰,
 * jotta v‰ri-muunnoksia ei jouduta tekem‰‰n useammin.
 */
public class MapRendererPlugin extends DefaultRendererPluginBase {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Lokaali kopio kartasta */
	protected BufferedImage mapBufferImage;

	/** kuva, josta meill‰ on lokaali kopio */
	protected MapImage image = null;

	
	/** Tekee uuden lokaalin kopion MapImage:sta mapBufferImage:een. */
	public void reInit(GameData gameData) {
		int w=gameData.getMapImage().getImage().getWidth(null);
		int h=gameData.getMapImage().getImage().getHeight(null);
		mapBufferImage = new BufferedImage(
				w,
				h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D mapGraphics = mapBufferImage.createGraphics();
		mapGraphics.drawImage(gameData.getMapImage().getImage(),
				0, 0,
				(ImageObserver) null);
		mapGraphics.dispose();
	}
	/**
	 * Piirt‰‰ kartan.
	 */
	public void render(Graphics2D g, GameData gameData) {
		if(image == null || gameData.getMapImage() != image) {
			log.info("Re-initing MapRendererPlugin for new map");
			image = gameData.getMapImage();
			reInit(gameData);
		}
		Vector origin = getOriginPosition(gameData, g.getClipBounds());
		g.drawImage(mapBufferImage,
				(int) -origin.getX(),
				(int) -origin.getY(),
				(ImageObserver) null);
		//log.debug("drawing image to: ("+(int)origin.getX()+","+(int)origin.getY()+")"+ " with bounds: "+g.getClipBounds());
	}

}
