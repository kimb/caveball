package graphics;

import core.*;
import util.*;
import java.awt.*;
import java.awt.image.*;

import org.apache.log4j.Logger;

/**
 * Piirt‰‰ kaikki partikkelit.
 */
public class ParticleRendererPlugin extends DefaultRendererPluginBase {

	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Piirt‰‰ kaikki partikkelit.
	 */
	public void render(Graphics2D g, GameData gameData) {
		Vector origin = getOriginPosition(gameData, g.getClipBounds());
		Particle[] parts = gameData.getParticles();
		for(int i=0; i<parts.length; i++) {
			renderParticle(g, parts[i], origin);
		}
	}

	protected void renderParticle(Graphics2D g, Particle p, Vector origin) {
		if(p instanceof PlayerParticle) {
			g.setColor(((PlayerParticle)p).getColor());
		} else {
			g.setColor(Color.red.darker());
		}
		Vector relPos = p.getPosition().sub(origin);
		//log.debug("drawing particle to: ("+(int)relPos.getX()+","+(int)relPos.getY()+")"+ " with bounds: "+g.getClipBounds());
		g.fillArc((int)relPos.getX()-(int)p.getRadius(),
				(int)relPos.getY()-(int)p.getRadius(),
				(int)p.getRadius()*2, (int)p.getRadius()*2,
				0, 360);
	}
}
