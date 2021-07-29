package chess.client;

import java.util.ArrayList;

import chess.client.ClientBoardSpace.Color;
import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.common.Util;

//The board the client sees and interacts with.
public class ClientBoard {

	public ArrayList<ArrayList<ClientBoardSpace>> board;

	//ArrayList containing all pieces currently in play. Pieces store their own type, color, and grid position.
	public ArrayList<ClientPiece> activePieces = new ArrayList<ClientPiece>();

	//ArrayList containing all pieces no longer in play.
	public ArrayList<ClientPiece> claimedPieces = new ArrayList<ClientPiece>();




	public ClientBoard(ClientPlayer player) {
		//Initialize the board with starting pieces.
		initialize(player);

	}


	public void update(float delta, ClientPlayer player) {
		this.draw(player);
	}

	public void print() {
		for(int i = 0; i < board.size(); i++) {
			for(int j = 0; j < board.get(i).size(); j++) {

				boolean free = true;

				for(int k = 0; k < activePieces.size(); k++) {
					if(activePieces.get(k).xPos == i && activePieces.get(k).yPos == j) {
						System.out.print("[" + activePieces.get(k).type + "]");
						free = false;
					}					

				}
				if(free) {
					System.out.print("[free]");
				}	
			}
			System.out.println("");
		}
	}

	//create and fill board with starting pieces
	public void initialize(ClientPlayer player) {

		board = new ArrayList<ArrayList<ClientBoardSpace>>();	

		boolean blackFlag = true;
		for(int i = 0; i < 8; i++) {
			board.add(new ArrayList<ClientBoardSpace>());			
			for(int j = 0; j < 8; j++) {
				if(blackFlag) {
					board.get(i).add(new ClientBoardSpace(j, i, Color.black));
				}else {
					board.get(i).add(new ClientBoardSpace(j, i, Color.white));
				}
				if(j != 7) blackFlag = !blackFlag;
			}
		}

		assembleBoard();

	}

	private void assembleBoard() {
		activePieces.add(new ClientPiece(PieceType.rook, PieceColor.black, 0, 0));
		activePieces.add(new ClientPiece(PieceType.knight, PieceColor.black, 1, 0));
		activePieces.add(new ClientPiece(PieceType.bishop, PieceColor.black, 2, 0));
		activePieces.add(new ClientPiece(PieceType.queen, PieceColor.black, 3, 0));
		activePieces.add(new ClientPiece(PieceType.king, PieceColor.black, 4, 0));
		activePieces.add(new ClientPiece(PieceType.bishop, PieceColor.black, 5, 0));
		activePieces.add(new ClientPiece(PieceType.knight, PieceColor.black, 6, 0));
		activePieces.add(new ClientPiece(PieceType.rook, PieceColor.black, 7, 0));

		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 0, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 1, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 2, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 3, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 4, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 5, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 6, 1));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.black, 7, 1));

		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 0, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 1, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 2, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 3, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 4, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 5, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 6, 6));
		activePieces.add(new ClientPiece(PieceType.pawn, PieceColor.white, 7, 6));

		activePieces.add(new ClientPiece(PieceType.rook, PieceColor.white, 0, 7));
		activePieces.add(new ClientPiece(PieceType.knight, PieceColor.white, 1, 7));
		activePieces.add(new ClientPiece(PieceType.bishop, PieceColor.white, 2, 7));
		activePieces.add(new ClientPiece(PieceType.queen, PieceColor.white, 3, 7));
		activePieces.add(new ClientPiece(PieceType.king, PieceColor.white, 4, 7));
		activePieces.add(new ClientPiece(PieceType.bishop, PieceColor.white, 5, 7));
		activePieces.add(new ClientPiece(PieceType.knight, PieceColor.white, 6, 7));
		activePieces.add(new ClientPiece(PieceType.rook, PieceColor.white, 7, 7));
	}

	public boolean isSpaceFree(int xArg, int yArg) {
		for(int i = 0; i < activePieces.size(); i++) {
			if(activePieces.get(i).xPos == xArg && activePieces.get(i).yPos == yArg) {
				return false;
			}
		}
		return true;
	}

	public ClientPiece getPieceAt(int xArg, int yArg){
		for(int i = 0; i < activePieces.size(); i++) {
			if(activePieces.get(i).xPos == xArg && activePieces.get(i).yPos == yArg) {
				try {
					return activePieces.get(i);
				}catch(NullPointerException e) {
					System.out.println("ERROR! Cannot locate piece at " + xArg + ", " + yArg);
				}				
			}
		}
		System.out.println("ERROR! Cannot locate piece at " + xArg + ", " + yArg);
		return new ClientPiece();
	}

	//Returns the board space at a given x and y position.
	public ClientBoardSpace getSpaceAt(int xArg, int yArg) {
		return board.get(xArg).get(yArg);
	}

	private void draw(ClientPlayer player) {

		//Draw the board itself
		for(int i = 0; i < board.size(); i++) {	
			for(int j = 0; j < board.get(i).size(); j++) {

				getSpaceAt(i, j).draw();				
			}			
		}

		//Draw the board pieces in their current locations
		for(int i = 0; i < activePieces.size(); i++) {
			activePieces.get(i).draw(player);
		}



	}

}
