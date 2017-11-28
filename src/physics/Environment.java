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
 * Sisältää ympäristötekijöitä, kuten ajan mukana muuttuvia voimakenttiä jne.
 *
 * @author  TMT
 */
public class Environment {   
    
    private final Logger log = Logger.getLogger(this.getClass());
    public Vector acceleration;
    
    /** Creates a new instance of Environment */
    public Environment() {
	
    }
    
    /** Päivittää kiihtyvyysvektorin ja ajanhetken. */
    public void updateForces(Particle part) {
    
    }
    
    /** Palauttaa ympäristön aiheuttaman lisäkiihtyvyyden tietylle partikkelille.
    */
    public static Vector acceleration(Particle part) {
	return new Vector(0.f, 0.f);
    }
    
}

