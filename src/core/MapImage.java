package core;

import java.awt.*;
import vectorizer.*;

/**
 * Abstraktoi karttakuvan k‰sittelyn.
 */
public interface MapImage 
	extends VectorizationParameters, VectorizableImage 
{

	/** Palauttaa listan eri alueiden v‰reist‰ */
	public int[] getZoneColors();

	/** Palauttaa avaruuden alueen */
	public int getSpaceZone();

	/** Palauttaa kartan korkeuden */
	public int getHeight();

	/** Palauttaa kartan leveyden */
	public int getWidth();

	/** Palauttaa m‰‰r‰tyss‰ kohtaa olevan v‰rin */
	public int getPixel(int x, int y);

	/** Palauttaa kuvan koko kartasta */
	public Image getImage();
	
	/** Palauttaa pienen kuvan kartasta */
	public Image getThumbnail();

	/** Palauttaa kartan vektorisoutuna */
	public ImageVector[] getVectorization();
        
        /** Palauttaa kartan pisteet, jotka ovat merkitty erityisell‰ v‰rill‰. */
        public util.Vector[] getStartLocations();
}
