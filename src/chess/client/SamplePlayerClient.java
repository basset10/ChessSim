package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuad;

import org.lwjgl.input.Keyboard;

public class SamplePlayerClient {

	public float xPos;
	public float yPos;
	
	public SamplePlayerClient() {
		xPos = 500;
		yPos = 500;
	}
	
	public void update() {
		draw();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) yPos -= 5;
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) xPos -= 5;
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) yPos += 5;
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) xPos += 5;
		
	}
	
	public void draw() {
		hvlDraw(hvlQuad(xPos, yPos, 10, 10), hvlColor(100f, 0f, 0f));
	}
	
	
}
