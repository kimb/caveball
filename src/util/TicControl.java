package util;

/**
 * Yksinkertainen ajastin.
 * Luodessa kerrotaan kuinka isoja aika-askeleita otetaan.
 * nextTic()-metodi palauttaa v‰hint‰‰n luodessa annetun ajan ellei edellisest‰
 * kutsusta ole kulunut aika-askelta pitempi aika.
 * <p>
 * Huomaa ett‰ kutsujan t‰ytyy olla synkronoitu t‰m‰n olion suhteen.
 */
public class TicControl {

	/** koska edellinen Aika-askeleen aloitus tehtiin. */
	protected long lastTicStart;

	/** Aika askeleen pituus */
	protected int timeStep;

	/**
	 * Luo uuden TicControl:in.
	 * @param timeStep Aika-askeleen pituus.
	 */
	public TicControl(int timeStep) {
		this.timeStep = timeStep;
		lastTicStart = System.currentTimeMillis();
	}

	/**
	 * Odottaa seuraava askelta, jos edellisest‰ kutsusta tai olion luomisesta
	 * on kulunut alle aika-askeleen m‰‰r‰ aikaa. Jos aikaa on kulunut enemm‰n
	 * kuin aika-askeleen verran metodi palaa heti, ja kompensoi menetetty‰ 
	 * aikaa palauttamalla isomman arvon.
	 * <p>
	 * HUOM! kutsuvan s‰ikeen on oltava synkronisoitu t‰m‰n olion suhteen.
	 * @return Seuraavan askeleeseen kuluva aika.
	 */
	public int nextTic() {
		long currtime = System.currentTimeMillis();
		int lastTicTime = (int) (currtime - lastTicStart);
		if(timeStep < lastTicTime) {
			// ollaan j‰‰ty per‰‰n
			lastTicStart = currtime;
			System.out.println(lastTicTime + " trying to catch up (limited to 200)");
			return lastTicTime<200?lastTicTime:200;
		} else {
			try {
				// 10 on kokeellisesti saatu paras approximaatio
				// wait-operaation lis‰tyˆlle.
				int waitTime = timeStep - lastTicTime - 10;
				if(waitTime > 0) {
					if(waitTime > 1000) {
						System.out.println("Going to wait: "+waitTime);
					}
					wait(waitTime);
				}
			} catch(Exception e) { }
			// TODO: check that wait waited the right time
			lastTicTime = (int) (System.currentTimeMillis() - lastTicStart);
			lastTicStart = System.currentTimeMillis();
			// System.out.println(lastTicTime);
			return lastTicTime;
		}
	}
}
	
