package gui;

import util.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import core.*;

/** P‰‰valikko ja pelaajan asetukset n‰ytt‰v‰ ikkuna. */
public class MainWindow extends AbstractWindow {

	/** V‰riesikatselun koko (mustalla pomppiva pallo) */
	public static final int PREVIEW_W = 340;
	/** V‰riesikatselun koko (mustalla pomppiva pallo) */
	public static final int PREVIEW_H = 60;
	/** V‰riesikatselun reunan koko */
	public static final int BORDER = 7;

	/** kutsuu init():i‰ */
	public MainWindow(Resources messages) {
		super(messages);
		init();
	}

	/** vierekk‰in olevat napit. */
	protected JPanel buttonPane;
	/** Serverin aloittaminen-nappi */
	public JButton startServerButton;
	/** Peliin liittyminen-nappi */
	public JButton joinGameButton;
	/** Peliin lopettaminen-nappi */
	public JButton quitButton;
	
	/** Pelaajan asetukset. */
	protected JPanel configPane;
	/** Pelaajan v‰ri */
	public JColorChooser playerColor;
	/** Pelaajan nimi */
	public JTextField playerName;
	/** V‰rivalitsijan esikatselu. */
	protected Controller controller;
	/** Esikatselun graafinen komponentti */
	protected JPanel colorPreviewPane;


	/**
	 * Aloittaa pallon liikuttamisen preview-panessa.
	 */
	public void start() {
		controller.getGraphics().getDisplay().setSize(
				new Dimension(PREVIEW_W, PREVIEW_H));
		controller.startSinglePlay();
	}

	/**
	 * Lopettaa previev panen liikkeen
	 */
	public void stop() {
		controller.reset();
	}
	
	/** Luo tarvittavat komponentit*/
	protected void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel title = new JLabel(ImageOperations.getIcon(
					messages.getString("GAME_LOGO")));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		// kaksi alempaa pane:a sis‰lt‰v‰
		JPanel lower = new JPanel();
		lower.setLayout(new BoxLayout(lower, BoxLayout.Y_AXIS));
		createButtonPane();
		createConfigPane();
		lower.add(configPane);
		lower.add(buttonPane);
		this.add(title);
		this.add(lower);
		this.add(Box.createGlue());
	}

	/** Apufunktio init():ille. Luo napit. */
	protected void createButtonPane() {
		buttonPane = new JPanel();
		JPanel gamePane = new JPanel();
		SwingTools.setBorder(gamePane, messages.getString(
					"Entering the game"));
		gamePane.setLayout(new BoxLayout(gamePane, BoxLayout.X_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		startServerButton = new JButton(messages.getString
				("Start a new game"));
		joinGameButton = new JButton(messages.getString
				("Join an existing game"));
		quitButton = new JButton(
				messages.getString("Exit")
				+" "+
				messages.getString("Caveball"));
		quitButton.setAlignmentY(Component.TOP_ALIGNMENT);
		gamePane.add(startServerButton);
		gamePane.add(joinGameButton);
		buttonPane.add(gamePane);
		buttonPane.add(Box.createVerticalGlue());
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(quitButton);
	}

	/** Apufunktio init():ille, luo pelaajan asetuksien valinnat */
	protected void createConfigPane() {
		configPane = new JPanel();
		configPane.setLayout(new BoxLayout(configPane, BoxLayout.Y_AXIS));
		SwingTools.setBorder(configPane, messages.getString("Player settings"));
		// the color panel
		playerColor = new JColorChooser(core.PlayerParticle.DEFAULT_COLOR);
		createColorPreviewPane(PREVIEW_W, PREVIEW_H);
		playerColor.setPreviewPanel(colorPreviewPane);
		AbstractColorChooserPanel[] panels = playerColor.getChooserPanels();
		playerColor.removeChooserPanel(panels[0]); // leave only the middle one
		playerColor.removeChooserPanel(panels[2]);
		// and a listener for the color chooser.
		playerColor.getSelectionModel().addChangeListener
			(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					Color newColor = playerColor.getColor();
					((PlayerParticle)controller.getGameData()
					 .getPlayerParticle()).setColor(newColor);
				}
			});
		// the "name" string and input box
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		playerName = new JTextField(messages.getString("noname"));
		namePanel.add(new JLabel(messages.getString("Name")+":"));
		namePanel.add(Box.createRigidArea(new Dimension(Launcher.SPACE, 0)));
		namePanel.add(playerName);
		// everythin on the configPanel
		configPane.add(namePanel);
		configPane.add(Box.createGlue());
		configPane.add(playerColor);
	}

	/** Luo "pikkupelin" v‰rivalinnan alle */
	protected void createColorPreviewPane(int w, int h) {
		controller = new Controller();
		BufferedImage map = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics g = map.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,w,h);
		g.dispose();
		controller.loadMap(new core.MapImageImpl(map));
		colorPreviewPane = new JPanel() {
			public Dimension getPreferredSize() {
				return new Dimension(PREVIEW_W+BORDER, PREVIEW_H+BORDER);
			}
		};
		colorPreviewPane.add(controller.getGraphics().getDisplay());
		controller.getGraphics().getDisplay().setSize(
				new Dimension(PREVIEW_W, PREVIEW_H));
	}

}
