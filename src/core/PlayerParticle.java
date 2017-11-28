package core;

import util.Vector;

import java.awt.*;

/**
 * Partikkeli joka on pelaaja.
 */
public class PlayerParticle extends AbstractParticle {

	/** Oletusväri */
	public static final Color DEFAULT_COLOR = Color.getHSBColor(1,0.7f,0.38f);

	/** Pallon väri */
	protected Color color;

	/** Pelaajan nimi*/
	protected String playername;

	/**
	 * Luo partikkelille järkevät alkuarvot
	 */
	public PlayerParticle(String playerName) {
		temp = 0;
		radius = 15;
		bounceFactor = (float) 1.0f;
		maxTemp = 120;
		mass = 10.f;
		steeringForce = 200.f;
		velocity = new Vector(-10.f,-10.f);
		color = DEFAULT_COLOR;
                increaseRadius=0;
                increaseBounceFactor=0;
	}

	/** Palauttaa pelaajan nimen */
	public String getPlayername() {
		return playername;
	}


	/** Palauttaa pelaajan pallon värin */
	public Color getColor() {
		return color;
	}


	/** Asettaa pelaajan pallon värin */
	public void setColor(Color color) {
		this.color = color;
	}
}
