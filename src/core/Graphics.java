package core;

import java.awt.Component;

/**
 * Määrittelee controllerin piirtämiseen käyttämän rajapinnan.
 */
public interface Graphics {
	
	/**
	 * Palauttaa komponentin johon pelitilanne piirretään
	 */
	public Component getDisplay();

	/**
	 * Päivittää komponentilla olevan pelitilanteen.
	 */
	public void updateDisplay(GameData gameData);
}
