package gui;

import util.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import javax.swing.event.*;
import core.*;

/** Serverin aloittamiseen tarvittavien asetusten valintaikkuna */
public class StartServerWindow extends AbstractWindow {


	/** 
	 * Tekee uuden sisällön.
	 * @param messages Lokalisointi.
	 * @param mapFolder Hakemisto josta karttojen etsiminen aloitetaan.
	 * */
	public StartServerWindow(Resources messages, File mapFolder) {
		super(messages);
		init(mapFolder);
	}

	/**  olevat napit. */
	protected JPanel buttonPane;
	/** Nappi jolla pääsee takaisin pääikkunaan */
	public JButton backButton;
	/** Serverin aloittaminen-nappi */
	public JButton startButton;

	/** Kartan valintaan */
	public JFileChooser mapChooser;
	
	/** Luo tarvittavat komponentit*/
	protected void init(File mapFolder) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel title = new JLabel(ImageOperations.getIcon(
					messages.getString("GAME_LOGO")));
		JLabel subTitle = new JLabel("<html><h2>"
				+messages.getString("Start a new game")
				+"</h2></html>");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(title);
		this.add(subTitle);
		this.add(createFileSelector(mapFolder));
		this.add(createButtonPane());
	}

	/** Luo ala-napit */
	protected JPanel createButtonPane() {
		// luo komponentit
		backButton = new JButton(messages.getString("Back"));
		startButton = new JButton(messages.getString("Start"));
		backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		// lisää komponentit
		JPanel pane = new JPanel();
		pane.add(backButton);
		pane.add(startButton);
		pane.setAlignmentX(Component.LEFT_ALIGNMENT);
		return pane;
	}

	/** Luo tiedostojen valintaan käytettävän komponentin */
	protected JComponent createFileSelector(File mapFolder) {
		mapChooser = new JFileChooser(mapFolder);
		FileFilter mapFilter = new FileFilter() {
			// tallennettuna jottei käännöstä tehtäisi useampaa kertaa.
			String description = messages.getString("Map files");
			public String getDescription() {
				return description;
			}
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				if(f.getName().toLowerCase().endsWith(".png"))
					return true;
				else
					return false;
			}
		};
		mapChooser.setFileFilter(mapFilter);
		mapChooser.setAccessory(new ImagePreview(mapChooser));
		mapChooser.setControlButtonsAreShown(false);
		SwingTools.setBorder(mapChooser, messages.getString("Select a map"));
		return mapChooser;
	}

}
