package core;

import util.*;
import physics.*;
import graphics.*;
import core.modules.*;
import core.packets.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
	 * Hoitaa eli peliosien synkronoinnin ja pelitilojen vaihtumisen.
	 * Tarjoaa myös uusien pelien etsimisen lähiverkosta.
	 */
public class Controller extends Thread {
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	/** Valmiustila, Controller ei tee mitään */
	public static final int STATE_READY = 1;
	
	/** Yksinpelitila, Testausta varten */
	public static final int STATE_SINGLEPLAY = 2;
	
	/** Pelitila serverinä */
	public static final int STATE_SERVER = 3;
	
	/** Serverien etsiminen verkosta */
	public static final int STATE_CLIENT_CONNECT = 4;
	
	/** Pelitila klienttinä */
	public static final int STATE_CLIENT = 5;
	
	/** Lopetustila, jonka jälkeen Controller lakkaa toimimasta,
	 * tähän siirrytään ainoastaan kun peli lopetetaan. */
	public static final int STATE_DIE = -1;
	
	/** Kohdeaikavälin pituus */
	protected int TIME_STEP = 50;
	
	
	// protected
	
	/** Syötteen ottoon käytetty olio, Controller asettaa tämän itse */
	protected Input input;
	
	/** Fysiikan laskut suorittava olio */
	protected Physics physics;
	
	/** Grafiikan piirtävä olio */
	protected Graphics graphics;
	
	/** Verkkoyhteydet hoitava olio */
	protected Network network;
	
	/** Controllerin käyttämä datavarasto */
	protected GameDataImpl gameData;
	
	/** Moduuleja laajennuksille */
	protected List modules;

	/** Tulevien pakettien käsittelijä */
	protected NetworkPacketListener packetListener;
	
	// state-keeping
	
	/** Kertoo controllerin tilan, vaihdetan setState-funktiolla. */
	protected int state;
	
	/**
	 * Jos ollaan STATE_CLIENT_CONNECT tilassa kertoo tulosten käsittelijän.
	 * Muissa tiloissa tällä ei ole merkitystä.
	 */
	protected SearchResultHandler searchResultHandler = null;
	
	// Constructors
	
	/**
	 * Luo controlleri. 
	 * Kutsuu initController():ia, joten olio on heti käyttövalmis
	 */
	public Controller() {
		super("ControllerMainLoopThread");
		initController();
	}
	
	/**
	 * Luo Controllerille tärkeät oliot, ellei konstrukori ole niitä jo
	 * tehnyt. Tarkemmin ottaen luo tarpeen mukaan: 
	 * GameData:lle implementaatioksi GameDataImpl:n,
	 * Graphics:ille implementaatioksi graphics.GraphicsImpl:n,
	 * Input:ille implementaatioksi InputImpl:n ja
	 * Physics:ille implementaatioksi physics.PhysicsImpl:n.
	 */
	protected void initController() {
		// setDaemon(true); // Jos swingin thready kuolee, niin ohjelma
		// kuolkoon.
		// edellinen rivi kommentoitu pois, jotta swingin
		// event-thread:iä ei ole välttämätöntä käynnistää.
		state = STATE_READY;
		modules = new ArrayList();
		if(gameData==null) gameData = new GameDataImpl();
		if(graphics==null) graphics = new graphics.GraphicsImpl();
		if(input==null) input = new InputImpl(gameData);
		if(physics==null) physics = new physics.PhysicsImpl();
		if(network==null) network = new network.NetworkImpl();
		this.packetListener = new ControllerPacketListener(this);
		network.setNetworkPacketListener(packetListener);
		// valmis, käynnistetään.
		this.start();
	}
	
	////////////////
	// Public members
	//////////////
	
	/**
	 * Lataa uuden kartan alimmasta rajapinnasta. 
	 * Mahdollista vain kun peli on valmiustilassa 
	 */
	public void loadMap(MapImage mapImage) {
		if(state == STATE_CLIENT || state == STATE_SERVER)
			throw new RuntimeException("Karttaa ei voi vaihtaa pelitilassa!");
		gameData.setMapImage(mapImage);
	}
	
	/** Lataa kartan tiedostosta */
	public void loadMap(File f) {
		loadMap(new MapImageImpl(ImageOperations.loadAsBufferedImage(f)));
	}
	
	/** Lisää moduulin. Kunkin moduulin voi lisätä vain kerran. */
	public void addModule(Module m) {
		if(m == null) {
			throw new RuntimeException("Can't add module, it's null!");
		}
		if(modules.contains(m)) {
			throw new RuntimeException("Can't add module, already added: "+m);
		}
		log.info("Adding module: "+m);
		modules.add(m);
	}
	
