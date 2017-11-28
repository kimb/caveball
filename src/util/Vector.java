package util;

public class Vector implements java.io.Serializable {
	public static final Vector N = new Vector(0, -1);
	public static final Vector E = new Vector(1, 0);
	public static final Vector S = new Vector(0, 1);
	public static final Vector W = new Vector(-1, 0);
	public static final Vector NE = new Vector(1, -1);
	public static final Vector NW = new Vector(-1, -1);

	protected float x;
	protected float y;
	public Vector(float x1, float y1) {
		x = x1;
		y = y1;
	}

	/**
	 * Kloonaa toinen vektori 
	 */ 
        public Vector(Vector v) {
		x = v.getX();
		y = v.getY();
	}

        public Vector show() {
            System.out.println("Vektorin x ja y: "+x+" "+y);
            return this;
        }
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Vector add(Vector v) {
		return new Vector(x + v.getX(), y + v.getY());
	}

	public Vector sub(Vector v) {
		return new Vector(x - v.getX(), y - v.getY());
	}

	public Vector turnRight() {
		return new Vector(-y, x);
	}

	public Vector turnLeft() {
		return new Vector(y, -x);
	}

	public float dotProduct(Vector v) {
		return v.getX() * x + v.getY() * y;
	}

	public Vector scalarProduct(float i) {
		return new Vector(x * i, y * i);
	}

        public float lenght(){
            return (float) Math.sqrt(x*x+y*y);
        }
        
        public float lenght2(){
            return x*x+y*y;
        }

        
	/**
	 * CrossProduct of this and a vector i*_k_, _k_ unitary vector
	 *
	 * Palauttaa vektorin normaalivektorin. Pituus alkuperäisen
	 * skaalattuna i:llä.
	 */
	public Vector kCrossProduct(float i) {
		return new Vector(y * i, -x * i);
	}

	public float squareLength() {
		return x * x + y * y;
	}

	public float length() {
		return (float) Math.sqrt(squareLength());
	}

	public float vCrossProduct(Vector v) {
		return x * v.getY() - y * v.getX();
	}
	
	/**
	* Kahden yhdensuuntaisen vektorin pituuden vertailu.
	* Vektori v:n oletetaan olevan pitempi.
	*/
	public float parallelCompareLength(Vector v) {
	    if(x==0) {
		if(y==0)
		    return 0;
		else {
		    if(v.getY() != 0)
			return (y/(v.getY()));
		    return 0;
		}    
	    }
	    else {
		if(v.getX() != 0)
		    return (x/(v.getX()));
		return 0;
	    }
	}
        
	public boolean isN() {
		return y < 0;
	}

	public boolean isS() {
		return y > 0;
	}
	public boolean isW() {
		return x > 0;
	}
	public boolean isE() {
		return x < 0;
	}

	public String toString() {
		return "Vector("+x+", "+y+")";
	}
}
