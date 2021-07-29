package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.hvol2.base.anarchy.HvlAgentClientAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;
import com.osreboot.ridhvl2.HvlCoord;
import com.osreboot.ridhvl2.loader.HvlLoaderFont;
import com.osreboot.ridhvl2.menu.HvlFont;

import chess.client.ClientMenuManager.MenuState;
import chess.client.ClientPlayer.PlayerColor;
import chess.common.NetworkUtil;
import chess.common.Util;
import chess.common.packet.PacketClientGameReady;
import chess.common.packet.PacketClientMove;
import chess.common.packet.PacketServerGameReadyResponse;
import chess.common.packet.PacketServerMoveResponse;
import chess.common.packet.PacketCollectivePlayerStatus;
import chess.common.packet.PacketPlayerStatus;

public class ClientGame {

	public enum GameState{
		menu,
		connecting,
		connected;
	}

	private boolean debug = false;
	PacketCollectivePlayerStatus playerPacket = new PacketCollectivePlayerStatus();
	private SamplePlayerClient sampleplayer;
	private HashMap<String, ClientPlayer> otherPlayers = new HashMap<String, ClientPlayer>();
	private String id;
	public ClientBoard board;
	public ClientPlayer player;
	private ArrayList<HvlCoord> validMoves = new ArrayList<HvlCoord>();
	private int selectedPiecexPos = -1;
	private int selectedPieceyPos = -1;
	private boolean boardInitialized = false;
	private boolean normalInput = true;
	private boolean playersTurn = false;
	public GameState state = GameState.menu;

	public ClientGame(String id) {
		sampleplayer = new SamplePlayerClient();
		this.id = id;
		player = new ClientPlayer(id);

		if(debug) {
			player.color = PlayerColor.black;
			board = new ClientBoard(player);
			boardInitialized = true;
		}

	}

	public void drawValidMoves() {
		for(HvlCoord m : validMoves) {
			if(player.color == PlayerColor.black) {
				hvlDraw(hvlQuadc(Util.convertToPixelPositionXBlack((int)m.x), Util.convertToPixelPositionYBlack((int)m.y), 10, 10), hvlColor(0f, 1f, 0f));
			}else if(player.color == PlayerColor.white) {
				hvlDraw(hvlQuadc(Util.convertToPixelPositionXWhite((int)m.x), Util.convertToPixelPositionYWhite((int)m.y), 10, 10), hvlColor(0f, 1f, 0f));
			}
		}
	}
	
	public void reset() {
		playerPacket = new PacketCollectivePlayerStatus();
		otherPlayers = new HashMap<String, ClientPlayer>();
		validMoves = new ArrayList<HvlCoord>();
		selectedPiecexPos = -1;
		selectedPieceyPos = -1;
		boardInitialized = false;
		normalInput = true;
		playersTurn = false;
		state = GameState.menu;
	}