	/** 
	 * Poistaa moduulin.
	 * @return Tosi, jos poistettava moduuli oli lisätty, ja se tosiaan
	 * poistettiin.
	 */
	public boolean removeModule(Module m) {
		if(m != null) {
			log.info("Removing module: "+m);
			return modules.remove(m);
		} else {
			return false;
		}
	}
	
	/** 
	 * Alkaa etsiä verkosta palvelimia. Mahdollista vain kun peli on
	 * valmiustilassa.
	 */
	public void searchForServers(SearchResultHandler resultHandler) 
	{
		if(state != STATE_READY) {
			throw new RuntimeException("Can't start searching for servers, "+
					"state is not STATE_READY");
		}
		this.searchResultHandler = resultHandler;
		gotoState(STATE_CLIENT_CONNECT);
	}
	
	/** Liittyy palvelimeen ja aloittaa pelin. */
	public void joinServer(String address) {
		if(state != STATE_CLIENT_CONNECT) {
			throw new RuntimeException("Can't join a server, "+
					"state is not STATE_CLIENT_CONNECT");
		}
		// already done when entering STATE_CLIENT_CONNECT
		// addModule(clientJoinHandler); // will handle the server's reply
		try {
		network.connectTo(address);
		} catch(RuntimeException e) {
			log.warn("Connecting to server failed", e);
			searchResultHandler.connectFailed(e.toString());
		}
	}
	
	/** 
	 * Aloittaa klienttinä toimimisen. Tätä kutsuttaessa pitää kartta ja
	 * verkkoyhteys olla valmiina.
	 */
	public void startClient() {
		if(state != STATE_CLIENT_CONNECT) {
			throw new RuntimeException("Can't start as a client, "+
					"state is not STATE_CLIENT_CONNECT");
		}
		gotoState(Controller.STATE_CLIENT);
	}
	
	/**
	 * Aloittaa serverin.
	 */
	public void startServer(util.Resources messages) {
		gameData.getServerProperties().setServerName(
				gameData.getPlayerProperties().getName()
				+messages.getString("'s server"));
		gameData.getPlayerProperties().setId(
				gameData.getServerProperties().getNextPlayerId());
		gotoState(Controller.STATE_SERVER);
	}
	
	/**
	 * Aloitta yksinpelin.
	 */
	public void startSinglePlay() {
		gotoState(Controller.STATE_SINGLEPLAY);
	}
	
	/**
	 * Palaa alkutilaan.
	 */
	public void reset() {
		gotoState(Controller.STATE_READY);
		// modules = new ArrayList();
		network.disconnect();
	}

	/** Moduulien ja muiden laajennusten käyttöön */
	public int getState() {
		return state;
	}
	
	/**
	 * Controllerin pää-looppi, TÄTÄ EI PIDÄ KUTSUA KÄSIN.
	 * Delegoi kontrollin Controllerin tilan mukaan oikealle funktiolle.
	 * Thread:in laajentamisen takia tämä metodi on public.
	 */
	public void run() {
		state = STATE_READY;
		while(state != STATE_DIE) {
			try {
				switch(state) {
					case STATE_READY: 
						// odota kunnes jotain tapahtuu.
						synchronized(this) {
							wait(); 
						}
						break;
					case STATE_SINGLEPLAY:
						log.info("Entering singlePlayLoop()");
						singlePlayLoop();
						break;
					case STATE_CLIENT_CONNECT:
						log.info("Starting to find servers");
						clientConnectWaitLoop();
						break;
					case STATE_CLIENT:
						log.info("Entering gameLoop() as client");
						gameLoop();
						break;
					case STATE_SERVER:
						log.info("Entering gameLoop() as server");
						gameLoop();
						break;
					default:
						log.error("Unrecognized state: "+state);
						log.info("Reverting to STATE_READY");
						state=STATE_READY;
				}
			} catch (InterruptedException e) {
				log.warn("ignored interrupt",e);
			} catch (Error e) {
				// ohita jopa OutOfMemoryError:it, niitä nyt
				// joskus vaan syntyy
				log.fatal("An error occured",e);
			}
		}
	}
	
	// Protected members
	
