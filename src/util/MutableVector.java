package util;

/**
 * Tarjoaa muutettavan version kantaluokasta Vector
 */
public class MutableVector extends Vector {

	/** 
	 * Tee muutettava klooni toisesta vektorista 
	 * @param v kloonattava vektori, joka j‰‰ rauhaan.
	 */
	public MutableVector(Vector v) {
		super(v);
	}

	public MutableVector(float x, float y) {
		super(x,y);
	}
	
	/**
	 * Lis‰‰ uusi vekteri olemassa olevaan
	 */
	public MutableVector setAdd(Vector v) {
		x += v.getX();
		y += v.getY();
		return this;
	}
}

