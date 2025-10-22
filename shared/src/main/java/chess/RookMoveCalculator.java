package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoveCalculator extends PieceMoveCalculator {
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
        setBoard(board);
        setPosition(position);

        return rookLogic(piece, board, position);
    }

    public static Collection<ChessMove> rookLogic(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> pieceMoves = new ArrayList<>(List.of());
        int availRow = position.getRow() + 1;
        int availColumn = position.getColumn();

        while(availRow <= 8) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
            //If the space is empty, or if the team color of the piece there is different from the og's piece color
            if (board.getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (board.getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
                break;
            }
            else {
                break;
            }
            availRow++;
        }

        availRow = position.getRow() - 1;
        while(availRow > 0) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
            //If the space is empty, or if the team color of the piece there is different from the og's piece color
            if (board.getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (board.getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
                break;
            }
            else {
                break;
            }
            availRow--;
        }

        availRow = position.getRow();
        availColumn = position.getColumn() + 1;
        while(availColumn <= 8) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
            //If the space is empty, or if the team color of the piece there is different from the og's piece color
            if (board.getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (board.getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
                break;
            }
            else {
                break;
            }
            availColumn++;
        }

        availColumn = position.getColumn() - 1;
        while(availColumn > 0) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
            //If the space is empty, or if the team color of the piece there is different from the og's piece color
            if (board.getPiece(availSpace) == null) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
            }
            else if (board.getPiece(availSpace).getTeamColor() != piece.getTeamColor()) {
                pieceMoves.add(new ChessMove (position, availSpace, null));
                break;
            }
            else {
                break;
            }
            availColumn--;
        }


        return pieceMoves;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }
}
