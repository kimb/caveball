package core;

/**
 * Partikkelien fysiikan kontrollointi. 
 * Implementointien ei tarvitse olla s‰ieturvallisia (eli voi olettaa,
 * ett‰ kutsutaan vain yht‰ funktiota kerrallaan)
 */
public interface Physics {

	/**
	 * Initialisoidaan fysiikka. 
	 * @param isServer Kertoo toimitaanko serverin‰, eli p‰ivitet‰‰nkˆ
	 * partikkeleiden bounceCount-arvot
	 */
	public void initPhysics(GameData gameData, boolean isServer);

	/**
	 * K‰skee siirt‰m‰‰n kaikkia partikkeleita m‰‰r‰tyn ajan
	 * eteenp‰in.
	 */
	public void moveParticles(int milliSeconds);

	/**
	 * Siirt‰‰ annettuja partikkeleita m‰‰r‰tyn ajan eteenp‰in. Huomaa ett‰
	 * parametriksi annettu pallo voi olla siiretty toisen pallon sis‰lle
	 * esim. pingin vaikutuksesta. HUOM! t‰m‰n funktion t‰ytyy p‰ivitt‰‰
	 * gameDataan samalla id:ll‰ olevan partikkelin tiedot.
	 * @param delayedPosition Partikkelin sijainti v‰h‰n aikaa sitten.
	 * 	Huomaa, ett‰ t‰ll‰ ja jollain partikkelilla GameDatassa
	 * 	on siis sama id. Voi sis‰lt‰‰ nulleja, tai olla nollan pituinen.
	 * @param delayOfPosition Kuinka kauan sitten annettu sijainti oli
	 * 	t‰sm‰llinen.
	 */
	public void moveParticles(Particle[] delayedPositions, 
			int delayOfPosition);


	/**
	 * Palauttaa olion jossa on getterit ja setterit fysikaalisille
	 * parametreille.
	 * @return Papu jonka gettereit‰ ja settereit‰ voi muutella.
	 */
	public Object getPhysicalPropertiesBean();


}
