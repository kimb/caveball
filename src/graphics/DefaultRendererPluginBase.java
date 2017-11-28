package graphics;

import core.*;
import util.*;

import java.awt.Rectangle;

import org.apache.log4j.Logger;

/**
 * Juuriluokka t‰m‰n projektin grafiikkakomponenttien toiminalle.
 * Sis‰lt‰‰ yhteiset abstraktiot.
 */
public abstract class DefaultRendererPluginBase implements RendererPlugin {
	
	private final Logger log = Logger.getLogger(this.getClass());

	/**
	 * Palauttaa kohdan karttaa jossa piirett‰v‰n kuvan origo (yl‰kulma) on.
	 * @param clip Piirrett‰v‰n alueen suuruus
	 */
	public Vector getOriginPosition(GameData gameData, Rectangle clip) {
		// pelaajan paikka. Keskell‰ karttaa jos pelaajalla ei ole partikkelia.
		Vector player = (gameData.getPlayerParticle()==null
				?new Vector(
					160,
					160)
				:gameData.getPlayerParticle().getPosition());
		// ruudun kordinaatti pelaajaan n‰hden
		int x=(int)player.getX()-clip.width/2;
		int y=(int)player.getY()-clip.height/2;
		// korjataan ruudun ulkopuolella oleminen
		return new Vector
			(inBounds(x, 0,
				  gameData.getMapImage().getWidth()-clip.width),
			 inBounds(y, 0,
				 gameData.getMapImage().getHeight()-clip.height));
	}

	/**
	 * Rajaa annetun arvon kahden toisen arvon v‰liin.
	 * Jos min on isompi kuin max niin paluuarvona on (min+max)/2.
	 */
	protected int inBounds(int val, int min, int max) {
		if (min<=val && val<=max) {
			// v‰liss‰
			return val;
		} else if (val<=min && val<=max) {
			// liian pieni
			return min;
		} else if (val>=min && val>=max) {
			// liian iso
			return max;
		} else {
			// min>max
			log.warn("min>max"); 
			return (min+max)/2;
		}
	}
}
