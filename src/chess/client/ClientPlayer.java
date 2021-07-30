package chess.client;

public class ClientPlayer {
	
	public enum PlayerColor{
		none,
		white,
		black;
	}

	public String id;
	public PlayerColor color;
	
	public ClientPlayer(String idArg) {
		id = idArg;
		color = PlayerColor.none;
	}
	
}
