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
 * Prioriteettijono, joka laittaa j�rjestykseen sille sy�tetyt Objektit
 * joihin on assosioitu float v�lilt� [0,1]. Suuremmatkin arvot kelpaavat,
 * mutta ne eiv�t en�� mene j�rjestykseen, vaan ne heitet��n ainoastaan
 * prioriteettijonon h�nnille.
 *
 * Spesifinen otus, t�ss� implementaatiossa prioriteettijonoon saa vain
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
    
    
    // lis�t��n elementti jonoon
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
    
    
    // Palautetaan ensimm�inen alkio.
    public Collision first() {
	return ((Collision) queue.get(0));
    }
    
    // Palautetaan n:s alkio. Metodi first() on yht� kuin getElement(0).
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
