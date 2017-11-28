package gui;

import util.*;

import core.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

import org.apache.log4j.BasicConfigurator;

/**
 * Testikäytttöliittymän luova luokka
 */
public class BasicGui {
	final String FRAME_NAME = "Bouncer 1+";

	public static void test() {
		File f = new File(".");
		System.out.println
			("Basedir: "+f.getAbsolutePath());
	}

	/**
	 * Ajaa peruskäyttöliittymän testausta varten.
	 */
	public static void main(String[] args) {
		// Set up a simple configuration that logs on the console.
		BasicConfigurator.configure();
		
		test();
		Controller controller = new Controller();
		controller.loadMap(new MapImageImpl
				(ImageOperations.loadAsBufferedImage("gui/test.png")));
		(new BasicGui()).startDisplaying
			(controller.getGraphics().getDisplay());
		// set up the controller to receive events
		controller.getGraphics().getDisplay().addKeyListener
			(new InputKeyListener(controller.getInput()));
		controller.startSinglePlay();
	}

	/** The main frame */
	JFrame gameFrame;

	/** Aloita pelin näyttäminen */
	public void startDisplaying(Component gameDisplay) {
		// valmistele kehys
		gameFrame = new JFrame(FRAME_NAME);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = gameFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		// lisää gameCanvas
		contentPane.add(gameDisplay, BorderLayout.CENTER);
		// lisää chatpanel
		contentPane.add(createChatPane(),BorderLayout.SOUTH);

		// näytä ne
		gameFrame.pack();
		gameFrame.show();
	}

	/**
	 * Luo chattipaneli.
	 * FIXME: add support for taking listeners as parameters
	 */
	public Component createChatPane() {
		Container pane = new JPanel() {
			public Dimension getPreferredSize(){
				return getMinimumSize();
			}};
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(new JTextField());
		// the textarea
		JTextArea tArea = new JTextArea(5,40);
		tArea.setEditable(false);
		tArea.setLineWrap(true);
		tArea.setWrapStyleWord(true);
		pane.add(new JScrollPane(tArea,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		return pane;
	}
	
}

