package core;

import java.awt.Component;

/**
 * M‰‰rittelee controllerin piirt‰miseen k‰ytt‰m‰n rajapinnan.
 */
public interface Graphics {
	
	/**
	 * Palauttaa komponentin johon pelitilanne piirret‰‰n
	 */
	public Component getDisplay();

	/**
	 * P‰ivitt‰‰ komponentilla olevan pelitilanteen.
	 */
	public void updateDisplay(GameData gameData);
}
