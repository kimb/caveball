package gui;

import core.*;
import util.*;
import graphics.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.*;

/**
 * Pääluokka joka luo kontrollerin ja käyttöliittymät. Hoitaa käyttöliittymien
 * ja kontrollerin yhteenliittämisen.
 */
public class Launcher {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Komponenttien välissä käytettävä tyhjä. */
	public static final int SPACE = 6;

	/**
	 * Käynnistää pelin. Sisältää pelkästään "new Launcher()".
	 * @param args Näillä ei tehdä mitään. Peli konfiguroidaan
	 * käyttöliittymästä
	 */
	public static void main(String[] args) {
		new Launcher();
	}

	///////////
	
	/** Peliin käytettävä kontrolleri. */
	protected Controller controller;

	/** Pelin pääikkuna. Tätä uudelleenkäytetään kaikkille (paitsi
	 * modaalille) dialogeille. Näin saadan aikaseksi "ammattimaisempi"
	 * tuntuma.
	 */
	protected JFrame frame;

	/** Käytettävän kielen sanasto. */
	protected Resources messages;

	/** Käytettävä tapahutmien kuuntelija */
	protected EventHandler handler = new EventHandler(this);

	/** Chatti viestien lähettäjä */
	protected ChatRendererPlugin crp;

	// CONSTRUCTOR
	
	/** Alustaa kirjastot ja käynnistää pää-initialisoinnin. (start()) */
	public Launcher() {
		libInit();
		coreInit();
		start();
	}

	/** Alustaa järjestelmän kattavat kirjastot. */
	protected void libInit() {
		// Log4j saa tulostaa kaiken konsoliin.
		//BasicConfigurator.configure();
	}

	/** Luo kaikkien ikkunoiden käyttöliittymät, ja niiden vaatimat resurssit. **/
	protected void coreInit() {
		// lokalisointi
		messages = new Resources(ResourceBundle.getBundle("gui.messages"));
		// pelin kontrolloja (ydin)
		controller = new Controller();
		InputKeyListener listener = new InputKeyListener(controller.getInput());
		controller.getGraphics().getDisplay().addKeyListener(listener);
		// chat-viestien käsittely
		crp = new ChatRendererPlugin(controller);
		controller.addModule(crp);
		((AbstractGraphics)controller.getGraphics()).addRendererPlugin(crp);
		// ikkunat
		if(mainWindow == null) {
			mainWindow = new MainWindow(messages);
			mainWindow.startServerButton.addActionListener(handler);
			mainWindow.joinGameButton.addActionListener(handler);
			mainWindow.quitButton.addActionListener(handler);
			mainWindow.playerColor.getSelectionModel().addChangeListener(handler);
		}
		if(startServerWindow == null) {
			startServerWindow = new StartServerWindow(messages,
					new File(messages.getString("MAP_FOLDER")));
			startServerWindow.backButton.addActionListener(handler);
			startServerWindow.startButton.addActionListener(handler);
		} 
		if(joinWindow == null) {
			joinWindow = new JoinWindow(messages);
			joinWindow.backButton.addActionListener(handler);
			joinWindow.manualButton.addActionListener(handler);
			joinWindow.joinButton.addActionListener(handler);
		} 
		if(gameWindow == null) {
			gameWindow = new GameWindow(messages, 
					controller.getGraphics().getDisplay());
			gameWindow.exitGameButton.addActionListener(handler);
			gameWindow.exitGameButton.addKeyListener(listener);
			gameWindow.chatField.addActionListener(handler);
			gameWindow.chatField.addKeyListener(listener);
		}
	}
	
