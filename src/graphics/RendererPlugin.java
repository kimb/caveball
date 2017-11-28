package graphics;

import core.*;
import java.awt.*;
import java.awt.image.*;

/**
 * M‰‰rittelee komponentin joka osaa piirt‰‰ 2-ulotteista grafiikkaa
 */
public interface RendererPlugin {
	
	/**
	 * Piirt‰‰ kompnentin kohteet annetulla Graphics2D-oliolle.
	 */
	public void render(Graphics2D g, GameData gameData);

}
