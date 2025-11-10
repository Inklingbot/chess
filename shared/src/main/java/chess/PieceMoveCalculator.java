package chess;

import java.util.Collection;
import java.util.List;

public abstract class PieceMoveCalculator {



    public static PieceMoveCalculator create (ChessPiece piece) {
        switch(piece.getPieceType()) {
            case ChessPiece.PieceType.KNIGHT:
                return new KnightMoveCalculator();
            case ChessPiece.PieceType.ROOK:
                return new RookMoveCalculator();
            case ChessPiece.PieceType.PAWN:
                return new PawnMoveCalculator();
            case ChessPiece.PieceType.KING:
                return new KingMoveCalculator();
            case ChessPiece.PieceType.BISHOP:
                return new BishopMoveCalculator();
            case ChessPiece.PieceType.QUEEN:
                return new QueenMoveCalculator();
            default:
                throw new IllegalStateException("Unexpected value: " + piece);
        }

    }
    public Collection<ChessMove> pieceMovesCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {

        PieceMoveCalculator calculator = create(piece);
        Collection<ChessMove> positionsPossible;
        positionsPossible = calculator.pieceMoves(piece, board, position);
        return positionsPossible;
    }

    public abstract Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position);

    //should return a list???
}
