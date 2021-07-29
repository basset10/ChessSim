package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.client.ClientGame.GameState;
import chess.common.Util;

public class ClientMenuManager {

	public static MenuState menu = MenuState.main;
	
	public static enum MenuState{
		main,
		connect,
		options,	
	}
	
	public static void manageMenus(ClientGame game) {
		if(menu == MenuState.main) {
			operateMainMenu(game);
		}else if(menu == MenuState.connect) {
			operateConnectMenu();
		}else if(menu == MenuState.options) {
			operateOptionsMenu();
		}
	}
	
	private static void operateMainMenu(ClientGame game) {
		hvlDraw(hvlQuadc(Display.getWidth()/2, Display.getHeight()/2, 300, 100), Color.lightGray);
		hvlFont(0).drawc("Connect", Display.getWidth()/2, Display.getHeight()/2, Color.white, 2f);
		if(Util.leftMouseClick()) {
			game.state = GameState.connecting;
			ClientNetworkManager.connect("localhost", 25565);			
		} 
	}
	
	private static void operateConnectMenu() {
		
	}
	
	private static void operateOptionsMenu() {
		
	}
	
}
