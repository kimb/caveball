package physics;

import core.*;
import util.*;
import vectorizer.*;
import org.apache.log4j.Logger;

/**
 * Laskutoimituskirjasto.
 * 
 * 
 */
public abstract class PhysicalCalculation {
    
	private static Logger log = Logger.getLogger(PhysicalCalculation.class);
	
	final static int NE = 1;
	final static int E  = 2;
	final static int SE = 3;
	final static int N  = 4;
	final static int X  = 5;
	final static int S  = 6;
	final static int NW = 7;
	final static int W  = 8;
	final static int SW = 9;
	
	/**
	 * Vastaa booleanilla kysymykseen "Onko janonen va ja vb muodostamien
	 * kulmien laatu eri; Eli onko toinen ter�v� ja toinen tylpp�? (XOR)".
	 * Jos v on sein�vektori sek� a ja b vektorit osoittavat sein�vektorin
	 * p��tepisteist� samaan pisteeseen, t�m� kertoo onko t�m� piste
	 * sein�vektorin sivustalla (v:n normaalitprojektiot v:st� muodostavat
	 * sivustan)
	 */
	public static boolean isByWallSide(ImageVector wall, Vector position) {
	    
		Vector pa;
		Vector pb;
		pa = position.sub(wall.getStartPosition());
	      
	    /** 
	    * pb on se vektori, joka kulkee sein�vektorin loppupisteest�
	    * partikkelin keskipisteeseen.
	    */
		pb = (wall.getStartPosition()).add(wall.getVector());
		pb = position.sub(pb);
		Vector v = wall.getVector();
		return ((v.dotProduct(pa) < 0) ^ (v.dotProduct(pb) < 0));
	}
	
	
        /**
	 * Laskee tietyll� nopeudella kiit�v�lle partikkelille uuden paikan 
         * tietyn ajanhetken p��st�.
	 */
	public static Vector getNewPosition(Vector velocity, Vector position, 
								float time) {
								    
		return position.add(velocity.scalarProduct(time));		
	}
	
	
	 /**
	 * Laskee kokonaisnopeuden, jota tarvitaan diskreetin aika-askeleen 
	 * tekemiseen.
	 */
	public static Vector calculateVelocity(Particle part, float time, 
					PhysicalProperties worldProperties) {
					  

	    
	    float directionCount = 0.f;
	    Vector temp = new Vector(0.f, 0.f);
	    if(part.getSteering(Particle.STEERING_UP)) {
		temp = temp.add(Vector.N);
		directionCount++;
	    }
	    if(part.getSteering(Particle.STEERING_RIGHT)) {
		temp = temp.add(Vector.E);		
		directionCount++;
	    }
	    if(part.getSteering(Particle.STEERING_LEFT)) {
		temp = temp.add(Vector.W);
		directionCount++;
	    }
	    if(part.getSteering(Particle.STEERING_DOWN)) {
		temp = temp.add(Vector.S);
		directionCount++;
	    }
	    
	    if(part.getMass() != 0.f) {
		if(directionCount == 2)
		    temp = temp.scalarProduct(part.getSteeringForce() / 
						    (1.41f * part.getMass()));
		else 
		    temp = temp.scalarProduct(part.getSteeringForce() / 
						    part.getMass());
	    }
	    else 
		System.out.println("calculateVelocity: Mass is zero! I can't handle it!");

	    temp = temp.add(worldProperties.getGravity());
	    temp = temp.add(Environment.acceleration(part));
	    temp = temp.sub(part.getVelocity().scalarProduct(worldProperties.getAirResistance()/part.getMass()));
	    temp = temp.scalarProduct(time);	    
	    return part.getVelocity().add(temp);
	}
        
	
	 /**
	 * Interpoloi paikan johonkin vektoreiden oldpos ja newpos v�liin,
         * muuttujan percentage m��r��m�n�.
	 */
	public static Vector interpolateNewPosition(Vector oldpos, Vector newpos, 
		float percentage) {		    
		    
	    Vector ipos = oldpos.scalarProduct(1 - percentage);
	    ipos = oldpos.add(newpos.scalarProduct(percentage));
	    return ipos;
	}
	
	
	/**
	 * K��nt�� partikkelin nopeuden sen komponentin ymp�ri, joka on sein�n
	 * wall (pelkk� vektori t�ss� tapauksessa) normaalin suuntainen.
	 * vreversed = vold - 2*vold_x
	 */	
	public static Vector reverseNormalComponent(Vector velocity, 
								Vector wall) {
								    
	    Vector normal = wall.kCrossProduct(1);
	    Vector yComponent;
	    
	    /**
	    * Y-komponentin pituuden ja sitten suunnan laskeminen.
	    */
	    float temp = normal.dotProduct(velocity);
	    yComponent = normal.scalarProduct(temp);
	    
	    /**
	    * Nyt Y-komponentin pituus normalisoidaan ja kerrotaan miinus 
	    * kahdella. Eli k��nnet��n komponentti oikeaan suuntaan.
	    */
	    temp = (-2.f)/normal.squareLength();
	    yComponent = yComponent.scalarProduct(temp);
	    
	    return velocity.add(yComponent);			    
	}
	
	
	/**
	* Kertoo onko partikkeli liian l�hell� annettua sein��.
	* Palauttaa kyseisen sein�n jos on, null:in muuten.
	*/
        public static ImageVector wallHitDetection(Particle part, 
		ImageVector wall) {
		    
	    final ImageVector NOIMPACT = null;

	    Vector position = part.getPosition();
	    float r = part.getRadius();	      
	    Vector pa = position.sub(wall.getStartPosition());
	      
	    /** 
	    * pb on se vektori, joka kulkee sein�vektorin loppupisteest�
	    * partikkelin keskipisteeseen.
	    */
	    Vector pb = (wall.getStartPosition()).add(wall.getVector());
	    pb = position.sub(pb);
	      
	    float rr = r*r;
	      
	    /** 
	    * Tarkistaa onko pallo liian l�hell� kumpaakaan sein�n p��tepistett�,
	    * ja sitten viel� laskee pallon keskipisteen ja sein�n lyhimm�n
	    * et�isyyden.
	    */
	    if(pa.squareLength() > rr) {
		if(pb.squareLength() > rr) {
		    if(!isByWallSide(wall,position))
			return NOIMPACT;
		    if(wall.squareHeight(position) > rr)
			return NOIMPACT;
		    else return wall;
		}
		else return wall;
	    }
	    else return wall;
	}
	
	
	/**
	* Oletetaan, ett� partikkeli t�rm�� sein�n alku- tai loppupisteess�.
	* T�ss� palautetaan se vektori, joka lis��m�ll� partikkelin paikkaan
	* saadaan py�re�n partikkelin t�rm�yskohta kyseisen pisteen kanssa.
	* Lis�tietoa laskutoimituksessa l�ytyy dokumentaatiosta (TODO);
	*/
	public static Vector calculateVertexBouncePosition(Vector centerToVertex, 
		Vector particleMove, float radius) {
		    
	    float cp = centerToVertex.dotProduct(particleMove);
	    float vv = particleMove.squareLength();
	    if(vv == 0.f) {
		//log.debug(
		//   "calculateVertexBouncePosition: particle ain't moving!");
		return particleMove; 
	    }
	    float cc = centerToVertex.squareLength();
	    float rr = radius*radius;
	    float temp = (float) Math.sqrt((cp*cp)/(vv*vv)+(rr-cc)/vv);
	    temp = ((cp/vv) - temp);
	    //if(temp < 0)
		//log.debug("VertexCollision: -negative dotProduct");
	    //log.debug("VertexCollision: " + particleMove.scalarProduct(cp/vv-temp).length() + " cp/vv: " + (cp/vv) + " temp: " + temp);
	    //return particleMove.scalarProduct(cp/vv-temp).sub(particleMove.scalarProduct(radius/particleMove.length()));
	    return particleMove.scalarProduct(temp);
	}
	
	
	/**
	* Oletetaan, ett� partikkeli t�rm�� sein��n siten, ett� sein� toimii
	* ympyr�n muotoisen partikkelin tangenttina. T�ss� palautetaan se 
	* vektori, joka lis��m�ll� partikkelin paikkaan saadaan partikkelin 
	* t�rm�yskohta kyseisen sein�n kanssa.
	* HUOM: Origona toimii partikkeli.
	* Lis�tietoa laskutoimituksessa l�ytyy dokumentaatiosta (TODO);
	*/
	public static Vector calculateEdgeBouncePosition(ImageVector wall, 
		Vector centerToVertex, Vector particleMove, float radius) {
		    
	    Vector normal = (wall.getVector()).kCrossProduct(1);
	    if(normal.dotProduct(particleMove) < 0) 
		normal = normal.scalarProduct(-1.f);
	    float cn = centerToVertex.dotProduct(normal);
	    float pn = particleMove.dotProduct(normal);
	    if(pn==0) /* Virhetilanne. DESTROY BALL? */
		return particleMove;    
	    float temp = radius*normal.length();
	    /**if((cn-temp) < 0) {
		log.debug("EdgeCollision: -negative dotProduct");
		log.debug("Wstart: " + wall.getStartPosition().getX() + " " + wall.getStartPosition().getY() + 
			    " Wend: " + wall.getStartPosition().add(wall.getVector()).getX()  + " "  + wall.getStartPosition().add(wall.getVector()).getY());
		
		
		
	    }
	    */
	    //log.debug("EdgeCollision length: " + particleMove.scalarProduct((cn-temp)/pn).length());
	    return particleMove.scalarProduct((cn-temp)/pn);
	}
	
	
	/**
	* Laskee kahden 2D suoran (lineV ja lineW suuntaiset, piste vectirToX
	* on suoralla X) leikkauspisteen. Laskemisen j�lkeen leikkauspiste
	* palautetaan vektorina. Jos suorat ovat yhdensuuntaiset, palautetaan
	* null.
	*/
	public static Vector calculateIntersectionOfTwoLines(Vector lineV, 
			    Vector lineW, Vector vectorToV, Vector vectorToW) {
		
	    float denominator = lineV.vCrossProduct(lineW);
	    if(denominator == 0) {
		log.debug(
		    "calculateIntersectionOfTwoLines: the lines are parallel!");
		return null;
	    }
	    float numerator = lineV.vCrossProduct(vectorToV.sub(vectorToW));
	    return vectorToW.add(lineW.scalarProduct(numerator/denominator));
	}
	
	
	/**
	* Palauttaa sen kertoimen, jolla liikkuvan pallon liikevektori pit��
	* kertoa, jotta pallo olisi kontaktissa toisen, liikkumattoman pallon
	* kanssa. Palauttaa -1 jos t�rm�ys ei ole mahdollinen, tai jos
	* kerroin tulisi olemaan yli yhden.
	* Toimintaperiaate: 
	* http://www.gamasutra.com/features/20020118/vandenhuevel_02.htm
	*/
	public static float whenMobileParticleHitsImmobile(Vector mobilePosition,
			    float radius1, Vector immobilePosition, 
			    float radius2, Vector v) {
	    Vector c = immobilePosition.sub(mobilePosition);

	    float d = c.dotProduct(v);
            if (d<=0)
                return -1;
            d = d*d/v.squareLength();
	    float f = c.squareLength() - d;
	    float sumRadii = (radius1+radius2);
	    if(f > (sumRadii*sumRadii)) // Ei osumaa.
		return -1;
	    float t = sumRadii*sumRadii - f;
	    if(t < 0 ) // Ei osumaa.
		return -1;
	    d = (float) (Math.sqrt(d) - Math.sqrt(t));
	    if(d*d > v.squareLength() || d < 0) // Ei osumaa.
		return -1;
	    return d;
	}
	
	
	/**
	* Origossa kulmastaan (corner) riippuva suorakulmio m��ritell��n
	* rectangleHeight ja rectangleLength vektorien avulla. T�m� metodi
	* kertoo, miss� yhdeks�st� eri lohkosta annettu piste vertexPosition
	* on. Lohkot muodostetaan piirt�m�ll� suorakulmion joka sivulle
	* yhdensuuntaiset suorat.
	* Lis�tietoa laskutoimituksessa l�ytyy dokumentaatiosta (TODO);
	*/
	public static int mapVertex(Vector vertexPosition, Vector rectangleHeight, 
		Vector rectangleLength, Vector rectangleCorner) {
	    
	    final int NE = 1;
	    final int E  = 2;
	    final int SE = 3;
	    final int N  = 4;
	    final int X  = 5;
	    final int S  = 6;
	    final int NW = 7;
	    final int W  = 8;
	    final int SW = 9;

	    Vector cornerToPosition = vertexPosition.sub(rectangleCorner);
	    	 	    
	    float rls = rectangleLength.squareLength();
	    float rhs = rectangleHeight.squareLength();
	    
	    float LengthSideVertexDistance = 
		rectangleLength.vCrossProduct(cornerToPosition);
	    float HeightSideVertexDistance = 
		rectangleHeight.vCrossProduct(cornerToPosition);
	    
	    boolean u = (LengthSideVertexDistance >= 0);
	    boolean v = (LengthSideVertexDistance * LengthSideVertexDistance >= 
		rls*rhs);
	    
	    boolean y = (HeightSideVertexDistance >= 0);
	    boolean z = (HeightSideVertexDistance * HeightSideVertexDistance >= 
		rls*rhs);
	    
	    if(u) {
		if(y) 
		return NE;
		
		else {
		    if(z)
			return SE;
		    
		    else return E;
		}
	    }
	    
	    else {
		if(y) {
		    if(v)
			return NW;
		    
		    else return N;
		}

		else {
		    if(v) {
			if(z)
			    return SW;
			else return W;
		    }
		    
		    else {
			if(z)
			    return S;
			else return X;
		    }
		}
	    }

	    
	}
        /**
         * Taktiikkana laskea CM -koordinaatistossa kaikki laskut, 
         * koska siell� |p1|=|p2|=|q1|=|q2|.
         */
        public static void setNewMovementVectors(WallCollision wc1,WallCollision wc2){
            float m1=wc1.getParticle().getMass(), m2=wc1.getParticle().getMass(),
                  ypsm=1/(m1+m2);
            Vector v1=wc1.getParticle().getVelocity(), v2=wc2.getParticle().getVelocity(),
                v1_end, v2_end, 
               // debug1=wc2.getParticle().getPosition().sub(wc1.getParticle().getPosition()),
                            wall=wc2.getParticle().getPosition().
                            sub(wc1.getParticle().getPosition()).kCrossProduct(1),
                   vc=v1.add(v2.scalarProduct(m2/m1)).scalarProduct(ypsm*m1),
                //p1 on 1.partikkelin suhteellinen liikem��r� massakeskipistekoordinaatistossa...
                p1=v1.sub(vc).scalarProduct(m1);
            /**if (v1.dotProduct(debug1)<v2.dotProduct(debug1)) {
                System.out.println("Ei t�rm�ttyk��n, koska menn��n eri suuntaan!! ");
                return;
            }
            if (wc1.getMovementLeft().dotProduct(debug1)<=wc2.getMovementLeft().dotProduct(debug1)){
                System.out.println("Ei t�rm�ttyk��n, koska movementit eri suuntaan!! ");                
                return;
            }*/
            /**
             * Lasketaan t�rm�nnyt p1. Tiedet��n, ett� se on vektori, joka on peilautunut
             * pallojen v�lisest� tangentiaalisesta sein�st�... tms.
             */
             p1=reverseNormalComponent(p1,wall);
             wc1.setMovementLeft(reverseNormalComponent(wc1.getMovementLeft(),wall));
             wc2.setMovementLeft(reverseNormalComponent(wc2.getMovementLeft(),wall));
             
            /**
             * Nyt vaan muutetaan p1 takaisin normaalikoordinaatistoon ja lasketaan siit� v1 ja v2.
             */
            v1_end=p1.scalarProduct(wc1.getParticle().getBounceFactor() * wc2.getParticle().getBounceFactor() /m1).add(vc);
            v2_end=p1.scalarProduct(-wc1.getParticle().getBounceFactor() * wc2.getParticle().getBounceFactor()/m2).add(vc);
            wc1.getParticle().setVelocity(v1_end);
            wc2.getParticle().setVelocity(v2_end);
            //ja sitten lasketaan viel� uudet movementVectorsit...
        }
	
	public static float calculateMass(float radius) {
	    return radius*radius*0.0444444f + 4;
	}
	
	public static float calculateSteeringForce(float r) {
	    if(r < 83)
		return (1.1f*r-0.02f*r*r+0.00007f*r*r*r+15)*calculateMass(r);
	    if(r < 130)
		return 11*calculateMass(r);
	    return 7*calculateMass(r);
	}
	
}
