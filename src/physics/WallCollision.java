package physics;

import core.*;
import util.*;
import vectorizer.*;
import java.util.SortedMap;
import org.apache.log4j.Logger;

/**
 * Seinien ja partikkeleiden väliset törmäilyt hoidetaan täällä.
 * 
 * 
 */
public class WallCollision implements Collision {
    
    private final Logger log = Logger.getLogger(this.getClass());
    
    private final static int NE = 1;
    private final static int E  = 2;
    private final static int SE = 3;
    private final static int N  = 4;
    private final static int X  = 5;
    private final static int S  = 6;
    private final static int NW = 7;
    private final static int W  = 8;
    private final static int SW = 9;
    
    private GameMap place;
    
    private ParticleCollision pc;
    
    private WallCollision lastCollisionPartner;
    
    private float lastCollisionTime = 0;
    
    private Particle part;
    
    private ImageVector wallList[];
    
    private Vector movementLeft;
    private Vector tempMove;
    private Vector tempPosition;
    private Vector reflectWall;      
    private Vector rectangleHeight;   
    private Vector rectangleLength;
    private Vector rectangleCorner;
    private Vector wallAreaCenter;
    private Vector wallAreaCorner;
    private Vector hitWasFoundPosition;
    private Vector moveToHit;
    private Vector PCHitWasFoundPosition;
    private Vector PCMoveToHit;
    private Vector fixPosition;
     
    private boolean lastHitWasWithVertex;
    private boolean particlesCollided = false;
    
    /** Relative collision time, between (0,1). */
    private float rct;
    /** Current time, between (0,1). */
    private float ct;

    private float tempBounceFactor;
    private float radius;
    
    
    /**
    * WallCollisionin constructori ottaa parametrikseen liikevektorin,
    * joka lisäämällä partikkelin paikkaan on se paikka, minne partikkeli
    * kulkisi tällä aika-askeleella ilman esteitä.
    * Ensin haetaan partikkelin liikkeen tiellä olevat seinät, ja tarkistetaan
    * törmätäänkö niihin. Jos ei, niin sitten ei tarvitse tehdä mitään.
    * Jos löytyy törmäysseinä, johon törmätään aika-askeleen sisällä,
    * haetaan seinät partikkelin ympäriltä.
    */
    public WallCollision(Vector movement, Particle particle, GameMap arena, ParticleCollision pac) {
	place = arena;
	fixPosition = new Vector(0,0);
	pc = pac;
	part = particle;
	radius = part.getRadius();
	tempPosition = part.getPosition();
	movementLeft = movement;
	tempMove = movementLeft;
	reflectWall = movementLeft.kCrossProduct(1);
	wallAreaCenter = tempPosition.add(movement.scalarProduct(0.5f));
	//FIXME: keinotekoiset rajat
	wallAreaCorner = new Vector(Math.abs(0.5f * movementLeft.getX()) +10+ 2*radius,
				Math.abs(0.5f * movementLeft.getY()) +10+ 2*radius);
	

	ct = 0.f;
	rct = 1.1f;
	
	wallList = (ImageVector[]) arena.getImageVectors(
		    (int) wallAreaCenter.getX(), (int) wallAreaCenter.getY(), 
		    (int) wallAreaCorner.getX(), (int) wallAreaCorner.getY()
						).toArray(new ImageVector[0]);
	
	

	reInitializeWallCollisions();
	
	float tempRCT = -1;
	/**int count = id;
	Particle[] partList = pc.getParticles();
	while(count < partList.length) {
	    if(part != partList[count]) //FIXME: <--- turha?
		tempRCT = pc.getRelativeCollisionTime(part, partList[count]);
	    if(tempRCT < rct) {
		rct = tempRCT;
		particlesCollided = true;
	    }
	    count++;
	}
	*/
	
	if(rct > 1) {
	    // log.debug("NO COLLISION");
	    /** Ei törmäyksiä seinien kanssa*/
	}
	
	else {	     
	     //log.debug("CRASH");
	     wallAreaCenter = tempPosition.add(tempMove);
	     wallAreaCorner = movementLeft.sub(tempMove);
	     wallAreaCorner = new Vector(Math.abs(wallAreaCorner.getX()) +10+ radius,
				     Math.abs(wallAreaCorner.getY()) +10+ radius);
	    
	     // Törmäys seinän kanssa, haetaan enemmän vektoreita ympäriltä.
	     wallList = (ImageVector[]) arena.getImageVectors(
		    (int) wallAreaCenter.getX(), (int) wallAreaCenter.getY(), 
		    (int) wallAreaCorner.getX(), (int) wallAreaCorner.getY()
						).toArray(new ImageVector[0]);
	     

	}

	// log.debug("walls" + wallList.length);
	
    }
    
