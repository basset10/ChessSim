package chess.client;

import com.osreboot.hvol2.direct.HvlDirect;
import com.osreboot.ridhvl2.template.HvlChronology;
import com.osreboot.ridhvl2.template.HvlDisplayWindowed;
import com.osreboot.ridhvl2.template.HvlTemplateI;

import chess.client.ClientGame.GameState;
import chess.common.Util;

public class ClientMain extends HvlTemplateI{

	private ClientGame game;

	//Castling, promotion
	//Check and mate detection
	//Server disconnection
	//Full server indication
	

	public static void main(String args[]) {
		HvlChronology.registerChronology(HvlDirect.class);
		new ClientMain();
	}

	public ClientMain() {
		super(new HvlDisplayWindowed(144, 1280, 720, "Chess Simulator", false));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		ClientLoader.loadTextures();
		ClientNetworkManager.initialize();
		//Immediately attempts to connect to localhost on startup
		//ClientNetworkManager.connect("localhost", 25565);
		game = new ClientGame(ClientNetworkManager.id);

	}

	@Override
	public void update(float delta) {
		ClientNetworkManager.update(delta);
		Util.update();
		game.update(delta);		
	}

}
