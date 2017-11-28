package gui;

import util.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import javax.swing.event.*;
import core.*;

/** Launcherin eri ikkunoiden hoitamiseen k‰ytt‰m‰ rajapinta. */
public class AbstractWindow extends JPanel {

	/** K‰ytett‰v‰ lokalisointi */
	protected Resources messages;

	/** 
	 * Uuden abstraktin ikkunan luonti.
	 * @param messages Lokalisointi.
	 * */
	public AbstractWindow(Resources messages) {
		this.messages = messages;
	}

	/* Uuden abstraktin ikkunan luonti. 
	 * Ilman t‰m‰n luokan tarjoamaa lokalisointia.
	 */
	public AbstractWindow() { }

	/** Kutsutaan kun kyseinen ikkuna aijotaan n‰ytt‰‰ */
	public void start() { }

	/** Kutsutaan kun kyseinen ikkuna aijotaan piillottaa */
	public void stop() { }

}
