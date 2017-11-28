package graphics;

import core.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
 * Implementaatio joka k�ytt�� Canvas-luokkaa.
 * <p>
 * Canvas luokan k�yt�ll�, voimme itse hallita tuplapuskuria, joten
 * tarvittaessa voidaan piirt�� grafiikkaa my�s muiden pelin algoritmien
 * sis�lt�. Mit��n t�mm�ist� komponenttia ei ole suuniteltu t�h�n projektiin,
 * mutta t�m�n mekanismin avulla on mahdollista toteuttaa, esim. pallojen
 * t�rm�yksest� piirt�� kipin�it�, tai muita yhden kuvaruudun efektej� ilman
 * ett�, kaikkien n�iden algoritmien ajoaika olisi Component-luokan
 * update-metodista kutsuttu (t�ll� taas olisi muita seurauksia, jotka
 * l�yt�� sunin java-tutorialista). 
 * <p>
 * Canvaksen k�ytt� siis antaa paremman laajennettavuuden, mutta tuottaa v�h�n
 * enemm�n ty�t�.
 * <p>
 * K�sittelee my�s kuvapiirtoalan ajonaikaisen koon muuttumisen.
 * <p>
 * Koska piirt�minen tuplapuskuriin tapahtuu eri s�ikeess� kuin ruudulle piirt�minem,
 * eik� ole mit��n takeita ett� EventThread olisi ehtinyt piirt�� tuplapuskurin ruudulle,
 * synkronisoidaan tuplapuskurin k�ytt�.
 */
public class GraphicsImpl extends AbstractGraphics 
{
	private final Logger log = Logger.getLogger(this.getClass());
		
	/** Piirt�misen implementoiva komponentti */
	protected Canvas canvas;
	/** Canvakselle tarvittava tuplapuskusri */
	protected BufferedImage dBufferImage;
	/** 
	 * Tuplapuskurille piirtoon k�ytett�v� olio. T�lle on asetettu, 
	 * clip, niin ett� vain Canvaksella n�kyv� osa sen sis�ll�, kuitenkin
	 * siten ett� origo on aina n�kyv�n alueen yl�kulmassa.
	 */
	protected Graphics2D dBuffer;

	/** 
	 * Onko tuplapuskuri:
	 * 1. sek� luotu
	 * 2. ett� saman kokoinen kuin canvas
	 */
	protected boolean dBufferImageInited = false;

	/** Tuplapuskurin synkronisointiin k�ytett�v� olio */
	protected Object dBufferSynchronizer = new Object();

	/**
	 * Luo uuden Graphics-implementaation.
	 * Lis�� automaattisesti peruspluginit.
	 */
	public GraphicsImpl() {
		canvas = new Kanvas();
		canvas.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				dBufferImageInited = false;
				canvas.invalidate();
			}
		});
		addRendererPlugin(new MapRendererPlugin());
		addRendererPlugin(new ParticleRendererPlugin());
		//addRendererPlugin(new MapVectorRendererPlugin());
		addRendererPlugin(new HeatRendererPlugin());
	}
	
	/**
	 * Implementoitu java.awt.Canvaksen avulla.
	 */
	public Component getDisplay() {
		return canvas;
	}

	/**
	 * Piirt�� uuden kuvan komponentille, k�ytt�en ainoastaan
	 * list�ttyj� plugineja.
	 */
	public void updateDisplay(GameData gameData) {
		// luo tarvittaessa uusi (oikeankokoinen) tuplapuskuri
		if(!dBufferImageInited) initDoubleBuffer();
		// p�ivit� tuplapuskurin kuva (yl�luokan funktio)
		synchronized(dBufferSynchronizer) {
			renderWithPlugins(dBuffer, gameData);
			// piirr� tuplapuskuri n�kyviin
			canvas.repaint();
		}
	}

	/**
	 * Luo uuden oikeankokoisen tuplapuskurin ja sille piirt�v�n 
	 * Graphics2D:n
	 */
	protected void initDoubleBuffer() {
		// siivoa vanha pois
		if(dBuffer != null) {
			dBuffer.dispose();
		}
		// luo uusi
		if( canvas.getWidth() <=0 ||  canvas.getHeight() <= 0) {
			log.info("Graphics canvas is zero-sized");
			return;
		}
		synchronized(dBufferSynchronizer) {
			dBufferImage = new BufferedImage(
					canvas.getWidth(),
					canvas.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			dBuffer = dBufferImage.createGraphics();
			dBuffer.setClip(0, 0, canvas.getWidth(), canvas.getHeight());
			dBufferImageInited = true;
			log.info("Created new doublebuffer with size: "+
					canvas.getWidth()+"x"+canvas.getHeight());
		}
	}
	

	/**
	 * Kanvas-sis�luokka, joka piirt�� kuvansa GraphicsImpl-luokan
	 * tuplapuskurista
	 */
	protected class Kanvas extends Canvas {
		private Kanvas() { }
		public void paint(java.awt.Graphics g) {
			update(g);
		}
		public void update(java.awt.Graphics g) {
			if(dBufferImage != null) {
				synchronized(dBufferSynchronizer) {
					if(!g.drawImage(dBufferImage, 0, 0, this)) {
						log.warn("could not draw the complete image to the map");
					}
				}
			}
		}

		protected Dimension size = new Dimension(640,480);
		
		public Dimension getMinimumSize() {
			return new Dimension(15,15);
		}
		public Dimension getPreferredSize() {
			return size;
		}
		public Dimension getMaximumSize() {
			return new Dimension(1200,1600);
		}

		public void setSize(Dimension size) {
			this.size = size;
		}
	}
}
