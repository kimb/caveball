package physics;

import util.*;
import java.beans.*;
import org.apache.log4j.Logger;

/**
 * Painovoima, ilmanvastus ja muut maailmalle yhteiset ominaisuudet tallennettu 
 * tänne. 
 *
 * @author  TMT
 */
public class PhysicalProperties extends Object implements java.io.Serializable {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    private Vector gravity;  
    
    public Vector getGravity() {
	return gravity;
    }
    
    public void setGravity(Vector value) {
	gravity = value;
    }
    
    private float airResistance;
    
    public float getAirResistance() {
	return airResistance;
    }
    
    public void setAirResistance(float value) {
	airResistance = value;
    }
    
}
