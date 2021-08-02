package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl2.HvlCoord;


/*
 * Class responsible for creating and drawing the individual squares used for an 8x8 chess board.
 */
public class ClientBoardSpace {

	
	public static final int SPACE_SIZE = 75;
	
	public int xPos;
	public int yPos;
	public Color color;

	public static enum Color{
		white,
		black;
	}

	public ClientBoardSpace(int xPosArg, int yPosArg, Color colorArg) {
		xPos = xPosArg;
		yPos = yPosArg;
		color = colorArg;
	}
	
	public void draw() {
		if(this.color == Color.black) {
			hvlDraw(hvlQuadc(this.getPixelPosition().x, this.getPixelPosition().y, SPACE_SIZE, SPACE_SIZE), hvlColor(0.15f, 0.15f, 0.15f));
		}else if(this.color == Color.white) {
			hvlDraw(hvlQuadc(this.getPixelPosition().x, this.getPixelPosition().y, SPACE_SIZE, SPACE_SIZE), hvlColor(0.9f, 0.9f, 0.9f));
		}
	}
	
	//Returns the exact center coordinate of the referenced grid square
	public HvlCoord getPixelPosition() {
		return new HvlCoord((xPos)*SPACE_SIZE + Display.getWidth()/2 - ((SPACE_SIZE * 4) - SPACE_SIZE/2),
				(yPos)*SPACE_SIZE + Display.getHeight()/2 - ((SPACE_SIZE * 4) - SPACE_SIZE/2));
	}	

}
