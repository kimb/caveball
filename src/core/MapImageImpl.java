package core;

import vectorizer.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Perusimplementaatio karttakuvan käsittelystä.
 */
public class MapImageImpl implements MapImage {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Esikatselun korkeus */
	protected static final int thumbnailHeight = 50;
	/** Avaruuden väri */
	protected static final int spaceColor = 0;

	/** Kartassa olevien alueiden värit */
	protected int[] ZONE_COLORS;
        /** Kartassa paikka, josta voidaan lähteä. */
        protected int START_LOCATION_COLOR;
	/** Millä indeksill ZONE_COLORS:ista löytyy avaruutta. */
	protected static final int SPACE_ZONE = 0;

	/** Ladattu kartta */
	protected BufferedImage map;

	/** Kartan vektorisaatio */
	protected ImageVector[] mapImageVectors;

        /***/
        protected util.Vector[] startLocations;
	/**
	 * Luo kartan kuvan pohjalta. Käyttää mustaa avaruutena.
	 */
	public MapImageImpl(BufferedImage map) {
		this.map = map;
		ZONE_COLORS = new int[2];
		ZONE_COLORS[0]=java.awt.Color.white.getRGB();
		ZONE_COLORS[1]=java.awt.Color.black.getRGB();
                START_LOCATION_COLOR = (new java.awt.Color(0,1,0)).getRGB();
	}
	
	/**
	 * Alueiden värit
	 */
	public int[] getZoneColors() {
		return ZONE_COLORS;
	}
        /**
         * Aloituspaikkojen värit
         */
        public int getStartLocationColor() {
                return START_LOCATION_COLOR;
        }

	/**
	 * Avaruuden väri.
	 */
	public int getSpaceZone() {
		return SPACE_ZONE;
	}

	/** Palauttaa pienen kuvan kartasta */
	public Image getThumbnail() {
		int thumbnailWidth = (int) ((float)getWidth()*(float)thumbnailHeight/(float)getHeight());
		return map.getScaledInstance(thumbnailWidth, 
				thumbnailHeight, Image.SCALE_DEFAULT);
	}

	/** Palauttaa avaruuden värin */
	public int getSpaceColor() {
		return spaceColor;
	}

	/** Palauttaa kartan korkeuden */
	public int getHeight() {
		return map.getHeight();
	}

	/** Palauttaa kartan leveyden */
	public int getWidth() {
		return map.getWidth();
	}

	/** Palauttaa määrätyssä kohtaa olevan värin */
	public int getPixel(int x, int y) {
		return map.getRGB(x, y);
	}

	/** Palauttaa kuvan koko kartasta */
	public Image getImage() {
		return map;
	}
	
	/**
	 * Palauttaa kysisen kartan vektorisaation
	 */
	public ImageVector[] getVectorization() {
		if(mapImageVectors==null) {
			initMapImageVectors();
		}
		return mapImageVectors;
	}
        
        /**
	 * Palauttaa kyseisen kartan paikat, joissa on ollut merkittynä
	 * START_LOCATION_COLOR:lla sopivat aloituspaikat.
         */
	public util.Vector[] getStartLocations(){
                if(mapImageVectors==null) {
			initMapImageVectors();
		}
		return startLocations;
        }

	protected void initMapImageVectors() {
		Vectorizer v = new Vectorizer(this, this, 5);
		ArrayList al = v.getImageVectors();
		mapImageVectors = (ImageVector[]) al.
			toArray(new ImageVector[0]);
		ArrayList sl = v.getStartLocations();
		startLocations = (util.Vector[]) sl.toArray(new
				util.Vector[0]);
		log.info("Vectorized map has "+al.size()+" vectors.");
		log.info("Vectorized map has "+startLocations.length
				+" start locations .");
	}
        
}
