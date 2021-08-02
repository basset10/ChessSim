package chess.client;

import com.osreboot.hvol2.base.anarchy.HvlAgentClientAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;

import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.common.NetworkUtil;
import chess.common.packet.PacketClientGameOver;
import chess.common.packet.PacketClientGameReady;
import chess.common.packet.PacketClientMove;
import chess.common.packet.PacketServerGameReadyResponse;
import chess.common.packet.PacketServerMoveResponse;

public class ClientNetworkReceive {
	
	/**
	 * Receives game ready packet from the server and initializes the game.
	 */
	public static void waitForOpponentReady(ClientGame game) {
		if(HvlDirect.getKeys().contains(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY)) {
			PacketServerGameReadyResponse readyPacket = HvlDirect.getValue(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY);
			((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY);
			for (String name : readyPacket.collectiveClientGameReady.keySet()){
				if(name.equals(game.player.id)) {
					if(readyPacket.isWhite) {
						game.player.color = PlayerColor.white;
						game.playersTurn = true;
					}else {
						game.player.color = PlayerColor.black;
					}
					game.board = new ClientBoard(game.player);
					game.boardInitialized = true;
				}else {
					game.opponent = new ClientPlayer(name);
					if(readyPacket.isWhite) {
						game.opponent.color = PlayerColor.black;
					}else {
						game.opponent.color = PlayerColor.white;
					}
				}
			}

		}else {
			if(game.otherPlayers.size() == 1) {
				HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_GAME_READY, new PacketClientGameReady(game.player.id));
			}
		}
	}
	
	/**
	 * Receives move packets from the server and modifies the game state.
	 */
	public static void waitForOpponentMove(ClientGame game) {
		if(HvlDirect.getKeys().contains(NetworkUtil.KEY_SERVER_MOVE_RESPONSE)) {
			PacketServerMoveResponse movePacket = HvlDirect.getValue(NetworkUtil.KEY_SERVER_MOVE_RESPONSE);
			((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_SERVER_MOVE_RESPONSE);	
			if(!movePacket.id.equals(game.player.id)) {
				System.out.println("Opponent's move received.");					
				System.out.println("Move packet id: " + movePacket.id);
				System.out.println("Your id: " + game.player.id);					
				System.out.println("Attempting to move piece at " + movePacket.packet.existingPieceX + ", " + movePacket.packet.existingPieceY 
						+ ". This piece is a " + game.board.getPieceAt(movePacket.packet.existingPieceX, movePacket.packet.existingPieceY ).color + " "
						+ game.board.getPieceAt(movePacket.packet.existingPieceX, movePacket.packet.existingPieceY ).type);
				for(ClientPiece pc : game.board.activePieces) {
					if(pc.enPassantVulnerable) pc.enPassantVulnerable = false;
				}
				for(ClientPiece p : game.board.activePieces) {
					boolean escape = false;
					if(p.xPos == movePacket.packet.existingPieceX && p.yPos == movePacket.packet.existingPieceY) {			
						if(!game.board.isSpaceFree(movePacket.packet.intendedMoveX, movePacket.packet.intendedMoveY)) {
							for(int i = 0; i < game.board.activePieces.size(); i++) {
								if(game.board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX && game.board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY) {
									game.board.claimedPieces.add(game.board.activePieces.get(i));
									game.board.activePieces.remove(i);
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
								for(int i = 0; i < game.board.activePieces.size(); i++) {
									if(game.board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX 
											&& game.board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY+1) {
										game.board.claimedPieces.add(game.board.activePieces.get(i));
										game.board.activePieces.remove(i);
										break;
									}
								}	
							}
							//Black perspective
							if(movePacket.packet.intendedMoveY == 5) {
								for(int i = 0; i < game.board.activePieces.size(); i++) {
									if(game.board.activePieces.get(i).xPos == movePacket.packet.intendedMoveX 
											&& game.board.activePieces.get(i).yPos == movePacket.packet.intendedMoveY-1) {
										game.board.claimedPieces.add(game.board.activePieces.get(i));
										game.board.activePieces.remove(i);
										break;
									}
								}	
							}
						}

						//if a move packet labeled as "castle" is received, detect and move the appropriate rook.
						if(movePacket.packet.castled) {
							if(movePacket.packet.intendedMoveX == 6 && movePacket.packet.intendedMoveY == 7) {
								game.board.getPieceAt(7, 7).xPos = 5;
							}else if(movePacket.packet.intendedMoveX == 2 && movePacket.packet.intendedMoveY == 7) {
								game.board.getPieceAt(0, 7).xPos = 3;
							}if(movePacket.packet.intendedMoveX == 6 && movePacket.packet.intendedMoveY == 0) {
								game.board.getPieceAt(7, 0).xPos = 5;
							}else if(movePacket.packet.intendedMoveX == 2 && movePacket.packet.intendedMoveY == 0) {
								game.board.getPieceAt(0, 0).xPos = 3;
							}
						}

						//Move the piece to its intended position
						p.xPos = movePacket.packet.intendedMoveX;
						p.yPos = movePacket.packet.intendedMoveY;
						if(game.player.color == PlayerColor.black) {
							game.moveCount++;
						}
						
						//if the move packet indicates a pawn needs to be promoted, locate and promote that pawn.
						if(movePacket.packet.promotionType != PacketClientMove.PAWN_PROMOTION_FALSE) {
							if(movePacket.packet.promotionType == PacketClientMove.PAWN_PROMOTION_QUEEN) {
								p.type = PieceType.queen;
							}else if(movePacket.packet.promotionType == PacketClientMove.PAWN_PROMOTION_KNIGHT) {
								p.type = PieceType.knight;
							}else if(movePacket.packet.promotionType == PacketClientMove.PAWN_PROMOTION_ROOK) {
								p.type = PieceType.rook;
							}else if(movePacket.packet.promotionType == PacketClientMove.PAWN_PROMOTION_BISHOP) {
								p.type = PieceType.bishop;
							}
						}
						
						if(!p.moved) p.moved = true;
						game.inCheck = ClientPieceLogic.getCheckState(game.board, game.player);
						game.gameEndState = ClientPieceLogic.getGameEndState(game.board, game.player, game.inCheck);
						System.out.println("Game End State:");
						System.out.println(game.gameEndState);
						if(game.gameEndState == ClientGame.GAME_END_STATE_CHECKMATE || game.gameEndState == ClientGame.GAME_END_STATE_STALEMATE){
							System.out.println("Sending failure packet...");
							HvlDirect.writeTCP(NetworkUtil.KEY_CLIENT_GAME_OVER, new PacketClientGameOver(game.gameEndState, game.player.color));
							if(game.player.color == PlayerColor.black) {
								game.finalMove = PlayerColor.white;
							}else {
								game.finalMove = PlayerColor.black;
							}
						}else {
							game.playersTurn = true;
						}								
						escape = true;
					}					
					if(escape) break;
				}					
			}
		}
		
		if(game.gameEndState == ClientGame.GAME_END_STATE_CONTINUE) {
			if(HvlDirect.getKeys().contains(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE)) {
				System.out.println("Victory packet received!");
				PacketClientGameOver gameOverPacket = HvlDirect.getValue(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE);
				((HvlAgentClientAnarchy)HvlDirect.getAgent()).getTable().remove(NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE);
				game.gameEndState = gameOverPacket.gameEndState;
				if(gameOverPacket.playerColor == PlayerColor.black) {
					game.finalMove = PlayerColor.white;
				}else {
					game.finalMove = PlayerColor.black;
				}
			}
		}
	}
	
}
