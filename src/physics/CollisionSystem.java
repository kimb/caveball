/*
 * CollisionSystem.java
 *
 * Created on 4. huhtikuuta 2004, 1:35
 */

package physics;

import core.*;
import util.*;
import vectorizer.*;
import org.apache.log4j.Logger;



	
/**
 * Tämän luokan instanssit ovat fysikaalisia systeemejä, joiden tilanmuutokset
 * tämän luokan metodit laskevat.
 * @author  TMT
 */

public class CollisionSystem {
    // PriorityQueues:
    private PriorityQueue wallCollisions;
    private PriorityQueue particleCollisions;
    private final Logger log = Logger.getLogger(this.getClass());
    private PhysicalProperties world;
    
    /** 
    * Laskee systeemin, jonka muodostavat partArray, arena sekä wolrdProperties,
    * tilan ajanhetken timestep päässä.
    */
    public void collisionSystemSolver(float timestep, Particle[] partArray, 
			    GameMap arena, PhysicalProperties worldProperties) {
		
	int count = 0;
	float rct = 0;
	float temperature;
	world = worldProperties;
	wallCollisions = new PriorityQueue();
	particleCollisions = new PriorityQueue();
	WallCollision tempWC;
	ParticleCollision tempPC;	
	Vector movement;
	if(partArray.length == 0)
	    return;
	while(count < partArray.length) {
	    movement = PhysicalCalculation.calculateVelocity(partArray[count],
			0.5f*timestep, worldProperties).scalarProduct(timestep);
	    partArray[count].setMovement(movement);

	    
	    // Jäähdytys:
	    temperature = partArray[count].getTemperature() - 10*timestep;
	    if(temperature > 0)
		partArray[count].setTemperature(temperature);
	    if(temperature > partArray[count].getMaxTemperature()) {
		log.info("Particle number " + partArray[count].getId() + " overheated!");
		partArray[count].setTemperature(0);
		if(partArray[count].getRadius() > 7)
		    partArray[count].setRadius(partArray[count].getRadius()*0.5f);
	    }
	    movement = PhysicalCalculation.calculateVelocity(
				partArray[count], timestep, worldProperties);
	    partArray[count].setVelocity(movement);


	    tempWC = new WallCollision(movement, partArray[count], arena, null);	    

	    // Laitetaan seinätörmäykset järjestykseen.
	    wallCollisions.add(tempWC);
	    count++;
	}
	
	// Sijoitetaan WallCollisionit arenalle...
	count = 0;
	WallCollision[] wallCollisionsArray;
	wallCollisionsArray = ((WallCollision[]) wallCollisions.toArray(
							new WallCollision[0]));
	while(count < partArray.length) {

	    tempPC = new ParticleCollision(wallCollisionsArray,			// getWallCollitions(...);
					    wallCollisionsArray[count], rct, null);
  

	    // Laitetaan partikkelien väliset törmäykset järjestykseen.
	    if(tempPC.getRCT() <= 1)
		particleCollisions.add(tempPC);
	    
	    count++;
	}
 
	
	boolean wallCollidedFirst;
	if(particleCollisions.isNotEmpty() && (wallCollisions.first().getRCT() 
				    >= particleCollisions.first().getRCT())) {

	    wallCollidedFirst = false;
	    rct = particleCollisions.first().getRCT();
	}
	
	else {
	    
	    wallCollidedFirst = true;
	    rct = wallCollisions.first().getRCT();
	    
	}
	
	WallCollision tempWC2;
	count = 0;
	
	float oldRCT = 0;
	boolean largeTimeStep;
	
	while (rct <= 1) {
	    largeTimeStep = ((rct - oldRCT) > 0.001);
	    if(wallCollidedFirst) {
					
		//log.debug("Wall Collision");
		moveParticles(rct);
		//log.debug("ITER " + count + " RCT: " + rct);
		
		tempWC = ((WallCollision) wallCollisions.first());
		tempWC.updateParticle();
		tempPC = tempWC.getPC();
		if(tempPC != null)						// jos tulee ensin WallCollision,onko myös ParticleCollision olemassa?
		    particleCollisions.removeThis(tempPC);

		wallCollisions.removeFirst();					// FIXME: muuttui
		wallCollisions.add(tempWC);

		tempPC = new ParticleCollision((WallCollision[]) arena.getwallCollisions((int) tempWC.getParticle().getPosition().getX(), (int) tempWC.getParticle().getPosition().getY(), 2*tempWC.getParticle().getRadius() + 2.84f*Math.max(Math.abs(tempWC.getMovementLeft().getX()), Math.abs(tempWC.getMovementLeft().getY()))).toArray(new WallCollision[0]),		// getWallCollitions(...);
						tempWC, rct, null);
		
		if(tempPC.getRCT() <= 1) {
		    particleCollisions.add(tempPC);
		    tempWC.setPC(tempPC);		    
		}
		else tempWC.setPC(null);
	    }
	 
	    else {
		moveParticles(rct);
		tempPC = ((ParticleCollision) particleCollisions.first());
		tempWC = tempPC.getWC1();
		tempWC2 = tempPC.getWC2();

		if(tempPC.theyReallyCollided()) {				

		    //log.debug("Particle Collision " + count + " " + rct + " " + particleCollisions.queueSize());
		    
		    tempPC.updateTwoParticles();
		    wallCollisions.removeThis(tempWC);
		    wallCollisions.removeThis(tempWC2);

		    wallCollisions.add(tempWC);
		    wallCollisions.add(tempWC2);
		
		    if((particleCollisions.queueSize() > 1) && 
					tempPC.areTheSame(((ParticleCollision) 
					particleCollisions.getElement(1))))
		        particleCollisions.removeFirst();
		
		    particleCollisions.removeFirst();
		
		    tempPC = new ParticleCollision((WallCollision[]) arena.getwallCollisions((int) tempWC.getParticle().getPosition().getX(), (int) tempWC.getParticle().getPosition().getY(), 2*tempWC.getParticle().getRadius() + 2.84f*Math.max(Math.abs(tempWC.getMovementLeft().getX()), Math.abs(tempWC.getMovementLeft().getY()))).toArray(new WallCollision[0]),		 
						    tempWC, rct, tempWC2);
		    
		   //if((rct - tempWC.getLastCollisionTime()) < 0.05)
			//if(tempWC.getLastCollisionPartner() == tempPC.getWC2())
			  //  tooSlow = true;
		    
		    tempWC.setLastCollisionTime(rct);
		    tempWC.setLastCollisionPartner(tempWC2);
		    
		    tempWC2.setLastCollisionTime(rct);
		    tempWC2.setLastCollisionPartner(tempWC);
		    
		    if(tempPC.getRCT() <= 1) { // && !(((rct - tempWC.getLastCollisionTime()) < 0) && (tempWC.getLastCollisionPartner() == tempPC.getWC2()))
		        particleCollisions.add(tempPC);
			tempWC.setPC(tempPC);
		    }
		    else tempWC.setPC(null);
		    
		    tempPC = new ParticleCollision((WallCollision[]) arena.getwallCollisions((int) tempWC2.getParticle().getPosition().getX(), (int) tempWC2.getParticle().getPosition().getY(), 2*tempWC2.getParticle().getRadius() + 2.84f*Math.max(Math.abs(tempWC2.getMovementLeft().getX()), Math.abs(tempWC2.getMovementLeft().getY()))).toArray(new WallCollision[0]),		// getWallCollitions(...);
						    tempWC2, rct,tempWC);
		    if(tempPC.getRCT() <= 1) {
			particleCollisions.add(tempPC);
			tempWC2.setPC(tempPC);
		    }
		    else tempWC2.setPC(null);
		    
		}
		
		else {
		    // Jotain on tapahtunut, tätä ParticleCollisionia ei olekaan.
		    //log.debug("weird Particle Collision"  + count + " " + rct + " " + particleCollisions.queueSize());
		    particleCollisions.removeFirst();
		    
		    
		    tempPC = new ParticleCollision((WallCollision[]) arena.getwallCollisions((int) tempWC.getParticle().getPosition().getX(), (int) tempWC.getParticle().getPosition().getY(), 2*tempWC.getParticle().getRadius() + 2.84f*Math.max(Math.abs(tempWC.getMovementLeft().getX()), Math.abs(tempWC.getMovementLeft().getY()))).toArray(new WallCollision[0]), tempWC, // getWallCollitions(...);
		     rct, null);
		    
		    
		    tempWC.setLastCollisionTime(rct);
		    tempWC.setLastCollisionPartner(tempWC2);
		    if(tempPC.getRCT() <= 1) {
			particleCollisions.add(tempPC);
			tempWC.setPC(tempPC);
		    }
		    else tempWC.setPC(null);
		}
	    }
	    
	    if(particleCollisions.isNotEmpty() && 
				    (wallCollisions.first().getRCT() >= 
				    particleCollisions.first().getRCT())) {

		wallCollidedFirst = false;
		if(rct ==  particleCollisions.first().getRCT())			// FIXME:
		    rct = 1.01f;
		else rct = particleCollisions.first().getRCT();
	    }
	    
	    else {
	    
		wallCollidedFirst = true;
		rct = wallCollisions.first().getRCT();
	    
	    }
	    oldRCT = rct;
	    count++;
	}
	count = 0;
	// log.debug("CS Solver: went through one solving round");
	if(rct > 1) {
	    moveParticles(1.f);
	    //log.debug("ITER END " + rct);		    
	}
	float increase;
	float xpos;
	float ypos;
	
	while(count < wallCollisionsArray.length) {
	    wallCollisionsArray[count].fixPosition();
	    increase = partArray[count].getRadiusIncrease();
	    if(!wallCollisionsArray[count].isInsideWall()) {
		if(increase > 0) {
		    partArray[count].setRadius(partArray[count].getRadius() + increase);
		    partArray[count].setMass(PhysicalCalculation.calculateMass(partArray[count].getRadius()));
		    partArray[count].setSteeringForce(PhysicalCalculation.calculateSteeringForce(partArray[count].getRadius()));
		}
	    }
	    else {
		if(increase > 0 && (partArray[count].getRadius() - 1 > 1)) {
		    partArray[count].setRadius(partArray[count].getRadius() - 1);
		    partArray[count].setMass(PhysicalCalculation.calculateMass(partArray[count].getRadius()));
		    partArray[count].setSteeringForce(PhysicalCalculation.calculateSteeringForce(partArray[count].getRadius()));
		}
		/*else {
		    if(partArray[count].getVelocity().squareLength() < 1)
			partArray[count].setVelocity(partArray[count].getVelocity().add(world.getGravity().scalarProduct(-3*timestep)));
		 }*/
		
	    }
	    
	    if((increase < 0 && (partArray[count].getRadius() + increase) > 1)) {
		partArray[count].setRadius(partArray[count].getRadius() + increase);
		partArray[count].setMass(PhysicalCalculation.calculateMass(partArray[count].getRadius()));
		partArray[count].setSteeringForce(PhysicalCalculation.calculateSteeringForce(partArray[count].getRadius()));
	    }
	    
	    xpos = partArray[count].getPosition().getX();
	    ypos = partArray[count].getPosition().getY();
	    
	    increase = partArray[count].getRadius();
	    
	    if(xpos - increase > (float) arena.getWidth() || xpos + increase < 0 || ypos - increase > (float) arena.getHeigth() || ypos + increase < 0) {
		partArray[count].setPosition(arena.getRandomStartLocation());
		partArray[count].setVelocity(new Vector(0,0));
	    }
	    
	    increase = partArray[count].getBounceFactor() + partArray[count].getBounceIncrease();
	    if(increase > 0 && increase < 1.1f)
		partArray[count].setBounceFactor(increase);
	    
	    if(partArray[count].getRadius() > 1)
		partArray[count].setRadius(partArray[count].getRadius() + (float) (0.1*(Math.random() - 0.5)));
	    else
		partArray[count].setRadius(partArray[count].getRadius() + (float) (0.1*(Math.random() + 0.5)));
	    count++;

	}
	
    }

    
    /**
     * Liikuttaa kaikki partikkelit siten, että niiden sisäinen aika vastaa
     * rctnew:tä.
     */
    public void moveParticles(float rctnew) {
	int count = 0;
	// log.debug("CS Solver: moveParticles");
	while(count < wallCollisions.queueSize()) {
	 	((WallCollision) 
		    wallCollisions.getElement(count)).updatePosition(rctnew);
		count++;
	}
    }
    
}
