package util;

import javax.swing.*;

/** 
 * Kirjastometodeja k�ytt�liittym�n tekemiseen.
 */
public class SwingTools {

	/**
	 * Asettaa annetulle komponentille reunan.
	 * @param title Reunaan laitettava otsikko.
	 */
	public static void setBorder(JComponent comp, String title) {
		comp.setBorder(BorderFactory.createTitledBorder(title));
	}
}
	
