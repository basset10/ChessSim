package chess.client;

public class ClientMove {
	
	public int x;
	public int y;
	public boolean castle;
	
	public ClientMove(int x, int y, boolean castle) {
		this.x = x;
		this.y = y;
		this.castle = castle;
	}
	
}
