package chess.client;

import java.util.ArrayList;

import com.osreboot.ridhvl2.HvlCoord;

import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;

public class ClientPieceLogic{


	/**
	 * Finds all possible valid moves on this turn for this piece
	 * @return an ArrayList of all valid move coordinates
	 */
	public static ArrayList<HvlCoord> getAllValidMoves(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
		if(pieceArg.type == PieceType.pawn) {
			moves = pawnMoveCheck(pieceArg, boardArg, player);
		}else if(pieceArg.type == PieceType.knight) {
			moves = knightMoveCheck(pieceArg, boardArg);
		}else if(pieceArg.type == PieceType.rook) {
			moves = rookMoveCheck(pieceArg, boardArg);
		}else if(pieceArg.type == PieceType.bishop) {
			moves = bishopMoveCheck(pieceArg, boardArg);
		}else if(pieceArg.type == PieceType.queen) {
			moves = queenMoveCheck(pieceArg, boardArg);
		}else if(pieceArg.type == PieceType.king) {
			moves = kingMoveCheck(pieceArg, boardArg);
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a king
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<HvlCoord> kingMoveCheck(ClientPiece pieceArg, ClientBoard boardArg){
		//TODO Castling
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();

		if(pieceArg.yPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos-1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {
				moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
				}
			}
		}
		if(pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos)) {
				moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos));
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {
				moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
				}
			}
		}
		if(pieceArg.yPos <= 6) {
			if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos, pieceArg.yPos+1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				}
			}
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {
				moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
				}
			}
		}

		if(pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos)) {
				moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos));
				}
			}
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos >= 1) {
			if(boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {
				moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
			}else {
				if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
				}
			}
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a queen
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<HvlCoord> queenMoveCheck(ClientPiece pieceArg, ClientBoard boardArg){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
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
					moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
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
					moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
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
					moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
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
				moves.add(new HvlCoord(i, pieceArg.yPos));
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(i, pieceArg.yPos));
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
				moves.add(new HvlCoord(i, pieceArg.yPos));
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(i, pieceArg.yPos));
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
				moves.add(new HvlCoord(pieceArg.xPos, i));
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, i));
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
				moves.add(new HvlCoord(pieceArg.xPos, i));
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, i));
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
	private static ArrayList<HvlCoord> bishopMoveCheck(ClientPiece pieceArg, ClientBoard boardArg){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();

		//Upper-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos-i));
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
					moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos-i));
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
					moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-i, pieceArg.yPos+i));
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
					moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+i, pieceArg.yPos+i));
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
	private static ArrayList<HvlCoord> rookMoveCheck(ClientPiece pieceArg, ClientBoard boardArg){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();		
		//Right squares
		for(int i = pieceArg.xPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {	
				moves.add(new HvlCoord(i, pieceArg.yPos));
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(i, pieceArg.yPos));
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
				moves.add(new HvlCoord(i, pieceArg.yPos));
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					moves.add(new HvlCoord(i, pieceArg.yPos));
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
				moves.add(new HvlCoord(pieceArg.xPos, i));
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, i));
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
				moves.add(new HvlCoord(pieceArg.xPos, i));
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					moves.add(new HvlCoord(pieceArg.xPos, i));
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
	private static ArrayList<HvlCoord> knightMoveCheck(ClientPiece pieceArg, ClientBoard boardArg){
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();	
		//Rightmost moves
		if(pieceArg.xPos <= 5) {
			//Upper
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos-1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos-1));
				}
			}
			//Lower
			if(pieceArg.yPos <= 6) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+2, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+2, pieceArg.yPos+1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+2, pieceArg.yPos+1));
				}
			}
		}		
		//Mid-right moves
		if(pieceArg.xPos <= 6) {
			//Upper
			if(pieceArg.yPos >= 2) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-2).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-2));
				}
			}
			//Lower
			if(pieceArg.yPos <= 5) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+2).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+2));
				}
			}
		}		
		//Mid-left moves
		if(pieceArg.xPos >= 1) {
			//Upper
			if(pieceArg.yPos <= 5) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+2).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+2));
				}
			}
			//Lower
			if(pieceArg.yPos >= 2) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-2)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-2).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-2));
				}
			}
		}		
		//Leftmost moves
		if(pieceArg.xPos >= 2) {
			//Upper
			if(pieceArg.yPos <= 6) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-2, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-2, pieceArg.yPos+1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos+1));
				}
			}
			//Lower
			if(pieceArg.yPos >= 1) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-2, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-2, pieceArg.yPos-1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
					}
				}else {
					moves.add(new HvlCoord(pieceArg.xPos-2, pieceArg.yPos-1));
				}
			}
		}		
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a pawn
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<HvlCoord> pawnMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player){
		//TODO promotion
		ArrayList<HvlCoord> moves = new ArrayList<HvlCoord>();
		//Advancing moves

		if(player.color == PlayerColor.white) {
			if(pieceArg.yPos == 6) {
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-2));
			}else {
				if(pieceArg.yPos-1 >= 0) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos-1));
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos-1 >= 0) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos-1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos-1));
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos-1 >= 0) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos-1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos-1));
					}
				}		
			}
		} else if(player.color == PlayerColor.black) {
			if(pieceArg.yPos == 1) {
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
				moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+2));
			}else {
				if(pieceArg.yPos+1 <= 7) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
						moves.add(new HvlCoord(pieceArg.xPos, pieceArg.yPos+1));
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos+1 <= 7) {
				if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos+1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos+1, pieceArg.yPos+1));
					}
				}
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos+1 <= 7) {
				if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {				
					if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos+1).color != pieceArg.color) {
						moves.add(new HvlCoord(pieceArg.xPos-1, pieceArg.yPos+1));
					}
				}		
			}
		}
		return moves;
	}
}
