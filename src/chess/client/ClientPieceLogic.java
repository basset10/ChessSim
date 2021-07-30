package chess.client;

import java.util.ArrayList;

import com.osreboot.ridhvl2.HvlCoord;

import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;

public class ClientPieceLogic{

	//Used to determine if an attempted move will put the user in self-check.
	public static boolean legalMoveCheck(int intendedX, int intendedY, ClientPiece selectedPiece, ClientBoard boardArg, ClientPlayer playerArg) {
		System.out.println("Verifying legality of move " + selectedPiece.color + " " + selectedPiece.type + " to space " + intendedX + ", " + intendedY);
		//Temporary board to simulate attempted move
		ArrayList<ClientPiece> piecesCopy = new ArrayList<ClientPiece>();

		for(ClientPiece p : boardArg.activePieces) {
			piecesCopy.add(new ClientPiece(p.type, p.color, p.xPos, p.yPos));
		}
		ClientBoard sim = new ClientBoard(piecesCopy);
		for(int i = 0; i < sim.activePieces.size(); i++) {
			if(sim.activePieces.get(i).xPos == intendedX && sim.activePieces.get(i).yPos == intendedY) {
				sim.activePieces.remove(i);
				break;
			}
		}
		for(int i = 0; i < sim.activePieces.size(); i++) {
			if(sim.activePieces.get(i).xPos == selectedPiece.xPos && sim.activePieces.get(i).yPos == selectedPiece.yPos) {
				sim.activePieces.get(i).xPos = intendedX;
				sim.activePieces.get(i).yPos = intendedY;
				break;
			}
		}
		if(getCheckState(sim, playerArg)) {
			return false;
		}else {
			return true;
		}
	}

