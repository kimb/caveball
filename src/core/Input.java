package core;

/**
 * Controllerin rajapinta ohjausk�skyjen vastaanottamiseen.
 */
public interface Input {

	/** Ohjauksen statuksen asetus */
	public void up(boolean isPressed);
	/** Ohjauksen statuksen asetus */
	public void down(boolean isPressed);
	/** Ohjauksen statuksen asetus */
	public void right(boolean isPressed);
	/** Ohjauksen statuksen asetus */
	public void left(boolean isPressed);
        /** Muuttaa s�dett�. */
        public void sizeup(boolean isPressed);
        /** Muuttaa s�dett�. */
        public void sizedown(boolean isPressed);
        /** Muuttaa kimmokerrointa. */
        public void bouncemore(boolean isPressed);
        /** Muuttaa kimmokerrointa. */
        public void bounceless(boolean isPressed);
}
