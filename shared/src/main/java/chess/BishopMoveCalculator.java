package chess;

import java.util.*;

public class BishopMoveCalculator extends PieceMoveCalculator {
    private ChessBoard board;
    private ChessPosition position;

    BishopMoveCalculator() {

    }

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
        return bishopLogic(piece, board, position);
    }



    //All Overrides (equal, toString, and Hashcode)
    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }


    public static Collection<ChessMove> bishopLogic(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> pieceMoves = new ArrayList<>(List.of());
        int availRow = position.getRow() + 1;
        int availColumn = position.getColumn() + 1;

        //Check down and right
        availRow = position.getRow() - 1;
        availColumn = position.getColumn() + 1;
        while(availRow > 0 && availColumn <= 8) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
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
            availColumn++;
        }
        //check up and left
        availRow = position.getRow() + 1;
        availColumn = position.getColumn() - 1;
        while(availRow <= 8 && availColumn > 0) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
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
            availColumn--;
        }

        availRow = position.getRow() - 1;
        availColumn = position.getColumn() - 1;
        while(availRow > 0 && availColumn > 0) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
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
            availColumn--;
        }

        availRow = position.getRow() + 1;
        availColumn = position.getColumn() + 1;
        while(availRow <= 8 && availColumn <= 8) {
            ChessPosition availSpace = new ChessPosition(availRow, availColumn);
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
            availColumn++;
        }

        //add everything in the set to the collection


        return pieceMoves;
    }
}
