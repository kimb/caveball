package util;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;

/**
 * Yleisiä metodeja kuvien/grafiikan kanssa työskentelyyn.
 */
public abstract class ImageOperations {

	public static final String IMAGE_PREFIX = "build/classes/";

	/**
	 * Lataa annetun ikonin käyttäen suhteellista polkua.
	 */
	public static ImageIcon getIcon(String path) {
		return new ImageIcon(IMAGE_PREFIX+path);
	}
	
	/**
	 * Lukee annetusta tiedostosta kuvan.
	 */
	public static Image loadImage(String path) {
		ImageIcon ii = new ImageIcon(IMAGE_PREFIX+path);
		return ii.getImage();
	}

	/**
	 * Wrapperi
	 */
	public static BufferedImage loadAsBufferedImage(String path) {
		System.out.println("loading image: "+IMAGE_PREFIX+path);
		return loadAsBufferedImage(new File(IMAGE_PREFIX+path));
	}

	/**
	 * Lukee kuvan annetusta tiedostosta ja palauttaa sen
	 * BufferedImage-oliona.
	 */
	public static BufferedImage loadAsBufferedImage(File file) {
		ImageIcon ii = new ImageIcon(file.getPath());
		return asBufferedImage(ii.getImage());
	}

	/** Tekee puskuroitavan kuvan tavallisesta kuvasta. */
	public static BufferedImage asBufferedImage(Image i) {
		trackImage(i);
		BufferedImage bi = new BufferedImage(
				i.getWidth(null),
				i.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		g.drawImage(i,0,0,null);
		g.dispose();
		return bi;
	}
	
	/**
	 * Lataa annetun kuvan kokonaisuudessaan.
	 */
	public static void trackImage(Image i) {
		MediaTracker t = new MediaTracker(new Canvas());
		t.addImage(i,0);
		try {
			t.waitForAll();
		} catch(InterruptedException e) {
			// FIXME: log
		}
	}

}
