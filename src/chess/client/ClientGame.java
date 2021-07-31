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
import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.common.NetworkUtil;
import chess.common.Util;
import chess.common.packet.PacketClientGameOver;
import chess.common.packet.PacketClientGameReady;
import chess.common.packet.PacketClientMove;
import chess.common.packet.PacketServerGameReadyResponse;
import chess.common.packet.PacketServerMoveResponse;
import chess.common.packet.PacketCollectivePlayerStatus;
import chess.common.packet.PacketPlayerStatus;

public class ClientGame {

	public static final int GAME_END_STATE_CONTINUE = 0;
	public static final int GAME_END_STATE_CHECKMATE = 1;
	public static final int GAME_END_STATE_STALEMATE = 2;

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
	private ArrayList<ClientMove> validMoves = new ArrayList<ClientMove>();
	private int selectedPiecexPos = -1;
	private int selectedPieceyPos = -1;
	private boolean boardInitialized = false;
	private boolean normalInput = true;
	private boolean playersTurn = false;
	public GameState state = GameState.menu;
	public PlayerColor finalMove;
	public boolean inCheck = false;
	public int gameEndState = GAME_END_STATE_CONTINUE;
	public int moveCount = 0;

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
		for(ClientMove m : validMoves) {
			if(player.color == PlayerColor.black) {
				hvlDraw(hvlQuadc(Util.convertToPixelPositionXBlack(m.x), Util.convertToPixelPositionYBlack(m.y), 10, 10), hvlColor(0f, 1f, 0f));
			}else if(player.color == PlayerColor.white) {
				hvlDraw(hvlQuadc(Util.convertToPixelPositionXWhite(m.x), Util.convertToPixelPositionYWhite(m.y), 10, 10), hvlColor(0f, 1f, 0f));
			}
		}
	}

	public void reset() {
		playerPacket = new PacketCollectivePlayerStatus();
		otherPlayers = new HashMap<String, ClientPlayer>();
		validMoves = new ArrayList<ClientMove>();
		selectedPiecexPos = -1;
		selectedPieceyPos = -1;
		boardInitialized = false;
		normalInput = true;
		playersTurn = false;
		state = GameState.menu;
		inCheck = false;
		gameEndState = GAME_END_STATE_CONTINUE;
		finalMove = null;
		moveCount = 0;
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
				hvlFont(0).drawc("ESC to cancel", Display.getWidth()/2, Display.getHeight()/2 + 40, Color.white, 0.7f);
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
					reset();
					//Send the player to a menu telling them they lost connection
					ClientMenuManager.menu = MenuState.lostConnectionToOpponent;
					ClientNetworkManager.disconnect();
					System.out.println("Lost connection to opponent!");
				}else {
					board.update(delta, player);
					sampleplayer.update();
					drawValidMoves();

					if(inCheck) {
						//System.out.println("YOU'RE IN CHECK");
					}

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
							for(ClientPiece pc : board.activePieces) {
								if(pc.enPassantVulnerable) pc.enPassantVulnerable = false;
							}
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
									//if the move packet indicates a pawn moved two spaces, set that pawn to enPassantVulnerable
									if(p.type == PieceType.pawn && (movePacket.packet.intendedMoveY == movePacket.packet.existingPieceY + 2
											|| movePacket.packet.intendedMoveY == movePacket.packet.existingPieceY - 2)) {
										p.enPassantVulnerable = true;
									}

									//if a move packet labeled as "enPassant" is received, detect and remove the appropriate pawn.
									if(movePacket.packet.enPassant) {
										System.out.println("En passant packet received!");
										//White perspective
										if(movePacket.packet.intendedMoveY == 2) {
											for(int i = 0; i < board.activePieces.size(); i++) {
												if(board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX 
														&& board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY+1) {
													board.claimedPieces.add(board.activePieces.get(i));
													board.activePieces.remove(i);
													break;
												}
											}	
										}
										//Black perspective
										if(movePacket.packet.intendedMoveY == 5) {
											for(int i = 0; i < board.activePieces.size(); i++) {
												if(board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX 
														&& board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY-1) {
													board.claimedPieces.add(board.activePieces.get(i));
													board.activePieces.remove(i);
													break;
												}
											}	
										}
									}

									//if a move packet labeled as "castle" is received, detect and move the appropriate rook.
									if(movePacket.packet.castled) {
										if(movePacket.packet.intendedMoveX == 6 && movePacket.packet.intendedMoveY == 7) {
											board.getPieceAt(7, 7).xPos = 5;
										}else if(movePacket.packet.intendedMoveX == 2 && movePacket.packet.intendedMoveY == 7) {
											board.getPieceAt(0, 7).xPos = 3;
										}if(movePacket.packet.intendedMoveX == 6 && movePacket.packet.intendedMoveY == 0) {
											board.getPieceAt(7, 0).xPos = 5;
										}else if(movePacket.packet.intendedMoveX == 2 && movePacket.packet.intendedMoveY == 0) {
											board.getPieceAt(0, 0).xPos = 3;
										}
									}

									p.xPos = movePacket.packet.intendedMoveX;
									p.yPos = movePacket.packet.intendedMoveY;
									if(!p.moved) p.moved = true;
									inCheck = ClientPieceLogic.getCheckState(board, player);
									gameEndState = ClientPieceLogic.getGameEndState(board, player, inCheck);
									System.out.println("Game End State:");
									System.out.println(gameEndState);
									if(gameEndState == GAME_END_STATE_CHECKMATE || gameEndState == GAME_END_STATE_STALEMATE){
										System.out.println("Sending failure packet...");
										HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_GAME_OVER, new PacketClientGameOver(gameEndState, player.color));
										if(player.color == PlayerColor.black) {
											finalMove = PlayerColor.white;
										}else {
											finalMove = PlayerColor.black;
										}
									}else {
										playersTurn = true;
									}								
									escape = true;
								}					
								if(escape) break;
							}					
						}
					}

					//Check if a game over packet has been received from the server
					if(gameEndState == GAME_END_STATE_CONTINUE) {
						if(HvlDirect.getKeys().contains(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE)) {
							System.out.println("Victory packet received!");
							PacketClientGameOver gameOverPacket = HvlDirect.getValue(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE);
							((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE);
							gameEndState = gameOverPacket.gameEndState;
							if(gameOverPacket.playerColor == PlayerColor.black) {
								finalMove = PlayerColor.white;
							}else {
								finalMove = PlayerColor.black;
							}
						}
					}

					//Detect if a piece is clicked, highlight that piece's valid moves (if existing)
					if(gameEndState == GAME_END_STATE_CONTINUE) {
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
							for(ClientMove m : validMoves) {
								boolean escape = false;
								if(player.color == PlayerColor.black) {
									if((Util.getCursorX() >= Util.convertToPixelPositionXBlack(m.x) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorX() <= Util.convertToPixelPositionXBlack(m.x) + ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() >= Util.convertToPixelPositionYBlack(m.y) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() <= Util.convertToPixelPositionYBlack(m.y) + ClientBoardSpace.SPACE_SIZE/2)
											&& Util.leftMouseClick()) {

										HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE, new PacketClientMove(selectedPiecexPos, selectedPieceyPos, m.x, m.y, id, m.castle, m.enPassant));

										for(ClientPiece p : board.activePieces) {

											if(p.xPos == selectedPiecexPos && p.yPos == selectedPieceyPos) {					
												//Claim any piece existing on the attempted move's square
												if(!board.isSpaceFree(m.x, m.y)) {
													for(int i = 0; i < board.activePieces.size(); i++) {
														if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y) {
															board.claimedPieces.add(board.activePieces.get(i));
															board.activePieces.remove(i);
															break;
														}
													}														
												}						
												//Move piece to new square
												p.xPos = m.x;
												p.yPos = m.y;
												//If the move is en passant, detect and remove the appropriate pawn.
												for(int i = 0; i < board.activePieces.size(); i++) {
													if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y-1) {
														board.claimedPieces.add(board.activePieces.get(i));
														board.activePieces.remove(i);
														break;
													}
												}
												//If the move is a castle, detect and move the appropriate rook
												if(m.castle) {
													if(m.x == 6 && m.y == 0) {
														board.getPieceAt(7, 0).xPos = 5;
													}else if(m.x == 2 && m.y == 0) {
														board.getPieceAt(0, 0).xPos = 3;
													}
												}

												if(!p.moved) p.moved = true;
												validMoves.clear();
												escape = true;
												playersTurn = false;
												moveCount++;
											}					
											if(escape) break;
										}
									}
								} else if(player.color == PlayerColor.white) {

									if((Util.getCursorX() >= Util.convertToPixelPositionXWhite(m.x) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorX() <= Util.convertToPixelPositionXWhite(m.x) + ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() >= Util.convertToPixelPositionYWhite(m.y) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() <= Util.convertToPixelPositionYWhite(m.y) + ClientBoardSpace.SPACE_SIZE/2)
											&& Util.leftMouseClick()) {

										HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE, new PacketClientMove(selectedPiecexPos, selectedPieceyPos, m.x, m.y, id, m.castle, m.enPassant));

										for(ClientPiece p : board.activePieces) {
											if(p.xPos == selectedPiecexPos && p.yPos == selectedPieceyPos) {					
												//Claim any piece existing on the attempted move's square
												if(!board.isSpaceFree(m.x, m.y)) {
													for(int i = 0; i < board.activePieces.size(); i++) {
														if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y) {
															board.claimedPieces.add(board.activePieces.get(i));
															board.activePieces.remove(i);
															break;
														}
													}														
												}						
												//Move piece to new square
												p.xPos = m.x;
												p.yPos = m.y;
												//If the move is en passant, detect and remove the appropriate pawn.
												if(m.enPassant) {
													for(int i = 0; i < board.activePieces.size(); i++) {
														if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y+1) {
															board.claimedPieces.add(board.activePieces.get(i));
															board.activePieces.remove(i);
															break;
														}
													}
												}

												//If the move is a castle, detect and move the appropriate rook
												if(m.castle) {
													if(m.x == 6 && m.y == 7) {
														board.getPieceAt(7, 7).xPos = 5;
													}else if(m.x == 2 && m.y == 7) {
														board.getPieceAt(0, 7).xPos = 3;
													}
												}
												if(!p.moved) p.moved = true;
												validMoves.clear();
												escape = true;
												playersTurn = false;
												moveCount++;
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
					}else {
						if(gameEndState == GAME_END_STATE_CHECKMATE) {
							//Gives the wrong turn number to black if white wins (need to add one)
							if(player.color == PlayerColor.black && finalMove == PlayerColor.white) {
								hvlFont(0).drawc("GG! Checkmate by " + finalMove.toString() + " in " + moveCount+1 + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							}else {
								hvlFont(0).drawc("GG! Checkmate by " + finalMove.toString() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							}
						}else {
							if(player.color == PlayerColor.black && finalMove == PlayerColor.white) {
								hvlFont(0).drawc("Stalemate in " + moveCount+1 + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							}else {
								hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							}
						}
					}
				}
			}
		}
	}

}
