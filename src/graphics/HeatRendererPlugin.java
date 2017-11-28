package graphics;

import core.*;
import util.*;
import java.awt.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
	 * Piirtää partikkelin kuumuuden.
	 * Pienenä palkkina alareunaan.
	 */
public class HeatRendererPlugin extends DefaultRendererPluginBase {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	/** Palkkin tila reunaan */
	protected int INDENT = 5;
	/** Palkin paksuus */
	protected int HEIGHT = 3;
	
	public void render(Graphics2D g, GameData gameData) {
		Rectangle bounds = g.getClipBounds();
		Particle part = gameData.getPlayerParticle();
		if(part == null) {
			return;
		}
		float heat = part.getTemperature();
		float maxHeat = part.getMaxTemperature();
		// minne palkki piirretään
		int origoY = bounds.height-INDENT-HEIGHT;
		int origoX = INDENT;
		int barLenth = (int) ((bounds.width - 2*INDENT)*heat/maxHeat);
		// väri muutuu punaisemmaksi
		Color c = new java.awt.Color(
				(int) limit(255.0*heat/maxHeat),
				(int) limit(255.0*(1-heat/maxHeat)),
				0,
				150);
		g.setColor(c);
		g.fillRect(origoX, origoY, barLenth, HEIGHT);
	}
	
	/** rajottaa argumentin värille sallittuihin arvoihin */
	protected int limit(double val) {
		if(val>=255) {
			return 255;
		}
		if(val<0) {
			return 0;
		}
		return (int) val;
	}

}
