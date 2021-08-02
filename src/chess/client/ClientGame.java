package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.hvol2.direct.HvlDirect;

import chess.client.ClientMenuManager.MenuState;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.common.NetworkUtil;
import chess.common.Util;
import chess.common.packet.PacketClientMove;
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

	public GameState state = GameState.menu;
	public int gameEndState = GAME_END_STATE_CONTINUE;
	public boolean inCheck = false;
	public boolean playersTurn = false;
	public PlayerColor finalMove;
	public ClientBoard board;
	public ClientPlayer player;
	public ClientPlayer opponent;
	public HashMap<String, ClientPlayer> otherPlayers = new HashMap<String, ClientPlayer>();
	public boolean boardInitialized = false;
	public int moveCount = 0;

	private String id;		
	private boolean debug = false;
	private PacketCollectivePlayerStatus playerPacket = new PacketCollectivePlayerStatus();
	private ArrayList<ClientMove> validMoves = new ArrayList<ClientMove>();
	private int selectedPiecexPos = -1;
	private int selectedPieceyPos = -1;
	//private boolean normalInput = true;			
	private boolean promotionUI = false;
	private int promotionX = -1;
	private int promotionY = -1;

	public ClientGame(String id) {
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
			hvlDraw(hvlQuadc(Util.convertToPixelPositionX(m.x, player), Util.convertToPixelPositionY(m.y, player), 10, 10), hvlColor(0f, 1f, 0f));
		}
	}

	public void reset() {
		playerPacket = new PacketCollectivePlayerStatus();
		otherPlayers = new HashMap<String, ClientPlayer>();
		validMoves = new ArrayList<ClientMove>();
		selectedPiecexPos = -1;
		selectedPieceyPos = -1;
		boardInitialized = false;
		//normalInput = true;
		playersTurn = false;
		state = GameState.menu;
		inCheck = false;
		gameEndState = GAME_END_STATE_CONTINUE;
		finalMove = null;
		moveCount = 0;
		promotionUI = false;
	}

	public void update(float delta){

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
				ClientNetworkReceive.waitForOpponentReady(this);				

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
					drawValidMoves();

					if(inCheck) {
						hvlFont(0).drawc("You are in check!", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
					}else {
						if(ClientPieceLogic.getCheckState(board, opponent)) {
							hvlFont(0).drawc("Opponent is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
						}
					}

					//Receive and handle packets sent from the server.
					ClientNetworkReceive.waitForOpponentMove(this);

					//Detect if a piece is clicked, highlight that piece's valid moves (if existing)
					if(gameEndState == GAME_END_STATE_CONTINUE) {
						if(playersTurn) {
							hvlFont(0).drawc("It is your turn", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							if(!promotionUI) {
								for(ClientPiece p : board.activePieces) {
									if((Util.getCursorX() >= p.getPixelPosition(player).x - ClientPiece.PIECE_SIZE/2
											&& Util.getCursorX() <= p.getPixelPosition(player).x + ClientPiece.PIECE_SIZE/2
											&& Util.getCursorY() >= p.getPixelPosition(player).y - ClientPiece.PIECE_SIZE/2
											&& Util.getCursorY() <= p.getPixelPosition(player).y + ClientPiece.PIECE_SIZE/2)
											&& Util.leftMouseClick() && p.color.toString().equals(player.color.toString())) {
										System.out.println("You clicked a " + p.color + " " + p.type + " on space [ " + p.xPos + ", " + p.yPos + " ]");
										selectedPiecexPos = p.xPos;
										selectedPieceyPos = p.yPos;
										validMoves = p.getAllValidMoves(board, player);
									}									
								}

								//If a valid move is attempted as defined in ClientPieceLogic, execute the move.
								for(ClientMove m : validMoves) {
									boolean escape = false;
									if((Util.getCursorX() >= Util.convertToPixelPositionX(m.x, player) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorX() <= Util.convertToPixelPositionX(m.x, player) + ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() >= Util.convertToPixelPositionY(m.y, player) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() <= Util.convertToPixelPositionY(m.y, player) + ClientBoardSpace.SPACE_SIZE/2)
											&& Util.leftMouseClick()) {

										//HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE, new PacketClientMove(selectedPiecexPos, selectedPieceyPos, m.x, m.y, id, m.castle, m.enPassant));

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
												if(inCheck) {
													inCheck = false;
												}



												if(player.color == PlayerColor.black) {												
													//If the move is a promotion, upgrade the pawn.
													if(p.yPos == 7 && p.type==PieceType.pawn) {
														promotionUI = true;
														validMoves.clear();
														escape = true;
														promotionX = p.xPos;
														promotionY = p.yPos;
													}
													//If the move is en passant, detect and remove the appropriate pawn.
													if(m.enPassant) {
														for(int i = 0; i < board.activePieces.size(); i++) {
															if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y-1) {
																board.claimedPieces.add(board.activePieces.get(i));
																board.activePieces.remove(i);
																break;
															}
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
												}else {
													if(p.yPos == 0 && p.type==PieceType.pawn) {
														promotionUI = true;
														validMoves.clear();
														escape = true;
														promotionX = p.xPos;
														promotionY = p.yPos;
													}
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
												}
												if(!p.moved) p.moved = true;
												validMoves.clear();
												escape = true;
												if(!promotionUI) playersTurn = false;
												if(player.color == PlayerColor.white)
													moveCount++;
											}					
											if(escape) {
												if(!promotionUI) {
													HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
															new PacketClientMove(selectedPiecexPos, selectedPieceyPos, m.x, m.y, id, m.castle, m.enPassant, PacketClientMove.PAWN_PROMOTION_FALSE));
												}
												break;
											}
										}
									}

									if(escape) {
										if(!promotionUI) {
											HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
													new PacketClientMove(selectedPiecexPos, selectedPieceyPos, m.x, m.y, id, m.castle, m.enPassant, PacketClientMove.PAWN_PROMOTION_FALSE));
										}
										break;
									}
								}
							}else {
								//if promotion UI...
								ClientPromotionTypeUI.draw(player);
								if(Util.getCursorX() <= Display.getWidth()/2 + 425+55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425+55-48
										&& Util.getCursorY() <= Display.getHeight()/2+55+48 && Util.getCursorY() >= Display.getHeight()/2+55-48
										&& Util.leftMouseClick()) {
									HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
											new PacketClientMove(selectedPiecexPos, selectedPieceyPos, promotionX, promotionY, id, false, false, PacketClientMove.PAWN_PROMOTION_QUEEN));
									board.getPieceAt(promotionX, promotionY).type = PieceType.queen;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425+55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425+55-48
										&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
										&& Util.leftMouseClick()) {
									HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
											new PacketClientMove(selectedPiecexPos, selectedPieceyPos, promotionX, promotionY, id, false, false, PacketClientMove.PAWN_PROMOTION_KNIGHT));
									board.getPieceAt(promotionX, promotionY).type = PieceType.knight;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
										&& Util.getCursorY() <= Display.getHeight()/2+55+48 && Util.getCursorY() >= Display.getHeight()/2+55-48
										&& Util.leftMouseClick()) {
									HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
											new PacketClientMove(selectedPiecexPos, selectedPieceyPos, promotionX, promotionY, id, false, false, PacketClientMove.PAWN_PROMOTION_ROOK));
									board.getPieceAt(promotionX, promotionY).type = PieceType.rook;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
										&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
										&& Util.leftMouseClick()) {
									HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_MOVE,
											new PacketClientMove(selectedPiecexPos, selectedPieceyPos, promotionX, promotionY, id, false, false, PacketClientMove.PAWN_PROMOTION_BISHOP));
									board.getPieceAt(promotionX, promotionY).type = PieceType.bishop;
									promotionUI = false;
									playersTurn = false;
								}
							}
						}else {
							hvlFont(0).drawc("Waiting for opponent", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
						}
					}else {
						if(gameEndState == GAME_END_STATE_CHECKMATE) {
							hvlFont(0).drawc("GG! Checkmate by " + finalMove.toString() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);					
						}else {
							hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
						}
					}
				}
			}
		}
	}

}
