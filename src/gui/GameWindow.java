package gui;

import util.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import javax.swing.event.*;
import core.*;

/** Pelin n‰ytt‰v‰ ikkuna. */
public class GameWindow extends AbstractWindow {

	protected Resources messages;

	/** 
	 * Tekee uuden sis‰llˆn.
	 * @param messages Lokalisointi.
	 */
	public GameWindow(Resources messages, Component gameCanvas) {
		this.messages = messages;
		this.gameCanvas = gameCanvas;
		init();
	}

	/** Lakana jolle controller piirt‰‰ pelin kuvaa. */
	protected Component gameCanvas;
	/** Nappi jolla p‰‰see takaisin p‰‰ikkunaan */
	public JButton exitGameButton;
	/** Textikentt‰ johon pelaaja voi kirjoittaa rumia. */
	public JTextField chatField;

	
	/** Luo ja lis‰‰ tarvittavat komponentit. */
	protected void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(gameCanvas);
		this.add(createChatPanel());
		this.add(createExitButton());
		// gameCanvas.requestFocusInWindow();
	}

	public void start() {
		chatField.setText("");
	}

	/**
	 * Luo chattipaneli.
	 */
	public Component createChatPanel() {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		chatField = new JTextField();
		// add components
		pane.add(new JLabel(messages.getString("Send chat message")+":"));
		pane.add(chatField);
		//pane.setMaximumSize(pane.getPreferredSize());
		return pane;
	}


	/** Luo napin poistumista varten */
	protected JPanel createExitButton() {
		// luo komponentti
		exitGameButton = new JButton(messages.getString("Exit game"));
		exitGameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		// lis‰‰ komponentit
		JPanel pane = new JPanel();
		pane.add(exitGameButton);
		pane.setAlignmentX(Component.LEFT_ALIGNMENT);
		return pane;
	}
}
