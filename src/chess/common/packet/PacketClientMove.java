package chess.common.packet;

import java.io.Serializable;

public class PacketClientMove implements Serializable{

	private static final long serialVersionUID = -5510632217376022223L;
	public int existingPieceX;
	public int existingPieceY;	
	public int intendedMoveX;
	public int intendedMoveY;
	public boolean castled;
	public boolean enPassant;
	
	public String id;
	
	public PacketClientMove(int existingPieceXArg, int existingPieceYArg, int intendedMoveXArg, int intendedMoveYArg, String idArg, boolean castledArg, boolean enPassantArg) {
		existingPieceX = existingPieceXArg;
		existingPieceY = existingPieceYArg;
		intendedMoveX = intendedMoveXArg;
		intendedMoveY = intendedMoveYArg;
		id = idArg;
		castled = castledArg;
		enPassant = enPassantArg;
	}
	
}
