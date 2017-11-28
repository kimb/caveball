package gui;

import core.Input;
import java.awt.event.*;

import org.apache.log4j.Logger;

/**
 * N‰pp‰inpainalluksia KeyListener-rajapinnan avulla lukeva adapteri.
 */
public class InputKeyListener extends KeyAdapter {


	private final Logger log = Logger.getLogger(this.getClass());

	protected Input i;

	/**
	 * @param i Input jolle v‰litet‰‰n tapahtumat
	 */
	public InputKeyListener(Input i) {
		this.i=i;
	}

	/** Kutsuu setDirection:nia k‰sitteleem‰‰n tapahtuman */
	public void keyPressed(KeyEvent e) {
		setDirection(e, true);
	}

	/** Kutsuu setDirection:nia k‰sitteleem‰‰n tapahtuman */
	public void keyReleased(KeyEvent e) {
		setDirection(e, false);
	}

	/**
	 * Vie tapahtuman eteenp‰in inputille.
	 * @param isPressed onko t‰m‰ tapahtuma napin painaminen (vai p‰‰st‰minen).
	 */
	protected void setDirection(KeyEvent e, boolean isPressed) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				i.down(isPressed);
				break;
			case KeyEvent.VK_UP:
				i.up(isPressed);
				break;
			case KeyEvent.VK_RIGHT:
				i.right(isPressed);
				break;
			case KeyEvent.VK_LEFT:
				i.left(isPressed);
				break;
                        case KeyEvent.VK_Q:
                                i.bounceless(isPressed);
                                break;
                        case KeyEvent.VK_W:
                                i.bouncemore(isPressed);
                                break;
                        case KeyEvent.VK_A:
                                i.sizedown(isPressed);
                                break;
                        case KeyEvent.VK_S:
                                i.sizeup(isPressed);
                                break;
                        default:
				// log.debug("Unhandled keypress: "+e);
		}
	}
}