    public boolean isInsideWall() {
	int counter = 0;
	wallList = (ImageVector[]) place.getImageVectors((int) part.getPosition().getX(), 
		(int) part.getPosition().getY(), (int) part.getRadius() + 100, (int) part.getRadius() +100).toArray(new ImageVector[0]);
	while(counter < wallList.length) {
	    if(PhysicalCalculation.wallHitDetection(part, wallList[counter]) != null)
		return true;
	    counter++;
	}
	return false;
    }
    
    public void setLastCollisionTime(float lct) {
	lastCollisionTime = lct;
    }
    
    public float getLastCollisionTime() {
	return lastCollisionTime;
    }
    
     public void newPCHitData(Vector hitMove, Vector hitPos) {
	PCHitWasFoundPosition = hitPos;
	PCMoveToHit = hitMove;
    }
    
    public void setPCHitPosition() {
	tempPosition = PCHitWasFoundPosition.add(PCMoveToHit);
	part.setPosition(tempPosition);
    }
    
    public void setLastCollisionPartner(WallCollision partner) {
	lastCollisionPartner = partner;
    } 
    
    public WallCollision getLastCollisionPartner() {
	return lastCollisionPartner;
    }
    
    public void setPC(ParticleCollision pac) {
	pc = pac;
    }
    
    public ParticleCollision getPC() {
	return pc;
    }
    
    public void setReflectWall(Vector reflect) {
	reflectWall = reflect;
    }
    
    public Particle getParticle() {
	return part;
    }
    
    public Vector getMovementLeft() {
	return movementLeft;
    }
    
    public void setMovementLeft(Vector vect) {
	movementLeft = vect;
    }
    
    public void setCT(float newCT) {
	ct = newCT;
    }
    
