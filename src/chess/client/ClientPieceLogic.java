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
		//System.out.println("========REAL BOARD============");
		//boardArg.print();
		//System.out.println("=========SIM BOARD============");


		//Move the selected piece to the intended space on the sim board and get the check state
		//sim.getPieceAt(selectedPiece.xPos, selectedPiece.yPos).xPos = intendedX;
		//sim.getPieceAt(selectedPiece.xPos, selectedPiece.yPos).yPos = intendedY;
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



		//sim.print();

		if(getCheckState(sim, playerArg)) {
			return false;
		}else {
			return true;
		}
	}

	//should also check for stalemate...
	public static boolean getCheckmateState(ClientBoard board, ClientPlayer player, boolean checkStateArg) {
		if(checkStateArg) {
			if(player.color == PlayerColor.white) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.white) {						
						ArrayList<HvlCoord> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return false;
						}
					}
				}
				return true;
			}
			else if(player.color == PlayerColor.black) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.black) {
						ArrayList<HvlCoord> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return false;
						}
					}
				}
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	public static boolean getCheckState(ClientBoard board, ClientPlayer player) {
		if(player.color == PlayerColor.white) {
			for(ClientPiece piece : board.activePieces) {
				if(piece.color == PieceColor.black) {
					//System.out.println("Checking " + piece.color + " " + piece.type + " for check...");
					for(HvlCoord c : getAllValidMoves(piece, board, player, false)){				
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
					for(HvlCoord c : getAllValidMoves(piece, board, player, false)){						
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
	public static ArrayList<HvlCoord> getAllValidMoves(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
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
	private static ArrayList<HvlCoord> kingMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		//TODO Castling
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();

		if(pieceArg.yPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
					}
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
					}
				}
			}
		}
		if(pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
					}
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
					}
				}
			}
		}

		if(pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
					}
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
				}
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
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
	private static ArrayList<HvlCoord> queenMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
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
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
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
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
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
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
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
						moves.add(new HvlCoord(i, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(i, pieceArg.yPos));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(i, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(i, pieceArg.yPos));
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
						moves.add(new HvlCoord(i, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(i, pieceArg.yPos));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(i, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(i, pieceArg.yPos));
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
						moves.add(new HvlCoord(pieceArg.xPos, i));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, i));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, i));
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
						moves.add(new HvlCoord(pieceArg.xPos, i));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, i));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, i));
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
	private static ArrayList<HvlCoord> bishopMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();

		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
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
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
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
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
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
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
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
	private static ArrayList<HvlCoord> rookMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();		
		//Right squares
		for(int i = pieceArg.xPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new HvlCoord(i, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(i, pieceArg.yPos));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(i, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(i, pieceArg.yPos));
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
						moves.add(new HvlCoord(i, pieceArg.yPos));
				}else {
					moves.add(new HvlCoord(i, pieceArg.yPos));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new HvlCoord(i, pieceArg.yPos));
					}else {
						moves.add(new HvlCoord(i, pieceArg.yPos));
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
						moves.add(new HvlCoord(pieceArg.xPos, i));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, i));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, i));
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
						moves.add(new HvlCoord(pieceArg.xPos, i));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, i));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos, i));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos, i));
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
	private static ArrayList<HvlCoord> knightMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();	
		//Rightmost moves
		if(pieceArg.xPos <= 5) {
			//Upper
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos-1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
					}
				}
			}
			//Lower
			if(pieceArg.yPos <= 6) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos+1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+2, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
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
								moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-2, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
					}
				}
			}
			//Lower
			if(pieceArg.yPos <= 5) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+2, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+2, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
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
								moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+2, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
					}
				}
			}
			//Lower
			if(pieceArg.yPos >= 2) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-2).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-2, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-2, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
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
								moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
					}
				}
			}
			//Lower
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-2, pieceArg.yPos-1).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
						}
					}
				}else {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-2, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
					}else {
						moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
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
	private static ArrayList<HvlCoord> pawnMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		//TODO promotion
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
		//Advancing moves

		if(pieceArg.color == PieceColor.white) {
			if(pieceArg.yPos == 6) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				}
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-2, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-2));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-2));
				}
			}else {
				if(pieceArg.yPos-1 >= 0) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
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
								moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
						}
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos-1 >= 0) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color == PieceColor.black) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
						}
					}
				}		
			}
		} else if(pieceArg.color == PieceColor.black) {
			if(pieceArg.yPos == 1) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				}
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+2, pieceArg, boardArg, player))
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+2));
				}else {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+2));
				}
			}else {
				if(pieceArg.yPos+1 <= 7) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
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
								moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
						}
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos+1 <= 7) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color == PieceColor.white) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
						}else {
							moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
						}
					}
				}		
			}
		}
		return moves;
	}
}