	/** Mahdollistaa pelitilojen vaihtamisen. */
	synchronized protected void gotoState(int newState) {
		if(state == STATE_DIE && newState != STATE_DIE) {
			throw new RuntimeException("Kuollutta kontrolleria (state=STATE_DIE), ei voi herättää henkiin.");
		}
		// tilakoneen implementaatio
		switch (newState) {
			case STATE_READY: 
				// sallittu kaikista tiloista.
				state=newState;
				break;
			case STATE_SINGLEPLAY:
				// mennään suoraan testaamaan asetetuilla
				// parametreillä
				if(gameData.getMapImage()==null) throw new
					RuntimeException("No Map loaded");
				physics.initPhysics(gameData, true);
				state=newState;
				break;
			case STATE_DIE:
				// Kaikkialta saa lopettaa pelaamisen.
				state=newState;
				break;
			case STATE_SERVER:
				if(state != STATE_READY && state != STATE_CLIENT_CONNECT) {
					state=STATE_DIE;
					throw new RuntimeException("Serverinä voi ryhtyä toimimaan vain ennen pelin aloittamista. Nykyinen tila on: "+state);
				}
				state=newState;
				break;
			case STATE_CLIENT_CONNECT:
				if(state != STATE_READY) {
					throw new RuntimeException("Pelejä voi ryhtyä etsimään vain valmiustilasta. Nykyinen tila on: "+state);
				}
				state=newState;
				break;
			case STATE_CLIENT:
				if(state != STATE_CLIENT_CONNECT) {
					throw new RuntimeException("Clienttinä voi ryhtyä toimimaan vain pelien etsimisen jälkeen. Nykyinen tila on: "+state);
				}
				state=newState;
				break;
			default:
				throw new RuntimeException("Tuntematon tila: " 
						+newState);
		}
		notify(); // Controllerin ainoalle säikeelle
	}
	
	/**
	 * Moninpeli, sekä serverinä että klienttinä toimiessa käytetään täsää
	 * funktiossa olevaa looppia.
	 */
	protected void gameLoop() throws InterruptedException {
		// Sekä serverinä että klienttinä olevalla pelaajalla täytyy olla oma
		// pallo.
		PlayerParticleHandlerModule pphm=null;
		ServerJoinHandlerModule sjhm=null;
		ServerParticleSynchronizer sps=null;
		PlayerParticleSynchronizer pps=null;
		try {
			// palaaja saa respawnata.
			pphm = new PlayerParticleHandlerModule(this);
			addModule(pphm);
			log.info("initing physics");
			physics.initPhysics(gameData, state==STATE_SERVER);
			if(state==STATE_SERVER) {
				sjhm=new ServerJoinHandlerModule(network, gameData);
				addModule(sjhm);
				network.startServer(new GamePreview(gameData));
				sps=new ServerParticleSynchronizer(this, packetListener);
				addModule(sps);
			} else if(state==STATE_CLIENT) {
				pps=new PlayerParticleSynchronizer(this, packetListener);
				addModule(pps);
			}
			TicControl ticControl = new TicControl(TIME_STEP);
			int timestep=TIME_STEP;
			graphics.updateDisplay(gameData);
			synchronized(ticControl) {
				Iterator iter; // used to iterate over modules
				Module m;
				while(state==STATE_SERVER || state==STATE_CLIENT) 
				{
					timestep = ticControl.nextTic();
					// estä muita moduuleja sotkemasta laskentaa.
					synchronized(gameData) {
						// piirrä grafiikka ensin, niin että swingi ehtii
						// päivittää sen ruudulle asynkroonisesti laskennan
						// kanssa.
						graphics.updateDisplay(gameData);
						// log.info("moving particles");
						physics.moveParticles(timestep);
					}
					for(iter = modules.iterator(); iter.hasNext(); ) {
						m = (Module) iter.next();
						m.tic(gameData, timestep);
					}
					// debug
					synchronized(gameData) {
						if(gameData.getParticles().length < 60) {
							PlayerParticle p = new PlayerParticle("foo");
							gameData.addParticle(p);
							p.setId(-1); // ei saa sekoittaa pelaajan palloa
							p.setPosition(new Vector
									(gameData.getMapImage().getWidth()/2,
								 gameData.getMapImage().getHeight()/2));
						}
					}
				}
			}
		} catch(RuntimeException e) {
			log.error("Exception in singlePlayLoop ",e);
			log.info("Reverting back to STATE_READY");
			state = STATE_READY;
		} finally {
			removeModule(pphm);
			removeModule(sjhm);
			removeModule(pps);
			removeModule(sps);
			gameData.setPlayerParticle(null);
			gameData.reset();
		}
		
	}
	
	/**
	 * Odottaa serveriltä tulevia liittymisvastauksia.
	 * Liittymispyyntö lähetetään ControllerPacketListener:istä kun
	 * uusi yhteys havaitaan.
	 */
	protected void clientConnectWaitLoop() throws InterruptedException {
		ClientJoinHandler clientJoinHandler = new ClientJoinHandler(this);
		addModule(clientJoinHandler);
		network.searchForServers();
		while(state == STATE_CLIENT_CONNECT)
			try {
				wait(); // kunnes saadaan yhteys tai palataan alkutilaan.
			} catch(Exception e) { }
		removeModule(clientJoinHandler);
	}
	
	
	
