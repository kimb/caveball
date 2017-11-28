package physics;

import core.*;
import util.*;
import vectorizer.*;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Fysiikka.
 * 
 * 
 */
public class PhysicsImpl implements Physics {
    
	private final Logger log = Logger.getLogger(this.getClass());
    
	private PhysicalProperties worldProperties;
	private GameData data;
	private GameMap arena;
	private CollisionSystem cs;
	
	/** Initialisoi fysiikan.
	 */
	public void initPhysics(GameData gameData, boolean isServer) {
		// FIXME: käytä isServer muuttujaa johonkin.
		cs = new CollisionSystem();
		data = gameData;
		worldProperties = new PhysicalProperties();
		Vector gravity = new Vector(0.f, 9.81f);
		worldProperties.setGravity(gravity);
		worldProperties.setAirResistance(0.02f);
		arena = new GameMap(new ArrayList(Arrays.asList(
			    data.getMapImage().getVectorization())),
			    data.getMapImage().getWidth(), 
			    data.getMapImage().getHeight(), gameData);
		

	}
	
	/**
	 * Liikuttaa systeemin tilaa milliSeconds parametrin verran
	 * eteenpäin.
	 */
	public void moveParticles(int milliSeconds) {
		cs = new CollisionSystem();
		cs.collisionSystemSolver(((float) milliSeconds)/(1000.f), 
				data.getParticles(), arena, worldProperties);
	}
	
	/**
	 * Korjaa systeemin virheellisen tilan, jonka verkon viivästys on
	 * aiheuttanut. Keskeneräinen.
	 */
	public void moveParticles(Particle[] delayedPositions, 
			int delayOfPosition) {
		
	}

	/**
	 * Palauttaa pavun, jolla voi muutella maailman ominaisuuksia.
	 */
	public Object getPhysicalPropertiesBean() {   
		return worldProperties;
	}

}
