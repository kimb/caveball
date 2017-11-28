package physics;

import core.*;
import util.*;
import vectorizer.*;
import org.apache.log4j.Logger;

/**
 * Hoitaa partikkeleiden keskenäiset törmäilyt.
 * Keskeneräinen.
 * 
 */
public class ParticleCollision implements Collision {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    /** Relative collision time, between (0,1). */
    private float rct;
    /** Current time, between (0,1). */
    private float ct;
    private float hitTime;
    
    private WallCollision wallC1, wallC2;
    
    private WallCollision[] wallCollisionArray;
    
    public ParticleCollision(WallCollision[] wcs, WallCollision wc, float newCT, WallCollision sibling) {
	rct = 1.2f;
	ct = newCT;
	wallCollisionArray = wcs;
	wallC1 = wc;
	wallC2 = null;
	//Vector between;
	int index = 0;
	float tempRTC;
	//float sumRadii;
	while(index < wallCollisionArray.length) {
	    if(wallC1 != wallCollisionArray[index] && wallC1 != sibling) {
		tempRTC = getRelativeCollisionTime(wallC1, 
						    wallCollisionArray[index]);
		if(tempRTC < rct) {
		    rct = tempRTC;
		    wallC2 = wallCollisionArray[index];
		}
	    }
	    
	    
	    /*sumRadii = wallC1.getParticle().getRadius() + wallCollisionArray[index].getParticle().getRadius();
	    between = wallC1.getParticle().getPosition().sub(wallCollisionArray[index].getParticle().getPosition());
	    if(between.squareLength() < sumRadii*sumRadii) {
	    
	    }*/
	    
	    
	    index++;
	}
	/*if((rct <= 1) && (wallC2 != null)) {
	    if(!((wallC2.getPC() != null) && (rct < wallC2.getPC().getRCT()))) {
		if((wallC2.getPC() != null))
		    rct = 1.25f;
	    }
	}*/
	
	if(rct <= 1) {
	    wallC1.newPCHitData(wallC1.getMovementLeft().scalarProduct(hitTime), wallC1.getParticle().getPosition());
	    wallC2.newPCHitData(wallC2.getMovementLeft().scalarProduct(hitTime), wallC2.getParticle().getPosition());
	}
    }
    
    public WallCollision getWC1() {
	return wallC1;
    }
    
    public WallCollision getWC2() {
	return wallC2;
    }
    
    public float getRCT() {
	return rct;
    }
    
    public void setCT(float newCT) {
	ct = newCT;
    }
    
    /**Palauttaa booleanin, joka kertoo, ovatko kaksi ParticleCollisionia 
     * saman törmäyksen kaksi eri ilmentymää.
     */
    public boolean areTheSame(ParticleCollision pc) {
	return ((wallC1 == pc.getWC2()) && (wallC2 == pc.getWC1()));
    }
    
    /**
     * Päivittää toisiinsa törmänneet partikkelit.
     */
    public void updateTwoParticles() {
	Vector reflect = ((wallC1.getParticle().getPosition()).sub(
			wallC2.getParticle().getPosition())).kCrossProduct(1);
	
	wallC1.setPCHitPosition();
	wallC2.setPCHitPosition();	
	
	wallC1.setReflectWall(reflect);
	wallC2.setReflectWall(reflect);	
	
	Particle part1 = wallC1.getParticle();
	Particle part2 = wallC2.getParticle();
	
	PhysicalCalculation.setNewMovementVectors(wallC1, wallC2);
	
	raiseTemperatures();
	
	if(wallC1.impactCausedDeath())
	    wallC1.destroyParticle();
	if(wallC2.impactCausedDeath())
	    wallC2.destroyParticle();
	
	part1.setBounceCount(part1.getBounceCount() + 1);
	part2.setBounceCount(part2.getBounceCount() + 1);
	
	wallC1.reInitializeWallCollisions();					// Uudestaan initialisointi partikkelien kuoleman jälkeen on epävarmaa.
	wallC2.reInitializeWallCollisions();
	
    }
    
    public WallCollision[] getWallCollisions() {
	return wallCollisionArray;
    }
    
    /**
    * Palauttaa floattina RCT:n, jona wallC1:sen ja wallC2 partikkelit
    * törmäävät. Palauttaa 1.3:n jos ne eivät törmääkään.
    */    
    public float checkIfValid(float oldRCT) {
	if(wallC2 == null)
	    return 1.3f;
	float temp = getRelativeCollisionTime(wallC1, wallC2);
	if(oldRCT >= temp)
	    return temp;
	else return 1.3f;
    }
    
    /**
    * Palauttaa floattina RCT:n, jona wallC1:sen ja wallC2 partikkelit
    * törmäävät. Palauttaa 1.3:n jos ne eivät törmääkään.
    */    
    public boolean theyReallyCollided() {
	Vector between = wallC1.getParticle().getPosition().sub(wallC2.getParticle().getPosition());
	float sumRadii = (wallC1.getParticle().getRadius() + wallC2.getParticle().getRadius());
	return (between.squareLength() <= (sumRadii + 6)*sumRadii);			    // OLI TÄSSÄ  + 0.0000001f
    }
    
    public void raiseTemperatures() {
	float factor = wallC1.getParticle().getBounceFactor() * 
		       wallC2.getParticle().getBounceFactor() * 
		       wallC1.getParticle().getVelocity().squareLength() *
		       wallC2.getParticle().getVelocity().squareLength() *
		       0.0002f;
	
	wallC1.getParticle().setTemperature(wallC1.getParticle().getTemperature()
				    + factor);
	wallC2.getParticle().setTemperature(wallC2.getParticle().getTemperature()
				    + factor);
    }
    
    /**
    * Palauttaa kahden partikkelin suhteellisen törmäysajan.
    */
    public float getRelativeCollisionTime(WallCollision wc1, 
					  WallCollision wc2) {
	Vector movement1 = wc1.getMovementLeft();
	Vector relMovement = movement1.sub(wc2.getMovementLeft());
	float f = PhysicalCalculation.whenMobileParticleHitsImmobile(
		wc1.getParticle().getPosition(), wc1.getParticle().getRadius(), 
		wc2.getParticle().getPosition(), wc2.getParticle().getRadius(), 
		relMovement);
	//log.debug(f + " " + ((f/ relMovement.length())*(1 - ct) + ct));
	if(f <= 0) { 
	    return 1.2f;	   // Ei osumaa. Tai f on negatiivinen (?).
	}
	f = (f / relMovement.length());				// Bonus tarkkuutta?
	hitTime = f;
	return (f*(1 - ct) + ct);	// tästä selviää ilman neliöjuuriakin
    }
    
}