    /**
    * Sellaisesta seinästä, jonka pisteistä toinen tai molemmat ovat partikkelin 
    * liikesuorakulmion sisällä, ei voi tietää miten pallo tulee törmäämään 
    * siihen (tangentiaalinen törmäys vai kulmaan kosahdus). Tämä metodi 
    * palauttaa törmäyskohdan tällaisen hankalan seinän kanssa.
    * P ja Q viittaavat seinän alku- ja loppupisteisiin, wallX kertoo
    * näiden pisteiden sijoittumisen suorakulmion suhteen. Palautettava 
    * Xposition on paikkavektori, joka tulee lisätä partikkelin nykyiseen
    * paikkaan täsmällisen törmäyskohdan saamiseksi.
    */
    public Vector findUnknownBounceLocation(int wallP, int wallQ, 
	    ImageVector wall) {
		
	Vector centerToP = (wall.getStartPosition()).sub(tempPosition);
	Vector centerToQ = (wall.getStartPosition()).add(wall.getVector());
	centerToQ = centerToQ.sub(tempPosition);
	
	if(wallP == X) {
	    Vector Pposition = 
		PhysicalCalculation.calculateVertexBouncePosition(centerToP, 
							movementLeft, radius);
	    
	    if(wallQ == X) {
		Vector Qposition = 
		    PhysicalCalculation.calculateVertexBouncePosition(centerToQ, 
							    movementLeft, radius);
	    
		if(Qposition.squareLength() < Pposition.squareLength()) {
		    Pposition = Qposition;
		    centerToP = centerToQ;
		}
		
		if(PhysicalCalculation.isByWallSide(wall, tempPosition.add(Pposition)))
		    return PhysicalCalculation.calculateEdgeBouncePosition(wall, 
						centerToP, movementLeft, radius);
	    
		lastHitWasWithVertex = true;
		return Pposition;
	    }
	    
	    else {
		if(PhysicalCalculation.isByWallSide(wall, tempPosition.add(Pposition)))
		    return PhysicalCalculation.calculateEdgeBouncePosition(wall, 
						centerToP, movementLeft, radius);

		else {
		    lastHitWasWithVertex = true;
		    return Pposition;
		}
	    }
	}
	
	else {
	    if(wallQ == X) {
		Vector Qposition = 
			PhysicalCalculation.calculateVertexBouncePosition(
				    centerToQ, movementLeft, radius);
		
		if(PhysicalCalculation.isByWallSide(wall, tempPosition.add(Qposition))) {
		    return PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					centerToQ, movementLeft, radius);
		}
		
		else {
		    lastHitWasWithVertex = true;
		    return Qposition;
		}
	    }
	    
	    /*Virhetilanne. DESTROY PARTICLE?*/
	    else {
		log.debug("findUnknownBounceLocation ihme error ---------------------------------------------------");
		return tempPosition;	    	 
	    }   
	}
	
    }
    
 
    /**
    * Palauttaa vektorin, joka lisättynä partikkelin paikkaan on täsmällinen
    * törmäyskohta argumenttina annetun seinän kanssa. Tämä metodi selvittää 
    * mahdollisen törmäyksen tyypin (tangentiaalinen vai kulmaosuma), sekä
    * laskee apufunktioilla tarkan törmäyskohdan.
    * Jos törmäyskohtaa ei löydy, tämä metodi palauttaa nullin. 
    */
    public Vector seekWallCollisionPosition(ImageVector wall) {
	
	Vector Pposition = wall.getStartPosition();
	Vector Qposition = (wall.getStartPosition()).add(wall.getVector());
	Vector centerToVertex = Pposition.sub(tempPosition);
	
	int p = PhysicalCalculation.mapVertex(Pposition, rectangleHeight, 
					    rectangleLength, rectangleCorner);
	int q = PhysicalCalculation.mapVertex(Qposition, rectangleHeight, 
					    rectangleLength, rectangleCorner);
	
	
	switch(p) {
	    case N:
		switch(q) {
		    case X: case S: //log.debug"seekWallCollisionPosition p:N q:X tai S"); 
		    default: return null;
		}
		
	    case X:
		if(q != N) {
		    /*switch(q){
			case X: log.debug("X - X"); break;
			case S: log.debug("X - S"); break;
			case SE: log.debug("X - SE"); break;
			case SW: log.debug("X - SW"); break;
			case E: log.debug("X - E"); break;
			case W: log.debug("X - W"); break;
			case NE: log.debug("X - NE"); break;
			case NW: log.debug("X - NW"); break;
		    }*/
		    return findUnknownBounceLocation(p, q, wall);
		}
		//FIXME: tällaisia tilanteita pääsee syntymään:
		else //log.debug("seekWallCollisionPosition p:X q N");
		return null;
		
	    case S:
		switch(q)
		{
		    case X: //log.debug("S - X"); 
			return findUnknownBounceLocation(p, q, wall);
		    case W: case NW: case NE: case E: 
			//log.debug("S - joku");
			return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					centerToVertex, movementLeft, radius);
		    case N: //log.debug("seekWallCollisionPosition p:S, q:N");
		    default: return null;
		}
		
	    case SW:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case E: //log.debug("SW - E"); 
			return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
		    
		    
		    case NE: 

			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			    //log.debug("SW-NE"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("SW-NE"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("SW-NE null");

		    default: return null;
		}
		     
	    case SE:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case W: //log.debug("SE - W");
			return    
			PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
		    case NW:
		
			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			//log.debug("SE-NW");
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("SE-NW"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("SE-NW null");

		    default: return null;
		}
		
	    case W:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case S: case SE: case E: //log.debug("W - S/SE/E");
			return
			PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			
		    case NE: 

			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			    //log.debug("W-NE"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("W-NE"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("W-NE null");
			
		    default: return null;
		}
		
	    case E:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case S: case SW: case W: //log.debug("E-S/SW/W"); 
			return
			PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
		    case NW:
			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			    //log.debug("E-NW"); 
			    return 
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("E-NW"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("E-NW null");
			
		    default: return null;
		}
		
	    case NW:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case S: //log.debug("NW-S");
			
			return
			PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			
			
		    case SE: case E:
			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			    //log.debug("NW-SE/E"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}

			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("NW-SE/E"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("NW-SE/E null");

		    default: return null;
		}
		
	     case NE:
		switch(q)
		{
		    case X: return findUnknownBounceLocation(p, q, wall);
		    case S: //log.debug("NE-S")
			
			return
			PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			
		    case SW: case W:
			Vector intersection1 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(0.5f)), 
						    wall.getStartPosition());
			Vector intersection2 = 
			    PhysicalCalculation.calculateIntersectionOfTwoLines(
			    movementLeft, wall.getVector(), part.getPosition().add(rectangleHeight.scalarProduct(-0.5f)), 
						    wall.getStartPosition());			
			
			
			if(intersection1 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection1))) {
			    //log.debug("NE-SW/W"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			
			
			if(intersection2 != null && 
					(PhysicalCalculation.isByWallSide(
					new ImageVector(tempPosition, 
					rectangleLength,1,1), intersection2))) {
			    //log.debug("NE-SW/W"); 
			    return
			    PhysicalCalculation.calculateEdgeBouncePosition(wall, 
					    centerToVertex, movementLeft, radius);
			}
			
			//log.debug("NE-SW/W null");

			
		    default: return null;
		}
		

	}
	
	System.out.println("seekWallCollisionPosition: SHOULD NOT SEE");
	return null;	
    }
    
    
    /**
    * Laskee ensimmäiselle seinätörmäykselle suhteellisen törmäysajankohdan rct
    * (väliltä [0,1]). Tämä luku toimii prioriteettina, kun valitaan kaikkien
    * partikkelien joukosta ensimmäisenä törmäävä partikkeli.
    */
    public void setNewRelativeCollisionTime() {
	float mvs = (tempMove.parallelCompareLength(movementLeft));
	if(mvs < 0) {
	    fixPosition = fixPosition.add(insideWallsFixVector(tempMove));
	    mvs = 0.01f;
	}
	if(mvs == 0) {
	    rct = 1.1f;
	    movementLeft = (part.getVelocity().scalarProduct(-0.0001f));
	    //movementLeft = movementLeft.scalarProduct(0);
	}
	else
	    rct = mvs*(1 - ct) + ct;
	return;
    }
    
    public float getRCT() {
	return rct;
    }
    

    // Asettaa ajan vastaamaan partikkelin tämänhetkistä paikkaa.
    public void setCurrentTime(float time) {
	ct = time;
	return;
    }
    
    /**
    * Käy läpi seinälistan, ja löytää sieltä ensimmäisen törmäyksen.
    * Asettaa Relative collision timen, jota käytetään ylemmällä tasolla.
    */
    public void setFirstWallToHit(ImageVector[] walls) {
	//log.debug("walls seen: " + walls.length);
	int counter = 0;

	Vector temp;
	Vector min = movementLeft.scalarProduct(1.1f);

	
	while(counter < walls.length) {
	    lastHitWasWithVertex = false;
	    temp = seekWallCollisionPosition(walls[counter]);
	    if(temp != null && temp.squareLength() < min.squareLength()) {
		
		/**
		 * Törmäykset seinän ja kulmien kanssa täytyy hoitaa eri
		 * tavalla. Riippuen kumpi tapaus on kyseessä, normaalivektori,
		 * jonka suuntainen nopeuden komponentti käännetään, vaihtelee.
		 */
		if(lastHitWasWithVertex) {
		    Vector newPosition = tempPosition.add(temp);
		    Vector newPositionToVertex = 
			((walls[counter]).getStartPosition()).sub(newPosition);
		    
		    if(newPositionToVertex.squareLength() > 
			    ((newPositionToVertex.add((
				  walls[counter]).getVector())).squareLength())) 
		    {
			
			newPositionToVertex = newPositionToVertex.add((
						    walls[counter]).getVector());
		    }
		    
		    reflectWall = 
			newPositionToVertex.kCrossProduct(1);
		    //log.debug("corner");
		}
		
		else {
		    reflectWall = ((walls[counter]).getVector());
		}
		
		min = temp;
		tempBounceFactor = walls[counter].getBounceFactor();
		walls[counter].updateWall();
		if(rct<0)
		    log.debug(" RCT: "+rct + " Pposition  " + tempPosition.getX() + " " + tempPosition.getY());
	    }
	    counter++;
	}
	//log.debug("Velocity: " + part.getVelocity().getX() + " " + part.getVelocity().getY());
	tempMove = min;
	moveToHit = min;
	hitWasFoundPosition = tempPosition;
	setNewRelativeCollisionTime();
    }

    
    /**
    * Initialisoi WallCollision instanssin ja etsii ensimmäisen törmäysseinän.
    * 
    */
    public void reInitializeWallCollisions() {

	tempBounceFactor = 1.f;	
	
	rectangleHeight = (movementLeft.kCrossProduct(-1)).scalarProduct(
						2*radius/movementLeft.length());   
	rectangleLength = (movementLeft.scalarProduct((movementLeft.length() + 
						radius)/movementLeft.length()));
	rectangleCorner = tempPosition.add(rectangleHeight.scalarProduct(
									0.5f));
	setFirstWallToHit(wallList);	
	return;
    }
    
    
    /**
    * Päivittää törmänneen partikkelin paikan, kimmokerroin kertymän, sekä 
    * liikevektorin suunnan ja pituuden.
    * 
    */
    public void updateParticle() {
	part.setVelocity(PhysicalCalculation.reverseNormalComponent(
	    part.getVelocity().scalarProduct(
			tempBounceFactor*part.getBounceFactor()),reflectWall));
	//log.debug("Velocity: " + part.getVelocity().getX() + " " + part.getVelocity().getY());
	movementLeft = 
	  PhysicalCalculation.reverseNormalComponent(movementLeft, reflectWall);
	part.setTemperature(part.getTemperature() + part.getBounceFactor() * 
					    tempBounceFactor * 
					    part.getVelocity().squareLength() *
					    0.0006f);
	if(impactCausedDeath())
	    destroyParticle();
	tempPosition = 	hitWasFoundPosition.add(moveToHit);  // Tarkka osumakohta?
	part.setPosition(tempPosition);
	reInitializeWallCollisions();
	return;
    }
    
    /**
    * Päivittää partikkelin paikan ja ajanhetken.
    */
    public void updatePosition(float rctNew) {
	float mvs;
	if(ct != 1.f)
	    mvs = (rctNew - ct)/(1.f - ct);
	else mvs = 0.f;
	tempMove = movementLeft.scalarProduct(mvs);
	tempPosition = (tempPosition.add(tempMove));
	part.setPosition(tempPosition);
	movementLeft = movementLeft.sub(tempMove);
	ct = rctNew;
	if(pc != null)
	    pc.setCT(rctNew);

	//int counter = 0;
	//Vector debug = tempPosition.sub(new Vector(300,264));
	
	//log.debug("tempMove length: " + tempMove.length() + " distanceToCorner: " + debug.length());
	
	/**while(counter<wallList.length) {
	    if(PhysicalCalculation.wallHitDetection(part, wallList[counter]) != null) {
		log.debug("INSIDE **************************************");
		log.debug(" Pposition  " + tempPosition.getX() + " " + tempPosition.getY());
		log.debug("Velocity: " + part.getVelocity().getX() + " " + part.getVelocity().getY());
		log.debug("Wstart: " + wallList[counter].getStartPosition().getX() + " " + wallList[counter].getStartPosition().getY() + 
			    " Wend: " + wallList[counter].getStartPosition().add(wallList[counter].getVector()).getX()  + " "  + wallList[counter].getStartPosition().add(wallList[counter].getVector()).getY());
	    }
	    counter++;   
	}*/
	
	//log.debug("tempMove length: " + tempMove.length() + " distanceToCorner: " + debug.length());
	return;
    }
    
    /**
    * Palauttaa truen, jos partikkeli kulkee liian lujaa, muulloin
    * falsen.
    */    
    public boolean impactCausedDeath() {
	Vector orthogonal = reflectWall.kCrossProduct(1);
	float velocity = part.getVelocity().squareLength();
	if(velocity > part.getMaxTemperature()*part.getMaxTemperature())
	    return true;
	else return false;
    }
    
    // Määrää tuhon vaikutukset partikkelille.
    public void destroyParticle() {
	while(part.getVelocity().squareLength() > part.getMaxTemperature() * 
						    part.getMaxTemperature())
	    part.setVelocity(part.getVelocity().scalarProduct(0.7f));  
    }
    
    public Vector insideWallsFixVector(Vector w) {
	float x = w.getX();
	float y = w.getY();
	if(x != 0) {
	    if(y != 0) {
		if(Math.abs(y/x) > 2)
		    return new Vector(0,y/Math.abs(y));
		if(Math.abs(x/y) > 2)
		    return new Vector(x/Math.abs(x),0);
		return new Vector(x/Math.abs(x),y/Math.abs(y));
	    }
	    return new Vector(x/Math.abs(x),0);
	}
	if(y != 0) 
	    return new Vector(0,y/Math.abs(y));
	return new Vector(0,0);
    }
    
    public void fixPosition() {
    	while(fixPosition.squareLength() > 25)
	    fixPosition = fixPosition.scalarProduct(0.5f);
	part.setPosition(part.getPosition().add(fixPosition));		// PURKKAA
    }
    
}
