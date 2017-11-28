package gui;

import util.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import javax.swing.event.*;
import core.*;
import org.apache.log4j.Logger;

/** Serverin aloittamiseen tarvittavien asetusten valintaikkuna */
public class JoinWindow extends AbstractWindow {
	private final Logger log = Logger.getLogger(this.getClass());

	protected Resources messages;

	/** 
	 * Tekee uuden sisällön.
	 * @param messages Lokalisointi.
	 * */
	public JoinWindow(Resources messages) {
		this.messages = messages;
		resultHandler = new ListResultAdder();
		init();
	}

	/** Nappi jolla pääsee takaisin pääikkunaan */
	public JButton backButton;
	/** Peliin-liittyminen aloittaminen-nappi */
	public JButton joinButton;
	/** Peliin liittyminen omalla osoitteella */
	protected JButton manualButton;
	
	/** Lista löydetyistä peleistä. */
	public JList serverList;
	/** Uusien löytyneiden serverien käsittelijä */
	public core.SearchResultHandler resultHandler;

	/** Luo tarvittavat komponentit*/
	protected void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel title = new JLabel(ImageOperations.getIcon(
					messages.getString("GAME_LOGO")));
		JLabel subTitle = new JLabel("<html><h2>"
				+messages.getString("Joining an existing game")
				+"</h2></html>");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(title);
		this.add(subTitle);
		this.add(createGameSelector());
		this.add(createButtonPane());
	}

	/** Luo ala-napit */
	protected JPanel createButtonPane() {
		// luo komponentit
		backButton = new JButton(messages.getString("Back"));
		manualButton = new JButton(messages.getString("Manual address"));
		joinButton = new JButton(messages.getString("Join"));
		backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		manualButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		joinButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		// lisää komponentit
		JPanel pane = new JPanel();
		pane.add(backButton);
		pane.add(manualButton);
		pane.add(joinButton);
		pane.setAlignmentX(Component.LEFT_ALIGNMENT);
		return pane;
	}

	/** Luo tiedostojen valintaan käytettävän komponentin */
	protected JComponent createGameSelector() {
		serverList = new JList(new DefaultListModel());
		serverList.setCellRenderer(new ResultRenderer());
		serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverList.setVisibleRowCount(-1);

		JScrollPane serverListScroller = new JScrollPane(serverList);
		serverListScroller.setPreferredSize(new Dimension(500, 400));
		
		SwingTools.setBorder(serverListScroller, 
				messages.getString("Found games")+":");
		return serverListScroller;
	}

	/**
	 * Lisää löydetyn palvelimen listaan
	 */
	protected class ListResultAdder implements core.SearchResultHandler {

		/** Lisää tuloksen löydettyjen listaan */
		public void serverFound(
				String address,
				core.packets.GamePreview preview) {
			log.debug("Server found: "+address);
			((DefaultListModel)serverList.getModel()).add
				(0,
				 new ImageIcon(
					 preview.mapThumbnail.getImage(),
					 address));
		}

		/** Näyttää dialogin */
		public void connectFailed(String reason) {
			JOptionPane.showMessageDialog(serverList, messages.getString
					("Connection failed: "+reason),
					messages.getString("Unable to connect"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	class ResultRenderer extends JLabel implements ListCellRenderer {
		public ResultRenderer() {
		  	setOpaque(true);
		  	setHorizontalAlignment(LEFT);
		  	setVerticalAlignment(CENTER);
		}
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) 
		{
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
            } else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			ImageIcon icon = (ImageIcon)value;
			setText(icon.getDescription());
			setIcon(icon);
			return this;
		}
    }
	
}
