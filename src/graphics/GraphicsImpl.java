package graphics;

import core.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
 * Implementaatio joka käyttää Canvas-luokkaa.
 * <p>
 * Canvas luokan käytöllä, voimme itse hallita tuplapuskuria, joten
 * tarvittaessa voidaan piirtää grafiikkaa myös muiden pelin algoritmien
 * sisältä. Mitään tämmöistä komponenttia ei ole suuniteltu tähän projektiin,
 * mutta tämän mekanismin avulla on mahdollista toteuttaa, esim. pallojen
 * törmäyksestä piirtää kipinöitä, tai muita yhden kuvaruudun efektejä ilman
 * että, kaikkien näiden algoritmien ajoaika olisi Component-luokan
 * update-metodista kutsuttu (tällä taas olisi muita seurauksia, jotka
 * löytää sunin java-tutorialista). 
 * <p>
 * Canvaksen käyttö siis antaa paremman laajennettavuuden, mutta tuottaa vähän
 * enemmän työtä.
 * <p>
 * Käsittelee myös kuvapiirtoalan ajonaikaisen koon muuttumisen.
 * <p>
 * Koska piirtäminen tuplapuskuriin tapahtuu eri säikeessä kuin ruudulle piirtäminem,
 * eikä ole mitään takeita että EventThread olisi ehtinyt piirtää tuplapuskurin ruudulle,
 * synkronisoidaan tuplapuskurin käyttö.
 */
public class GraphicsImpl extends AbstractGraphics 
{
	private final Logger log = Logger.getLogger(this.getClass());
		
	/** Piirtämisen implementoiva komponentti */
	protected Canvas canvas;
	/** Canvakselle tarvittava tuplapuskusri */
	protected BufferedImage dBufferImage;
	/** 
	 * Tuplapuskurille piirtoon käytettävä olio. Tälle on asetettu, 
	 * clip, niin että vain Canvaksella näkyvä osa sen sisällä, kuitenkin
	 * siten että origo on aina näkyvän alueen yläkulmassa.
	 */
	protected Graphics2D dBuffer;

	/** 
	 * Onko tuplapuskuri:
	 * 1. sekä luotu
	 * 2. että saman kokoinen kuin canvas
	 */
	protected boolean dBufferImageInited = false;

	/** Tuplapuskurin synkronisointiin käytettävä olio */
	protected Object dBufferSynchronizer = new Object();

	/**
	 * Luo uuden Graphics-implementaation.
	 * Lisää automaattisesti peruspluginit.
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
	 * Piirtää uuden kuvan komponentille, käyttäen ainoastaan
	 * listättyjä plugineja.
	 */
	public void updateDisplay(GameData gameData) {
		// luo tarvittaessa uusi (oikeankokoinen) tuplapuskuri
		if(!dBufferImageInited) initDoubleBuffer();
		// päivitä tuplapuskurin kuva (yläluokan funktio)
		synchronized(dBufferSynchronizer) {
			renderWithPlugins(dBuffer, gameData);
			// piirrä tuplapuskuri näkyviin
			canvas.repaint();
		}
	}

	/**
	 * Luo uuden oikeankokoisen tuplapuskurin ja sille piirtävän 
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
	 * Kanvas-sisäluokka, joka piirtää kuvansa GraphicsImpl-luokan
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