	/** Luo lokalisaation, controllerin ja pääikkunan. */
	protected void start() {
		// ikkuna jossa peli näytetään
		frame = new JFrame(messages.getString("Caveball"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// näytetään pääikkuna
		frame.setContentPane(mainWindow);
		frame.pack();
		mainWindow.start();
		// ikkuna keskemmälle ruutua
		Dimension windowSize = frame.getSize();
		Dimension screenSize = frame.getToolkit().getScreenSize();
		frame.setLocation
			((int) ((screenSize.getWidth()-frame.getWidth())/7.f*2),
			 (int) ((screenSize.getHeight()-frame.getHeight())/7.f*2));
		frame.show();
	}

	/** luotu pääikkunan sisältö */
	protected MainWindow mainWindow;

	/** luotu pelinaloittamisikkuna */
	protected StartServerWindow startServerWindow;

	/** Peliinliittymisikkuna. */
	protected JoinWindow joinWindow;
	
	/** Peli-ikkuna. */
	protected GameWindow gameWindow;

	/** Pitää kirjaa näytetystä ikkunasta. */
	protected AbstractWindow currentWindow;

	/** Näytä annettu ikkuna kehyksessä.*/
	protected void showWindow(AbstractWindow win) {
		if(currentWindow!=null) {
			currentWindow.stop();
		}
		frame.setContentPane(win);
		frame.pack();
		win.start();
		currentWindow = win;
	}


	/** 
	 * Tallentaa pääikkunassa tehdyt asetukset tämän luokan kontrollerille
	 */
	protected void getPlayerConfigFromMainWindow() {
		PlayerProperties p = controller.getGameData().getPlayerProperties();
		p.setName(mainWindow.playerName.getText());
		p.setColor(mainWindow.playerColor.getColor());
	}

	/** Kaikkien eri ikkunoiden tapahtumien kuuntelija */
	public class EventHandler implements ActionListener, ChangeListener {
		private Launcher parent;		
		public EventHandler(Launcher parent) {
			this.parent = parent;
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == mainWindow.quitButton) {
				// pelistä lähteminen
				System.exit(0);
			} else if(e.getSource() == mainWindow.joinGameButton) {
				// pelien etsiminen
				getPlayerConfigFromMainWindow();
				showWindow(joinWindow);
				controller.searchForServers(joinWindow.resultHandler);
			} else if(e.getSource() == joinWindow.backButton) {
				// serverin perustamisen peruminen
				showWindow(mainWindow);
			} else if(e.getSource() == joinWindow.manualButton) {
				// manuaalinen serverin osoite
				String address = JOptionPane.showInputDialog(frame,
						messages.getString("Enter server address"),
						messages.getString("Manual server address"),
						JOptionPane.PLAIN_MESSAGE);
				if(address != null && !"".equals(address)){
					showWindow(gameWindow);
					controller.joinServer(address);
				}
			} else if(e.getSource() == joinWindow.joinButton) {
				// listasta valittu
				Object selection = joinWindow.serverList.getSelectedValue();
				if(selection == null) {
					// none selected
					JOptionPane.showMessageDialog(
							parent.frame,
							messages.getString("No game selected!"),
							messages.getString(
								"Select a server or enter a manual address"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					String address = ((ImageIcon)selection).getDescription();
					showWindow(gameWindow);
					controller.joinServer(address);
				}
			} else if(e.getSource() == mainWindow.startServerButton) {
				// kentän valinta serverinä olemiselle
				getPlayerConfigFromMainWindow();
				showWindow(startServerWindow);
			} else if(e.getSource() == startServerWindow.backButton) {
				// serverin perustamisen peruminen
				showWindow(mainWindow);
			} else if(e.getSource() == startServerWindow.startButton) {
				// serverin käynnistäminen
				// vain jos valittu kartta on ok.
				File f = startServerWindow.mapChooser.getSelectedFile();
				if(f==null) {
					JOptionPane.showMessageDialog(
							parent.frame,
							messages.getString("No file selected!"),
							messages.getString("Invalid map selection"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					ImageIcon map = new ImageIcon(f.getPath());
					if(map.getIconWidth() > 0 && map.getIconHeight() > 0) {
						showWindow(gameWindow);
						controller.loadMap(f);
						controller.startServer(messages);
					} else {
						JOptionPane.showMessageDialog(
								parent.frame,
								messages.getString("File")+" "
								+ f.getName()
								+ messages.getString(
									" is not a valid map (picture)!"),
								messages.getString("Invalid map selection"),
							JOptionPane.ERROR_MESSAGE);
					}
				}
			} else if(e.getSource() == gameWindow.chatField) {
				// lähetä chatti
				crp.sendChat(gameWindow.chatField.getText());
				gameWindow.chatField.setText("");
				//gameWindow.chatField.transferFocus();
			} else if(e.getSource() == gameWindow.exitGameButton) {
				controller.reset();
				showWindow(mainWindow);
			} else {
				log.warn("UNHANDLED Action performed" + e.toString());
			}
		}

		public void stateChanged(ChangeEvent e) {
			log.debug("State changed: " + e.toString());
		    	//Color newColor = tcc.getColor();
			//banner.setForeground(newColor);
		}
	}
}
