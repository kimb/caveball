package graphics;

import core.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Erillisi‰ renderˆintiosia tukeva grafiikan implementaation kanta.
 */
public abstract class AbstractGraphics implements core.Graphics {

	/** Sis‰lt‰‰ piirt‰misen suorittavat RendererPlugin:it */
	protected ArrayList renderers = new ArrayList();
	
	/**
	 * Lis‰‰ piirtorutiini.
	 */
	public void addRendererPlugin(RendererPlugin plug) {
		renderers.add(plug);
	}

	/**
	 * Poista piirtorutiini.
	 */
	public void delRendererPlugin(RendererPlugin plug) {
		renderers.remove(plug);
	}

	/**
	 * Palauttaa kaikki piirtorutiinit.
	 */
	public RendererPlugin[] listRendererPlugins() {
		return (RendererPlugin[]) renderers.toArray(
				new RendererPlugin[renderers.size()]);
	}

	/**
	 * K‰ytt‰en lis‰ttyj‰ palikoita, piirrett‰‰n kuva.
	 * @param graphics Taso jolle piirret‰‰n.
	 * @param GameData Pelidata, josta tilanne piirret‰‰n.
	 * @param origin Kertoo miss‰ piirett‰v‰n grafiikan origo on.
	 */
	public void renderWithPlugins(Graphics2D graphics, GameData gameData)
	{
		Object plugin;
		for(Iterator i = renderers.iterator(); i.hasNext();)
		{
			plugin=i.next();
			((RendererPlugin)plugin).render(graphics, gameData);
		}
	}
	
}
