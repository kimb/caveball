/*
 * PriorityQueue.java
 *
 * Created on 9. huhtikuuta 2004, 18:53
 */

package util;

import java.util.ArrayList;
import java.util.Iterator;
import physics.Collision;

/**
 * Prioriteettijono, joka laittaa järjestykseen sille syötetyt Objektit
 * joihin on assosioitu float väliltä [0,1]. Suuremmatkin arvot kelpaavat,
 * mutta ne eivät enää mene järjestykseen, vaan ne heitetään ainoastaan
 * prioriteettijonon hännille.
 *
 * Spesifinen otus, tässä implementaatiossa prioriteettijonoon saa vain
 * Collision rajapinnnan toteuttavia luokkia.
 *
 * @author  TMT
 */
public class PriorityQueue {
    
    public ArrayList queue;
    
    /** Creates a new instance of PriorityQueue */
    public PriorityQueue() {
	queue = new ArrayList();
    }
    
    
    // lisätään elementti jonoon
    public void add(Collision element) {
	int index = 0;
	float priority = element.getRCT();
	if(priority > 1.f) {
	    index = queue.size();
	    queue.add(element);
	}
	else {    
	    Iterator iter = queue.listIterator();
	    while(iter.hasNext()) {
	        if(((Collision) iter.next()).getRCT() >= priority) {
		    break;
	        }
	        index++;
	    }
	    queue.add(index, element);
	}
    }
    
    
    // Palautetaan ensimmäinen alkio.
    public Collision first() {
	return ((Collision) queue.get(0));
    }
    
    // Palautetaan n:s alkio. Metodi first() on yhtä kuin getElement(0).
    public Object getElement(int n) {
	return ((Collision) queue.get(n));
    }
    
    public Collision getThis(Collision element) {
	int n = queue.indexOf(element);
	if(n != -1)
	    return ((Collision) queue.get(n));
	else return null;
    }
    
    public void removeFirst() {
	queue.remove(0);
    }
    
    public void removeThis(Collision element) {
	int n = queue.indexOf(element);
	if(n != -1)
	    queue.remove(n);
    }
    
    public boolean isNotEmpty() {
	return !(queue.isEmpty());
    }    
    
    public int queueSize() {
	return queue.size();
    } 
    
    public Collision[] toArray(Collision[] cArray) {
	return ((Collision[]) queue.toArray(cArray));
    }
    
}
