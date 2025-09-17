package chess;

import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator extends PieceMoveCalculator {
    private ChessBoard board;
    private ChessPosition position;

    @Override
    public Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (position.getRow() == 0 || position.getRow() == 7)) {

        }
        return List.of();
    }
}