	public static int getGameEndState(ClientBoard board, ClientPlayer player, boolean checkStateArg) {
		if(checkStateArg) {
			if(player.color == PlayerColor.white) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.white) {						
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_CHECKMATE;
			}
			else if(player.color == PlayerColor.black) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.black) {
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_CHECKMATE;
			}else {
				return ClientGame.GAME_END_STATE_CONTINUE;
			}
		}else {
			if(player.color == PlayerColor.white) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.white) {						
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_STALEMATE;
			}
			else if(player.color == PlayerColor.black) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.black) {
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_STALEMATE;
			}else {
				return ClientGame.GAME_END_STATE_CONTINUE;
			}
		}
	}



	public static boolean getCheckState(ClientBoard board, ClientPlayer player) {
		if(player.color == PlayerColor.white) {
			for(ClientPiece piece : board.activePieces) {
				if(piece.color == PieceColor.black) {
					//System.out.println("Checking " + piece.color + " " + piece.type + " for check...");
					for(ClientMove c : getAllValidMoves(piece, board, player, false)){				
						for(ClientPiece cp : board.activePieces) {
							if(cp.color == PieceColor.white && cp.type == PieceType.king && cp.xPos == (int)c.x && cp.yPos == (int)c.y) {
								//Check if this square can be safely captured...

								System.out.println(piece.color + " " + piece.type + " has a valid move on " + (int)c.x + ", " + (int)c.y);
								System.out.println("your King (white) located on " + (int)c.x + ", " + (int)c.y);
								System.out.println("King is in check!");
								return true;								
							}
						}
					}
				}
			}
			System.out.println("King is not in check");
		}else if(player.color == PlayerColor.black) {
			for(ClientPiece piece : board.activePieces) {
				if(piece.color == PieceColor.white) {
					//System.out.println("Checking " + piece.color + " " + piece.type + " for check...");
					for(ClientMove c : getAllValidMoves(piece, board, player, false)){						
						for(ClientPiece cp : board.activePieces) {
							if(cp.color == PieceColor.black && cp.type == PieceType.king && cp.xPos == (int)c.x && cp.yPos == (int)c.y) {
								//Check if this square can be safely captured...

								System.out.println(piece.color + " " + piece.type + " has a valid move on " + (int)c.x + ", " + (int)c.y);
								System.out.println("your King (black) located on " + (int)c.x + ", " + (int)c.y);
								System.out.println("King is in check!");
								return true;
							}
						}
					}
				}
			}
			System.out.println("King is not in check");
		}
		return false;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece
	 * @return an ArrayList of all valid move coordinates
	 */
	public static ArrayList<ClientMove> getAllValidMoves(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		if(pieceArg.type == PieceType.pawn) {
			moves = pawnMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.knight) {
			moves = knightMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.rook) {
			moves = rookMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.bishop) {
			moves = bishopMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.queen) {
			moves = queenMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.king) {
			moves = kingMoveCheck(pieceArg, boardArg, player, checkTest);
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a king
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> kingMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		//TODO Castling
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();

		//Check if king hasn't moved, rook hasn't moved, spaces to the rook are clear, king is not in check,
		//doing this will not put the king in check, and the king does not jump over a square that will put the king in check.

		if(checkTest) {
			if(!pieceArg.moved) {
				//White right castle
				if(pieceArg.color == PieceColor.white) {
					if(!boardArg.isSpaceFree(7, 7) && boardArg.isSpaceFree(6, 7) && boardArg.isSpaceFree(5, 7)) {
						if(boardArg.getPieceAt(7, 7).type == PieceType.rook && !boardArg.getPieceAt(7, 7).moved) {
							if(legalMoveCheck(6, 7, pieceArg, boardArg, player)
									&& legalMoveCheck(5, 7, pieceArg, boardArg, player)) {
								moves.add(new ClientMove(6, 7, true));
								//Need to also move the rook...
							}
						}
					}
				}
			}
		}

		if(pieceArg.yPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
					}
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
					}
				}
			}
		}
		if(pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos, false));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
					}
				}
			}
		}

		if(pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos, false));
					}
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
					}
				}
			}
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a queen
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> queenMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Upper-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Right squares
		for(int i = pieceArg.xPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Left squares
		for(int i = pieceArg.xPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Lower squares
		for(int i = pieceArg.yPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Upper squares
		for(int i = pieceArg.yPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}										
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a bishop
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> bishopMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();

		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Upper-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		return moves;
	}


	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a rook
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> rookMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();		
		//Right squares
		for(int i = pieceArg.xPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Left squares
		for(int i = pieceArg.xPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Lower squares
		for(int i = pieceArg.yPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Upper squares
		for(int i = pieceArg.yPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}				
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a knight
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> knightMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();	
		//Rightmost moves
		if(pieceArg.xPos <= 5) {
			//Upper
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos-1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos-1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos-1, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos-1, false));
					}
				}
			}
			//Lower
			if(pieceArg.yPos <= 6) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos+1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos+1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos+1, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+2, pieceArg.yPos+1, false));
					}
				}
			}
		}		
		//Mid-right moves
		if(pieceArg.xPos <= 6) {
			//Upper
			if(pieceArg.yPos >= 2) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-2, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-2, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-2, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-2, false));
					}
				}
			}
			//Lower
			if(pieceArg.yPos <= 5) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+2, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+2, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+2, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+2, false));
					}
				}
			}
		}		
		//Mid-left moves
		if(pieceArg.xPos >= 1) {
			//Upper
			if(pieceArg.yPos <= 5) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+2, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+2, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+2, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+2, false));
					}
				}
			}
			//Lower
			if(pieceArg.yPos >= 2) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-2, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-2, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-2, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-2, false));
					}
				}
			}
		}		
		//Leftmost moves
		if(pieceArg.xPos >= 2) {
			//Upper
			if(pieceArg.yPos <= 6) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-2, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-2, pieceArg.yPos+1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos+1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos+1, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos+1, false));
					}
				}
			}
			//Lower
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-2, pieceArg.yPos-1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos-1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos-1, false));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-2, pieceArg.yPos-1, false));
					}
				}
			}
		}		
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a pawn
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> pawnMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		//TODO promotion
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		//Advancing moves

		if(pieceArg.color == PieceColor.white) {
			if(pieceArg.yPos == 6) {
				if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
					}
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-2)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-2, false));
						}
					}
				}
			}else {
				if(pieceArg.yPos-1 >= 0) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false));
						}
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos-1 >= 0) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-1).color == PieceColor.black) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false));
						}
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos-1 >= 0) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color == PieceColor.black) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false));
						}
					}
				}		
			}
		} else if(pieceArg.color == PieceColor.black) {
			if(pieceArg.yPos == 1) {
				if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
					}
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+2)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+2, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+2, false));
						}
					}
				}
			}else {
				if(pieceArg.yPos+1 <= 7) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false));
						}
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos+1 <= 7) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+1).color == PieceColor.white) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false));
						}
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos+1 <= 7) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color == PieceColor.white) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false));
						}
					}
				}		
			}
		}
		return moves;
	}
}