	/**
	 * Yksinpelinä testausta varten.
	 * Sijoittaa uuden pallon keskelle ruutua.
	 */
	protected void singlePlayLoop() throws InterruptedException {
		try {
			if(physics == null) {
				throw new RuntimeException(
						"Physics must not be null");
			}
			if(gameData.getPlayerParticle() == null) {
				log.info("Adding a playerParticle");
				Particle player = new PlayerParticle("pomppija");
				gameData.addParticle(player);
				player.setPosition(new Vector
						(gameData.getMapImage().getWidth()/2,
						 gameData.getMapImage().getHeight()/2));
				gameData.setPlayerParticle(player);
			} else {
				log.debug("Not adding a player, one already exists");
			}
			log.info("initing physics");
			physics.initPhysics(gameData, true);
			while(state==STATE_SINGLEPLAY) {
				graphics.updateDisplay(gameData);
				// log.info("moving particles");
				physics.moveParticles(50);
				// make dynamic
				sleep(50);
			}
		} catch(RuntimeException e) {
			log.error("Exception in singlePlayLoop ",e);
			log.info("Reverting back to STATE_READY");
			state = STATE_READY;
		}
	}
	
	// generated bean methods
	
	/**
	 * Palauttaa controllerin syötettä vastaanottavan olion.
	 */
	public Input getInput() {
		return input;
	}
	
	/** Palauttaa controllerin käyttämän fysiikka-implementaation */
	public Physics getPhysics() {
		return physics;
	}
	/** Asettaa controllerin käyttämän fysiikka-implementaation */
	public void setPhysics(Physics physics) {
		this.physics = physics;
	}
	
	
	/** Palauttaa controllerin käyttämän grafiikka-implementaation */
	public Graphics getGraphics() {
		return graphics;
	}
	
	/** Asettaa controllerin käyttämän grafiikka-implementaation */
	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}
	
	/** Palauttaa controllerin käyttämän verkko-implementaation */
	public Network getNetwork() {
		return network;
	}
	/** Asettaa controllerin käyttämän verkko-implementaation */
	public void setNetwork(Network network) {
		this.network = network;
	}
	
	/** Palauttaa controllerin käyttämän pelidatan */
	public GameData getGameData() {
		return gameData;
	}
	
	/** Controllerille paketteja verko-osalta välittävä adapteri */
	protected class ControllerPacketListener implements NetworkPacketListener {
		private final Logger log = Logger.getLogger(this.getClass());

		/** ympäröivä controlleri */
		Controller parent;

		public ControllerPacketListener(Controller parent) {
			this.parent = parent;
		}
	
		public void handlePacket(NetworkPacket packet) {
			if(packet.getData() instanceof ModuleNetworkPacket) {
				Iterator iter;
				Module m;
				for(iter = modules.iterator(); iter.hasNext(); ) {
					m = (Module) iter.next();
					try {
						m.packetRecieved(packet);
					} catch(Exception e) {
						log.warn
							("Module,"+m.toString()+" threw an exception: ",
							 e);
					}
				}
			} else {
				log.warn("Unhandled packet received: "+packet);
			}
		}
	
		/**
		 * Kutsutaan kun lähiverkosta löytyy caveball palvelin.
		 * @param address osoite jossa peli on
		 * @param definition Serverin määritelmä.
		 */
		public void serverFound(String address, Serializable definition) {
			searchResultHandler.serverFound(address, (GamePreview) definition);
		} 
		
		/**
		 * Kutsutaan kun uusi yhteys on muodostunut. Tämän jälkeen voi
		 * paketteja ryhtyä lähettelemään. Ennen tämän kutsua ei paketteja voi
		 * lähettääkkään.
		 * @param source Luotu yhteys.
		 */
		public void connectionEstablished(Object source) {
			if(state==STATE_CLIENT_CONNECT) {
				log.info("Client established connection, sending JoinRequest");
				network.sendPacket(new JoinRequest());
				// Tämän jälkeen moduulit hoitavat lopun.
			} else if(state==STATE_SERVER) {
				// Klientti otti yhteyden serveriin.
				// Ei tehdä mitään, moduulit hoita homman.
			} else {
				log.error("Connection received, but state is: "+state);
			}
		} 
		
		public void connectionAttemptFailed(String reason, Object source) {
			searchResultHandler.connectFailed(reason);
		}
		
		/**
		 * Kutsutaan kun yhteys serveriin katkeaa.
		 * Ei kutsuta kun toimitaan serverinä.
		 * Tämän jälkeen ei voi enää lähettää paketteja.
		 * @param reason Syy yhteyden katkeamiseen.
		 * @param source Katkennut yhteys.
		 */
		public void connectionLost(String reason, Object source) {
			if(parent.state==parent.STATE_CLIENT) {
				log.info("Connection to server lost: "+reason);
				parent.reset();
			} else if(parent.state==parent.STATE_SERVER) {
				log.info("Connection to client lost: "+reason);
			} else {
				log.warn("Connection lost, even though there should be no connections: " + reason);
			}
		} 
		
	}
}
