package chess.server;


import java.util.HashSet;


import com.osreboot.hvol2.base.anarchy.HvlIdentityAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;

public class ServerLobby {
	private HashSet<HvlIdentityAnarchy> ids;
	private ServerGame game;

	public ServerLobby(){
		ids = new HashSet<>();
		game = new ServerGame();
	}

	public void update(float delta){
		game.update();
		//drawStatusInfo();
	}
	
	
	public void onConnect(HvlIdentityAnarchy identity){
		ids.add(identity);
	}
	
	public void onDisconnect(HvlIdentityAnarchy identity){
		ids.remove(identity);

	}
	
	public HashSet<HvlIdentityAnarchy> getIds(){
		return ids;
	}
	
	private void drawStatusInfo(){		
		String statusInfo = "";
		statusInfo += "Client UUIDs:\\n";
		for(HvlIdentityAnarchy identity : HvlDirect.<HvlIdentityAnarchy>getConnections()){
			statusInfo += identity.getName() + "\\n";
		}
		System.out.println(statusInfo);
	}

}
