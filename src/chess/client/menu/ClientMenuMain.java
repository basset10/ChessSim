package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlFont;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.client.ClientNetworkManager;
import chess.client.ClientGame;
import chess.client.ClientGame.GameState;

public class ClientMenuMain {

	private ArrayList<ClientButton> buttons;
	//private String text = "lol";
	
	public ClientMenuMain(ClientGame game) {
		buttons = new ArrayList<ClientButton>();
		buttons.add(new ClientButton(300, 100, Display.getWidth()/2f, Display.getHeight()/2f+80, "Connect", () ->{
			game.state = GameState.connecting;
			ClientNetworkManager.connect("localhost", 25565);
		}));
		buttons.add(new ClientButton(300, 100, Display.getWidth()/2f, Display.getHeight()/2f+200, "Localhost", () ->{
			game.state = GameState.connecting;
			ClientNetworkManager.connect("localhost", 25565);
		}));
	}
	
	public void operate() {
		hvlFont(0).drawc("Chess Sim", Display.getWidth()/2, Display.getHeight()/2-100, Color.white, 4f);
		for(ClientButton b : buttons) {
			b.operate();
		}
	}
	
}
