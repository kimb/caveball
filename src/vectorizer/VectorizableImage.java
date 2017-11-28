package vectorizer;
/** Interface, jonka avulla Vectorizer saa tiet‰‰ kaiken tarvitsemansa kuvasta. */
public interface VectorizableImage {

	/** Palauttaa kartan korkeuden */
	public int getHeight();

	/** Palauttaa kartan leveyden */
	public int getWidth();

	/** Palauttaa m‰‰r‰tyss‰ kohtaa olevan v‰rin */
	public int getPixel(int x, int y);

}
