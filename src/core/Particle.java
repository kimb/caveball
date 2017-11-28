package core;

import util.*;


/**
 * Geneerinen partikkeli.
 */
public interface Particle extends java.io.Serializable {
	/** M‰‰rittelee ohjausparametrin */
	public Integer STEERING_UP = new Integer(1);
	/** M‰‰rittelee ohjausparametrin */
	public Integer STEERING_RIGHT = new Integer(2);
	/** M‰‰rittelee ohjausparametrin */
	public Integer STEERING_LEFT = new Integer(3);
	/** M‰‰rittelee ohjausparametrin */
	public Integer STEERING_DOWN = new Integer(4);
        /** M‰‰rittelee ohjausparametrin */
        public Integer INCREASE_RADIUS = new Integer(5);
                /** M‰‰rittelee ohjausparametrin */
        public Integer DECREASE_RADIUS = new Integer(6);
                /** M‰‰rittelee ohjausparametrin */
        public Integer INCREASE_BOUNCE = new Integer(7);
                /** M‰‰rittelee ohjausparametrin */
        public Integer DECREASE_BOUNCE = new Integer(8);
        
        
        public void setQuality(Integer quality, boolean isPressed);

        public float getBounceIncrease();
        public float getRadiusIncrease();
	/** Unique identifier for this particle */
	public Integer getId();

	/** Ohjataanko alusta annettuun suuntaan? */
	public boolean getSteering(Integer steeringProperty);

	/** Asettaa/poistaa ohjauksen annettuun suuntaan. */
	public void setSteering(Integer steeringProperty, boolean active);
	
	/** Asettaa ohjainsuutinten aiheuttaman voiman.*/
	public void setSteeringForce(float temp);
	
	/** Palauttaa ohjausvoiman suuruuden */
	public float getSteeringForce();
	
	/** Palauttaa sijainnin */
	public Vector getPosition();

	/** Asettaa sijainnin */
	public void setPosition(Vector position);


	/** Palauttaa nopeuden */
	public Vector getVelocity();

	/** Asettaa nopeuden */
	public void setVelocity(Vector velocity);

	/** Palauttaa edellisen liikkeen m‰‰r‰n */
	public Vector getMovement();

	/** Asettaa edellisen liikkeen m‰‰r‰n */
	public void setMovement(Vector movement);	
	
	/** Palauttaa l‰mpˆtilan */
	public float getTemperature();

	/** Asettaa l‰mpˆtilan */
	public void setTemperature(float temperature);

	/** Palauttaa t‰m‰n partikkelin maximaalisen l‰mpˆtilan, jos jokin */
	public float getMaxTemperature();

	/** Palauttaa toisten pallojen kanssa pompahdusten m‰‰r‰n */
	public int getBounceCount();

	/** Asettaa toisten pallojen kanssa pompahdusten m‰‰r‰n */
	public void setBounceCount(int bounceCount);

	/** Palauttaa s‰teen */
	public float getRadius();

	/** Asettaa s‰teen */
	public void setRadius(float radius);

	/** Palauttaa partikkelin massan */
	public float getMass();

	/** Asettaa partikkelin massan */
	public void setMass(float mass);

	/** Palauttaa partikkelin kimmokertoimien */
	public float getBounceFactor();

	/** Asettaa partikkelin kimmokertoimien */
	public void setBounceFactor(float bounceFactor);

}
