package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator extends PieceMoveCalculator {
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

        int row = position.getRow();
        int column = position.getColumn();
        if (row + 1 <= 8) {
            ChessPosition availSpace = new ChessPosition(row + 1, column);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (row - 1 > 0) {
            ChessPosition availSpace = new ChessPosition(row - 1, column);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (column + 1 <= 8) {
            ChessPosition availSpace = new ChessPosition(row, column + 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (column - 1 > 0) {
            ChessPosition availSpace = new ChessPosition(row, column - 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (row - 1 > 0 && column + 1 <= 8) {
            ChessPosition availSpace = new ChessPosition(row - 1, column + 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (row - 1 > 0 &&  column - 1 > 0) {
            ChessPosition availSpace = new ChessPosition(row - 1, column - 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (row + 1 <= 8 &&  column + 1 <= 8) {
            ChessPosition availSpace = new ChessPosition(row + 1, column + 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }

        if (row + 1 <= 8 &&  column - 1 > 0) {
            ChessPosition availSpace = new ChessPosition(row + 1, column - 1);
            if (getBoard().getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (getBoard().getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
        }


        return pieceMoves;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }
}
