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

        //Find piece color
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            //Game Start Case
            boolean wasAdded = false;
            int row = position.getRow();
            int column = position.getColumn();
            if (row == 7) {
                ChessPosition availSpace = new ChessPosition(row - 1, column);
                if (addIfNull(availSpace, pieceMoves)) {
                    wasAdded = true;
                    availSpace = new ChessPosition(row - 2, column);
                    addIfNull(availSpace, pieceMoves);
                }

            }
            //Capturing
            if (column - 1 > 0 && row - 1 <= 8) {
                ChessPosition availSpace = new ChessPosition(row - 1, column - 1);
                if (board.getPiece(availSpace) != null) {
                    addIfEnemyPiece(availSpace, pieceMoves, piece);
                }
            }
            if (column + 1 <= 8 && row - 1 <= 8) {

                ChessPosition availSpace = new ChessPosition(row - 1, column + 1);
                if (board.getPiece(availSpace) != null) {
                    addIfEnemyPiece(availSpace, pieceMoves, piece);
                }
            }

            //Move 1 Forward
                if (wasAdded == false) {
                    ChessPosition availSpace = new ChessPosition(row - 1, column);
                    addIfNull(availSpace, pieceMoves);
                }


        }

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            //Game Start Case
            boolean wasAdded = false;
            int row = position.getRow();
            int column = position.getColumn();
            if (row == 2) {

                ChessPosition availSpace = new ChessPosition(row + 1, column);
                if (addIfNull(availSpace, pieceMoves)) {
                    wasAdded = true;
                    availSpace = new ChessPosition(row + 2, column);
                    addIfNull(availSpace, pieceMoves);
                }
            }
            //Capturing
            if (column - 1 > 0 && row + 1 <= 8) {
                ChessPosition availSpace = new ChessPosition(row + 1, column - 1);
                if (board.getPiece(availSpace) != null) {
                    addIfEnemyPiece(availSpace, pieceMoves, piece);
                }
            }
            if (column + 1 <= 8 && row + 1 <= 8) {
                ChessPosition availSpace = new ChessPosition(row + 1, column + 1);
                if (board.getPiece(availSpace) != null) {
                    addIfEnemyPiece(availSpace, pieceMoves, piece);
                }
            }

            //Move 1 Forward
            if (wasAdded == false) {
                ChessPosition availSpace = new ChessPosition(row + 1, column);
                addIfNull(availSpace, pieceMoves);
            }


        }


        return pieceMoves;
    }

    boolean addIfNull(ChessPosition newPosition, Collection<ChessMove> pieceMoves) {
        if (getBoard().getPiece(newPosition) == null) {
            if (newPosition.getRow() == 8 || newPosition.getRow() == 1) {

                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                return true;
                //Somehow call the pieceMoves method for the other piece, and then assign all to the pieceMoves collection
            }
            pieceMoves.add(new ChessMove(position, newPosition, null));
            return true;
        }
        return false;
        //Check for promotion piece
    }

    void addIfEnemyPiece(ChessPosition newPosition, Collection<ChessMove> pieceMoves, ChessPiece piece) {
        if (getBoard().getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
            //Check for promotion space??
            if (newPosition.getRow() == 8  || newPosition.getRow() == 1) {

                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                pieceMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                return;
                //Somehow call the pieceMoves method for the other piece, and then assign all to the pieceMoves collection
            }
            pieceMoves.add(new ChessMove(position, newPosition, null));
        }
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", super.toString());
    }
}
