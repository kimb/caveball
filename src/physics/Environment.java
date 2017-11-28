/*
 * Environment.java
 *
 * Created on 30. maaliskuuta 2004, 1:15
 */

package physics;

import core.*;
import util.*;
import vectorizer.*;
import org.apache.log4j.Logger;

/**
 * Sis�lt�� ymp�rist�tekij�it�, kuten ajan mukana muuttuvia voimakentti� jne.
 *
 * @author  TMT
 */
public class Environment {   
    
    private final Logger log = Logger.getLogger(this.getClass());
    public Vector acceleration;
    
    /** Creates a new instance of Environment */
    public Environment() {
	
    }
    
    /** P�ivitt�� kiihtyvyysvektorin ja ajanhetken. */
    public void updateForces(Particle part) {
    
    }
    
    /** Palauttaa ymp�rist�n aiheuttaman lis�kiihtyvyyden tietylle partikkelille.
    */
    public static Vector acceleration(Particle part) {
	return new Vector(0.f, 0.f);
    }
    
}

