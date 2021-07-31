package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl2.HvlCoord;

import chess.client.ClientPlayer.PlayerColor;

public class ClientPiece {

	public static final int PIECE_SIZE = 50;
	//Used to check castling legality
	public boolean moved = false;

	//Need to check for all possible valid moves, per piece. Store these moves in an arraylist somehow.
	//First just get valid moves working regardless of player order or turn

	enum PieceType{
		pawn,
		knight,
		rook,
		bishop,
		queen,
		king;
	}

	enum PieceColor{
		white,
		black;
	}

	public int xPos;
	public int yPos;
	public PieceType type;
	public PieceColor color;
	public boolean enPassantVulnerable = false;

	public ClientPiece(PieceType typeArg, PieceColor colorArg, int xPosArg, int yPosArg) {
		type = typeArg;
		xPos = xPosArg;
		yPos = yPosArg;
		color = colorArg;
	}

	public ClientPiece() {}

	public ArrayList<ClientMove> getAllValidMoves(ClientBoard boardArg, ClientPlayer player) {
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		moves = ClientPieceLogic.getAllValidMoves(this, boardArg, player, true);
		return moves;
	}


	public HvlCoord getPixelPositionWhitePerspective() {
		return new HvlCoord((xPos)*ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2),
				(yPos)*ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));
	}

	public HvlCoord getPixelPositionBlackPerspective() {
		return new HvlCoord((xPos)*-ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2),
				(yPos)*-ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));
	}

	public void draw(ClientPlayer p) {

		if(p.color == PlayerColor.white) {
			if(this.type == PieceType.pawn) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_PAWN_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_PAWN_BLACK));
				}
			}else if(this.type == PieceType.rook) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_ROOK_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_ROOK_BLACK));
				}
			}else if(this.type == PieceType.knight) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KNIGHT_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KNIGHT_BLACK));
				}
			}else if(this.type == PieceType.bishop) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_BISHOP_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_BISHOP_BLACK));
				}
			}else if(this.type == PieceType.queen) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_QUEEN_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_QUEEN_BLACK));
				}
			}else if(this.type == PieceType.king) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KING_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionWhitePerspective().x, this.getPixelPositionWhitePerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KING_BLACK));
				}
			}

		}else if(p.color == PlayerColor.black) {
			if(this.type == PieceType.pawn) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_PAWN_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_PAWN_BLACK));
				}
			}else if(this.type == PieceType.rook) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_ROOK_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_ROOK_BLACK));
				}
			}else if(this.type == PieceType.knight) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KNIGHT_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KNIGHT_BLACK));
				}
			}else if(this.type == PieceType.bishop) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_BISHOP_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_BISHOP_BLACK));
				}
			}else if(this.type == PieceType.queen) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_QUEEN_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_QUEEN_BLACK));
				}
			}else if(this.type == PieceType.king) {
				if(this.color == PieceColor.white) {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KING_WHITE));
				}else {
					hvlDraw(hvlQuadc(this.getPixelPositionBlackPerspective().x, this.getPixelPositionBlackPerspective().y, PIECE_SIZE, PIECE_SIZE), hvlTexture(ClientLoader.INDEX_KING_BLACK));
				}
			}			
		}
	}

}
