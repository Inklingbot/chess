package chess;

import java.util.Collection;
import java.util.List;

public class KnightMoveCalculator extends PieceMoveCalculator {
    private ChessBoard board;
    private ChessPosition position;

    @Override
    public Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return List.of();
    }
}
