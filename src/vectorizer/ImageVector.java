package vectorizer;

import util.*;

/**
 * Vastaa vektorisoidun kuvan yhden alueen yhtä suoraa reunan palaa.
 * On siis "seinävektori".
 */
public class ImageVector {
        
	/** Alkupiste. */
	protected Vector start;
	/** Siitä lähtevä vektori. */
	protected Vector vector;
	/** Alue johon tämä kuuluu. */
	protected int zoneleft, zoneright;
        /** Kimmokerroin. */
	private float bounceFactor;
	
	/** Luo uuden kuvan-seinävektorin.
         * @param vector Suunta ja pituus.
         * @param start Alkupiste.
         * @param zleft Millaista aluetyyppiä on vasemmalla puolella.
         * @param zright Millaista aluetyyppiä on oikealla puolella.
	 */
	public ImageVector(Vector start, Vector vector, int zleft, int zright) {
		bounceFactor = 1.f;
	    
		this.start = start;
		this.vector = vector;
		this.zoneleft = zleft;
                this.zoneright = zright;
	}
	/** Palauttaa kimmokertoimen. */
	public float getBounceFactor() {
	    return bounceFactor;
	}

	/** Palauttaa Vektorin alkukohdan. */
	public Vector getStartPosition() {
		return start;
	}

	/** Palauttaa vektorin (vektorina), eli saadaan suunta ja pituus. */
	public Vector getVector() {
		return vector;
	}

	/** 
	 * Tyyppi johon vektorin oikea puoli kuuluu.
	 */
	public int getZoneRight() {
		return zoneright;
	}
        /**
         * Tyyppi johon vektorin vasen puoli kuuluu.
         */
        public int getZoneLeft() {
                return zoneleft;
        }
	
	/** 
	 * Palauttaa seinävektorin pituuden neliön.
         * Voisi vaikka laittaa vector.squareLength() ? 
	 */
	public float squareLength() {
	    return (vector.getX()) * (vector.getX()) + (vector.getY()) * (vector.getY());
	}
	
	/** 
	* Palauttaa sen kolmion korkeuden, jonka muodostavat seinävektori
	* (vector) ja vectorin alkupisteestä pisteeseen position lähtevä vektori 
	* (ap).
	*/
	public float squareHeight(Vector position) {
	    Vector ap = position.sub(start);

	    /** 
	    * Luo seinän normaalivektorin.
	    */
	    Vector vectorNormal = vector.kCrossProduct(1);
	    
	    /** 
	    * Sijoittaa f:ään ap:n projektion normaalivektorille (normeeramaton 
	    * pituus).
	    */
	    float f = ap.dotProduct(vectorNormal);
	    return f*f/(vectorNormal.squareLength());  
	}
	
	/**
	* Mahdollistaa seinän ominaisuusten muokkaamisen.
	* TODO: kaikki.
	*/
	public void updateWall() {
	
	}
	/** 
         * Palauttaa skalaarin a joka toteuttaa:
         * a*vector+start==iv->start+iv->vector*b
         * jollain b:llä... -1 jos eivät leikkaa.
         * Jos < 0 tai > 1, eivät leikkaa vectoreiden pituuksilla.
         * Jos välillä 0,1 saattaavat leikata...
         */
        public float getIntersectionScalar(ImageVector iv){
            Vector v1,v2,p1,p2;
            v1=vector;
            p1=start;
            p2=iv.getStartPosition();
            v2=iv.getStartPosition();
            float a=v1.getX()*v2.getY()-v2.getX()*v1.getY();
            if (a==0)
                return -1;
            return (v2.getX()*p1.getY()-v2.getX()*p2.getY()-p1.getX()*v2.getY()+p2.getX()*v2.getY())/a;
        }
}
