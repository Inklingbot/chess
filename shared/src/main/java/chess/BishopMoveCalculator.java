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


    //Code from class (should it be an override? Is this the function where things are ACTUALLY calculated?
    //What am I doing with the Superclass here? Don't I pass in information when I create a new object????
    @Override
    public Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> pieceMoves = new ArrayList<>(List.of());
        setBoard(board);
        setPosition(position);
        //Check all spaces to the right
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;

        //check spaces up and to the right of the piece
        for (int i = row; i < 7; i++) {
            for (int j = column; j < 7; j++) {
                if (i + 1 < 7 && j + 1 < 7) {
                    ChessPosition availSpace = new ChessPosition(row + 1, column + 1);
                    if (board.getPiece(availSpace) == null) {
                        pieceMoves.add(new ChessMove (position, availSpace, null));
                    }
                }
            }
        }
        //check spaces down and to the right of the piece
        for (int i = row; i < 7; i++) {
            for (int j = column; j > 0; j--) {
                if (i + 1 < 7 && j + 1 < 7) {
                    ChessPosition availSpace = new ChessPosition(row + 1, column - 1);
                    if (board.getPiece(availSpace) == null) {
                        pieceMoves.add(new ChessMove (position, availSpace, null));
                    }
                }
            }
        }
        //check spaces up and to the left of the piece
        for (int i = row; i > 0; i--) {
            for (int j = column; j < 7; j++) {
                if (i + 1 < 7 && j + 1 < 7) {
                    ChessPosition availSpace = new ChessPosition(row - 1, column + 1);
                    if (board.getPiece(availSpace) == null) {
                        pieceMoves.add(new ChessMove (position, availSpace, null));
                    }
                }
            }
        }
        //check spaces down and to the left of the piece
        for (int i = row; i > 0; i--) {
            for (int j = column; j > 0; j--) {
                if (i + 1 < 7 && j + 1 < 7) {
                    ChessPosition availSpace = new ChessPosition(row - 1, column - 1);
                    if (board.getPiece(availSpace) == null) {
                        pieceMoves.add(new ChessMove (position, availSpace, null));
                    }
                }
            }
        }
        //add everything in the set to the collection


        return pieceMoves;
    }



    //All Overrides (equal, toString, and Hashcode)
    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        BishopMoveCalculator calculator = (BishopMoveCalculator) o;

        return calculator.position.equals(position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getPosition());
    }


}
