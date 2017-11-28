package vectorizer;
/** Palauttaa Vectorizerille tietoa siitä, miten kuvaa tulee tulkita. */
public interface VectorizationParameters {
	
	/** Palauttaa eri alueiden värien listan.
         * Ensimmäisenä on seinän väri, ja sitten jonkin tyyppisen tyhjän alueen väri.
         * Jos kohdassa (x,y) olevaa väriä ei ole tässä listassa, kohdellaan sitä seinänä.
         */
	public int[] getZoneColors();
        public int getStartLocationColor();
	
}
