package vectorizer;
/** Palauttaa Vectorizerille tietoa siit�, miten kuvaa tulee tulkita. */
public interface VectorizationParameters {
	
	/** Palauttaa eri alueiden v�rien listan.
         * Ensimm�isen� on sein�n v�ri, ja sitten jonkin tyyppisen tyhj�n alueen v�ri.
         * Jos kohdassa (x,y) olevaa v�ri� ei ole t�ss� listassa, kohdellaan sit� sein�n�.
         */
	public int[] getZoneColors();
        public int getStartLocationColor();
	
}
