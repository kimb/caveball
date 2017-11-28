/*
 * Area.java
 *
 * Created on 7. huhtikuuta 2004, 20:05
 */

package vectorizer;
import java.util.HashSet;
import java.util.Collection;
import util.*;
import core.*;
import java.util.Iterator;
import physics.*;
/**
 * Luokka partikkeleiden ja ImageVectoreiden säilytykseen.
 * @author  Aleksi
 */
public class Area {
    private HashSet imageVectors;
    private HashSet wallCollisions;
    private HashSet particles;
    private Vector start;
    private int width, heigth;
    /** Luo uuden Area:n. Start kertoo, missä sijaitsee fyysisesti. */
    public Area(int widthParam, int heigthParam, Vector positionParam) {
        imageVectors=new HashSet();
        wallCollisions=new HashSet();
        particles=new HashSet();
        start=positionParam;
        width=widthParam;
        heigth=heigthParam;
        
    }



    /** Laittaa uuden ImageVector:in alueen muistiin. */
    public void putImageVector(ImageVector iv){
        imageVectors.add(iv);
    }
    /** Laittaa uuden partikkelin alueen muistiin.*/
    public void putWallCollision(WallCollision wc){
        wallCollisions.add(wc);
    }
    public void putParticle(Particle p){
        particles.add(p);
    }
    /** Poistaa WallCollisionit. */
    public void refreshWallCollisions(){
        wallCollisions=new HashSet();
    }
    public void refresh(){
        wallCollisions=new HashSet();
        particles=new HashSet();
    }
    /** Laittaa uudeksi alueen partikkeleiksi annetut partikkelit. */
    public void refreshWallCollisions(Collection newWallCollisions){
        wallCollisions=new HashSet(newWallCollisions);
    }
    
    public void refreshParticles(Collection newParticles){
        particles=new HashSet(newParticles);
    }
    public void refreshParticles(){
        particles=new HashSet();
    }
    /** Palauttaa kaikki alueen partikkelit. */
    public HashSet getWallCollisions(){
        return wallCollisions;
    }
    public HashSet getParticles(){
        return particles;
    }
    /** Palauttaa alueessa olevat ImageVector:it. */
    public HashSet getImageVectors(){
        return imageVectors;
    }
  
}
