package core;

/**
 * Määrittelee Controllerille lisättävän moduulin.
 */
public interface Module {

	/**
	 * Aina kun palloja on siirretty yhden aikavälin verran, kutsutaan
	 * tätä metodia.
	 * Jos tämä moduuli aikoo muuttaa gameDataa omassa säikeesä,
	 * sen on synkronoitava gameData:n suhteen.
	 */
	public void tic(GameData gameData, int timestep);

	/**
	 * Kutsutaan kun verkosta saapuu paketti. Huomaa, ett kutsu ei 
	 * ole synkronisoitu, joten tätä voidaan kutsua samaan aikaan kuin
	 * esim. tic-funktiota. GameData:n lukitsemiseen pätevät samat säännöt.
	 */
	public void packetRecieved(NetworkPacket packet);

	/**
	 * Tulostaa modulin nimen ja tiedot.
	 */
	public String toString();
}
