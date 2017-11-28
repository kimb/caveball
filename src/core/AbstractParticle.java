package core;

import util.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Partikkelin default implementaatio.
 * Ei ole abstract koska sit‰ k‰ytet‰‰n testaukseen ja nimettˆmien partikkelien
 * hoitamiseen.
 */
public class AbstractParticle implements Particle {

	private static int nextId = 0;

	protected Vector position;

	protected Vector velocity;

	transient protected Vector movement;
	
	protected Integer id;
	
	protected float temp;

	protected int bounceCount = 0;

	protected float radius;

	protected float bounceFactor;

	protected float maxTemp;

	protected float mass;
	
	protected float steeringForce;

        protected float increaseRadius;
        
        protected float increaseBounceFactor;
        
	/** T‰‰ll‰ s‰ilytet‰‰n tieto ohjauksesta */
	private Map steering = new HashMap(4);

	protected static final Object nonNull = Boolean.TRUE;

	// Creator
	
	public AbstractParticle() {
		id = new Integer(nextId++);
	}

	/** Tosi, jos partikkeleilla on sama id */
	public boolean equals(Object o) {
		if(o == null || !( o instanceof Particle) ) {
			return false;
		}
		return this.id.equals(((Particle)o).getId());
	}

	// Accessors:
	//
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id=new Integer(id);
	}

	// toiminta m‰‰ritelty rajapinnassa
	public boolean getSteering(Integer steeringProperty) {
		return steering.get(steeringProperty)!=null;
	}

	// toiminta m‰‰ritelty rajapinnassa
	public void setSteering(Integer steeringProperty, boolean active) {
		steering.put(steeringProperty, active?nonNull:null);
	}
	
	public void setSteeringForce(float temp) {
	    steeringForce = temp;
	}
	
	public float getSteeringForce() {
	    return steeringForce;
	}
	
	public Vector getPosition() {
	      return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	public Vector getMovement() {
	    return movement;
	}

	public void setMovement(Vector move) {
	    movement = move;
	}	
	
	public float getTemperature() {
		return temp;
	}
	public void setTemperature(float temp) {
		this.temp = temp;
	}
	public float getMaxTemperature() {
		return maxTemp;
	}
	public void setMaxTemperature(float maxTemp) {
		this.maxTemp = maxTemp;
	}

	public int getBounceCount() {
		return bounceCount;
	}
	public void setBounceCount(int bounceCount) {
		this.bounceCount = bounceCount;
	}

	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getBounceFactor() {
		return bounceFactor;
	}
	public void setBounceFactor(float bounceFactor) {
		this.bounceFactor = bounceFactor;
	}

	public float getMass() {
		return mass;
	}
	public void setMass(float mass) {
		this.mass = mass;
	}
        
        public void setQuality(Integer quality, boolean isPressed) {
            int q=quality.intValue(),factor=1;
            if (!isPressed){
                factor=0;
            }
            if(quality.compareTo(INCREASE_RADIUS)==0) increaseRadius=factor;
            if(quality.compareTo(DECREASE_RADIUS)==0) increaseRadius=-factor;
            if(quality.compareTo(INCREASE_BOUNCE)==0) increaseBounceFactor=factor;
            if(quality.compareTo(DECREASE_BOUNCE)==0) increaseBounceFactor=-factor;
        }
        
        public float getBounceIncrease(){
            return increaseBounceFactor/10;
        }
        public float getRadiusIncrease(){
            return increaseRadius;
        }
        
}
