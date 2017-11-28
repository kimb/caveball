/*
 * QueueElement.java
 *
 * Created on 9. huhtikuuta 2004, 18:52
 */

package util;

import physics.*;

/**
 *
 * @author  TMT
 */
public class QueueElement {
    
    private float priority;
    private Object element;
    
    /** Creates a new instance of QueueElement */
    public QueueElement(Object data, float importance) {
	element = data;
	priority = importance;
    }
    
    public Object getElement() {
	return element;
    }
    
    public float getElementPriority() {
	return priority;
    }
    
}
