package core;

import java.awt.event.KeyListener;

/**
 * Ohjauskäskyjä vastaanottava implementaatio.
 * Asettaa annetut käskyt pelaajan pallolle. Tämän ei tarvitse lukita 
 * gameDataa päivitystä varten, koska yksikään toinen säije ei päivitä
 * samaa dataa.
 */
public class InputImpl implements Input {

	GameData gameData;

	/**
	 * Luo uuden annettuun gameDataan sidotun syötteenkäsittelijän.
	 */
	public InputImpl(GameData gameData) {
		this.gameData = gameData;
	}

	/** Ohjauksen statuksen asetus */
	public void up(boolean isPressed) {
		updatePlayerParticle(Particle.STEERING_UP, isPressed);
	}
	/** Ohjauksen statuksen asetus */
	public void down(boolean isPressed) {
		updatePlayerParticle(Particle.STEERING_DOWN, isPressed);
	}
	/** Ohjauksen statuksen asetus */
	public void right(boolean isPressed) {
		updatePlayerParticle(Particle.STEERING_RIGHT, isPressed);
	}
	/** Ohjauksen statuksen asetus */
	public void left(boolean isPressed) {
		updatePlayerParticle(Particle.STEERING_LEFT, isPressed);
	}

	/** Päivittää pelaajan partikkelin ohjausta */
	protected void updatePlayerParticle(Integer particle_steering, boolean
			isEnabled) {
		Particle p = gameData.getPlayerParticle();
		if( p==null ) return;
                if (particle_steering.intValue()<=4)
                    p.setSteering(particle_steering, isEnabled);
                else
                    p.setQuality(particle_steering, isEnabled);
	}
        /** Määritelty interfacessa... */
        public void bounceless(boolean isPressed) {
                updatePlayerParticle(Particle.DECREASE_BOUNCE, isPressed);
        }        

        /** Määritelty interfacessa... */
        public void bouncemore(boolean isPressed) {
                updatePlayerParticle(Particle.INCREASE_BOUNCE, isPressed);
        }
        
        /** Määritelty interfacessa... */
        public void sizedown(boolean isPressed) {
                updatePlayerParticle(Particle.DECREASE_RADIUS, isPressed);
        }
        
        /** Määritelty interfacessa... */
        public void sizeup(boolean isPressed) {
                updatePlayerParticle(Particle.INCREASE_RADIUS, isPressed);
        }
        
}
