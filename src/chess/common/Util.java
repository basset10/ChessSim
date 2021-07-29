package chess.common;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl2.HvlCoord;

import chess.client.ClientBoardSpace;

public class Util {

	private static boolean leftMouseState = false;
	private static boolean leftMouseClick = false;
	

	public static int getCursorX(){
		return Mouse.getX();
	}

	public static int getCursorY(){
		return Display.getHeight() - Mouse.getY();
	}

	public static float convertToPixelPositionXWhite(int xArg) {
		float x = ((xArg)*ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));
		return x;
	}

	public static float convertToPixelPositionYWhite(int yArg) {
		float y = (yArg)*ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		return y;
	}
	
	public static float convertToPixelPositionXBlack(int xArg) {
		float x = (xArg)*-ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		return x;
	}

	public static float convertToPixelPositionYBlack(int yArg) {
		float y = (yArg)*-ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		return y;
	}

	public static void update() {

		if(leftMouseClick) {
			leftMouseClick = false;
		}else {
			if(!leftMouseState) {
				if(!Mouse.isButtonDown(0)) {
					leftMouseClick = false;
				}else {
					leftMouseClick = true;
				}
			}
		}		
		if(Mouse.isButtonDown(0)) {
			leftMouseState = true;
		}else {
			leftMouseState = false;
		}		
	}	
	
	public static boolean leftMouseClick() {
		return leftMouseClick;
	}


}



