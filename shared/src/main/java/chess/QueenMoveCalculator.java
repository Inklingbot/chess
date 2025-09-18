package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoveCalculator extends PieceMoveCalculator {
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

        int availRow = position.getRow() + 1;
        int availColumn = position.getColumn() + 1;

        return pieceMoves;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }
}
