package core;

/**
 * M��rittelee Controllerille lis�tt�v�n moduulin.
 */
public interface Module {

	/**
	 * Aina kun palloja on siirretty yhden aikav�lin verran, kutsutaan
	 * t�t� metodia.
	 * Jos t�m� moduuli aikoo muuttaa gameDataa omassa s�ikees�,
	 * sen on synkronoitava gameData:n suhteen.
	 */
	public void tic(GameData gameData, int timestep);

	/**
	 * Kutsutaan kun verkosta saapuu paketti. Huomaa, ett kutsu ei 
	 * ole synkronisoitu, joten t�t� voidaan kutsua samaan aikaan kuin
	 * esim. tic-funktiota. GameData:n lukitsemiseen p�tev�t samat s��nn�t.
	 */
	public void packetRecieved(NetworkPacket packet);

	/**
	 * Tulostaa modulin nimen ja tiedot.
	 */
	public String toString();
}