	public void update(float delta){

		//System.out.println(otherPlayers.size());

		if(state == GameState.menu) {
			ClientMenuManager.manageMenus(this);
		}else if(state == GameState.connecting) {
			hvlFont(0).drawc("Connecting to server...", Display.getWidth()/2, Display.getHeight()/2, Color.white, 1f);
			if(ClientNetworkManager.isConnected) {
				System.out.println("Connection successful!");
				state = GameState.connected;
			}else {
				System.out.println("Connection error! Could not contact server.");
				state = GameState.menu;
			}		
		}
		else if(state == GameState.connected) {
			//Functions as a "Hello" packet for now
			HvlDirect.writeTCP(NetworkUtil.KEY_PLAYER_STATUS, new PacketPlayerStatus(true));
			if(HvlDirect.getKeys().contains(NetworkUtil.KEY_COLLECTIVE_PLAYER_STATUS)) {
				playerPacket = HvlDirect.getValue(NetworkUtil.KEY_COLLECTIVE_PLAYER_STATUS);
				for (String name : playerPacket.collectivePlayerStatus.keySet()){
					if(!otherPlayers.containsKey(name)) {
						if(!id.equals(name)) {
							otherPlayers.put(name, new ClientPlayer(name));
						}
					}
				}
			}
			otherPlayers.keySet().removeIf(p->{
				return !playerPacket.collectivePlayerStatus.containsKey(p);
			});	
			
			//Wait for second player before initializing game board
			if(!boardInitialized) {
				hvlFont(0).drawc("Connection established. Waiting for opponent...", Display.getWidth()/2, Display.getHeight()/2, Color.white, 1f);
				hvlFont(0).drawc("ESC to quit", Display.getWidth()/2, Display.getHeight()/2 + 40, Color.white, 0.7f);
				//Check if ready packet is received from server...
				if(HvlDirect.getKeys().contains(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY)) {
					PacketServerGameReadyResponse readyPacket = HvlDirect.getValue(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY);
					((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY);
					for (String name : readyPacket.collectiveClientGameReady.keySet()){
						if(name.equals(id)) {
							if(readyPacket.isWhite) {
								player.color = PlayerColor.white;
								playersTurn = true;
							}else {
								player.color = PlayerColor.black;
							}
							board = new ClientBoard(player);
							boardInitialized = true;
						}
					}

				}else {
					if(otherPlayers.size() == 1) {
						HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_GAME_READY, new PacketClientGameReady(id));
					}
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					ClientNetworkManager.disconnect();
					reset();
				}
			}else {
				if(otherPlayers.size() < 1) {
					ClientNetworkManager.disconnect();
					reset();
					System.out.println("Lost connection to opponent!");
				}
				board.update(delta, player);
				sampleplayer.update();
				drawValidMoves();

				//Receive and handle opponent's move from the server.
				if(HvlDirect.getKeys().contains(NetworkUtil.KEY_SERVER_MOVE_RESPONSE)) {
					PacketServerMoveResponse movePacket = HvlDirect.getValue(NetworkUtil.KEY_SERVER_MOVE_RESPONSE);
					((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_SERVER_MOVE_RESPONSE);	
					if(!movePacket.id.equals(id)) {
						System.out.println("Opponent's move received.");					
						System.out.println("Move packet id: " + movePacket.id);
						System.out.println("Your id: " + id);					
						System.out.println("Attempting to move piece at " + movePacket.packet.existingPieceX + ", " + movePacket.packet.existingPieceY 
								+ ". This piece is a " + board.getPieceAt(movePacket.packet.existingPieceX, movePacket.packet.existingPieceY ).color + " "
								+ board.getPieceAt(movePacket.packet.existingPieceX, movePacket.packet.existingPieceY ).type);				
						for(ClientPiece p : board.activePieces) {
							boolean escape = false;
							if(p.xPos == movePacket.packet.existingPieceX && p.yPos == movePacket.packet.existingPieceY) {			
								if(!board.isSpaceFree(movePacket.packet.intendedMoveX, movePacket.packet.intendedMoveY)) {
									for(int i = 0; i < board.activePieces.size(); i++) {
										if(board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX && board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY) {
											board.claimedPieces.add(board.activePieces.get(i));
											board.activePieces.remove(i);
											break;
										}
									}														
								}						
								p.xPos = movePacket.packet.intendedMoveX;
								p.yPos = movePacket.packet.intendedMoveY;
								escape = true;
								playersTurn = true;
							}					
							if(escape) break;
						}					
					}
				}

				//Detect if a piece is clicked, highlight that piece's valid moves (if existing)
				if(playersTurn) {
					hvlFont(0).drawc("It is your turn", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
					for(ClientPiece p : board.activePieces) {
						if(player.color == PlayerColor.white) {
							if((Util.getCursorX() >= p.getPixelPositionWhitePerspective().x - ClientPiece.PIECE_SIZE/2
									&& Util.getCursorX() <= p.getPixelPositionWhitePerspective().x + ClientPiece.PIECE_SIZE/2
									&& Util.getCursorY() >= p.getPixelPositionWhitePerspective().y - ClientPiece.PIECE_SIZE/2
									&& Util.getCursorY() <= p.getPixelPositionWhitePerspective().y + ClientPiece.PIECE_SIZE/2)
									&& Util.leftMouseClick() && p.color.toString().equals(player.color.toString())) {
								System.out.println("You clicked a " + p.color + " " + p.type + " on space [ " + p.xPos + ", " + p.yPos + " ]");
								selectedPiecexPos = p.xPos;
								selectedPieceyPos = p.yPos;
								validMoves = p.getAllValidMoves(board, player);
							}
						} else if(player.color == PlayerColor.black) {
							if((Util.getCursorX() >= p.getPixelPositionBlackPerspective().x - ClientPiece.PIECE_SIZE/2
									&& Util.getCursorX() <= p.getPixelPositionBlackPerspective().x + ClientPiece.PIECE_SIZE/2
									&& Util.getCursorY() >= p.getPixelPositionBlackPerspective().y - ClientPiece.PIECE_SIZE/2
									&& Util.getCursorY() <= p.getPixelPositionBlackPerspective().y + ClientPiece.PIECE_SIZE/2)
									&& Util.leftMouseClick() && p.color.toString().equals(player.color.toString())) {
								System.out.println("You clicked a " + p.color + " " + p.type + " on space [ " + p.xPos + ", " + p.yPos + " ]");
								selectedPiecexPos = p.xPos;
								selectedPieceyPos = p.yPos;
								validMoves = p.getAllValidMoves(board, player);
							}
						}
					}

					//If a valid move is attempted as defined in ClientPieceLogic, execute the move.
					for(HvlCoord m : validMoves) {
						boolean escape = false;
						if(player.color == PlayerColor.black) {
							if((Util.getCursorX() >= Util.convertToPixelPositionXBlack((int)m.x) - ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorX() <= Util.convertToPixelPositionXBlack((int)m.x) + ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorY() >= Util.convertToPixelPositionYBlack((int)m.y) - ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorY() <= Util.convertToPixelPositionYBlack((int)m.y) + ClientBoardSpace.SPACE_SIZE/2)
									&& Util.leftMouseClick()) {

								HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE, new PacketClientMove(selectedPiecexPos, selectedPieceyPos, (int)m.x, (int)m.y, id));

								for(ClientPiece p : board.activePieces) {
									if(p.xPos == selectedPiecexPos && p.yPos == selectedPieceyPos) {					
										//Claim any piece existing on the attempted move's square
										if(!board.isSpaceFree((int)m.x, (int)m.y)) {
											for(int i = 0; i < board.activePieces.size(); i++) {
												if(board.activePieces.get(i).xPos == (int)m.x && board.activePieces.get(i).yPos == (int)m.y) {
													board.claimedPieces.add(board.activePieces.get(i));
													board.activePieces.remove(i);
													break;
												}
											}														
										}						
										//Move piece to new square
										p.xPos = (int)m.x;
										p.yPos = (int)m.y;
										validMoves.clear();
										escape = true;
										playersTurn = false;
									}					
									if(escape) break;
								}
							}
						} else if(player.color == PlayerColor.white) {

							if((Util.getCursorX() >= Util.convertToPixelPositionXWhite((int)m.x) - ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorX() <= Util.convertToPixelPositionXWhite((int)m.x) + ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorY() >= Util.convertToPixelPositionYWhite((int)m.y) - ClientBoardSpace.SPACE_SIZE/2
									&& Util.getCursorY() <= Util.convertToPixelPositionYWhite((int)m.y) + ClientBoardSpace.SPACE_SIZE/2)
									&& Util.leftMouseClick()) {

								HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE, new PacketClientMove(selectedPiecexPos, selectedPieceyPos, (int)m.x, (int)m.y, id));

								for(ClientPiece p : board.activePieces) {
									if(p.xPos == selectedPiecexPos && p.yPos == selectedPieceyPos) {					
										//Claim any piece existing on the attempted move's square
										if(!board.isSpaceFree((int)m.x, (int)m.y)) {
											for(int i = 0; i < board.activePieces.size(); i++) {
												if(board.activePieces.get(i).xPos == (int)m.x && board.activePieces.get(i).yPos == (int)m.y) {
													board.claimedPieces.add(board.activePieces.get(i));
													board.activePieces.remove(i);
													break;
												}
											}														
										}						
										//Move piece to new square
										p.xPos = (int)m.x;
										p.yPos = (int)m.y;
										validMoves.clear();
										escape = true;
										playersTurn = false;
									}					
									if(escape) break;
								}
							}
						}
						if(escape) break;
					}
				}else {
					hvlFont(0).drawc("Waiting for opponent", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
				}
			}
		}
	}

}
