package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator extends PieceMoveCalculator {
    private ChessBoard board;
    private ChessPosition position;

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public void setPosition(ChessPosition position) {
        this.position = position;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> pieceMoves = new ArrayList<>(List.of());
        setBoard(board);
        setPosition(position);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (position.getRow() == 0 || position.getRow() == 7)) {

        }


        return pieceMoves;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }
}
