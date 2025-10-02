package chess;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    TeamColor teamColor = WHITE;

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getBoard());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean wasValid = false;

        //check that the start position is our team, and there's a piece there
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Invalid move.");
        }
        if (board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Invalid move.");
        }


        //Check every valid move against the move that we want to be made
        for (ChessMove valid : validMoves(move.startPosition())) {
            if (valid.equals(move)) {
                wasValid = true;
            }
        }
        //Check to see if the team is in check
        if (isInCheck(teamColor)) {
            //copy the board and make the move, if still in check, don't do that thing
            ChessBoard testCopy;
            testCopy = (ChessBoard) board.clone();

            ChessPiece piece = testCopy.getPiece(move.getStartPosition());
            testCopy.removePiece(move.getStartPosition());
            if (move.promotionPiece() != null) {
                piece.setPieceType(move.promotionPiece());
            }
            testCopy.addPiece(move.endPosition(), piece);
            ChessBoard clonedBoard;
            clonedBoard = (ChessBoard) board.clone();
            setBoard(testCopy);
            if (isInCheck(teamColor)) {
                setBoard(clonedBoard);
                throw new InvalidMoveException("Move not allowed! You are in Check.");
            }
            else {
                if (!wasValid || board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
                    setBoard(clonedBoard);
                    //throw exception
                    throw new InvalidMoveException(move.toString());
                }
                setBoard(clonedBoard);

                //Delete piece at current location, and move it to new location
                //Check if piece is at new location, then check to make sure it's the opposite piece type, then also check to make sure it's not a king
                if (board.getPiece(move.getEndPosition()) != null) {
                    if (board.getPiece(move.getEndPosition()).getTeamColor() != getTeamTurn() && board.getPiece(move.getEndPosition()).getPieceType() != ChessPiece.PieceType.KING) {
                        board.removePiece(move.getEndPosition());
                    } else if (board.getPiece(move.getEndPosition()).getTeamColor() == getTeamTurn()) {
                        throw new InvalidMoveException(move.toString());
                    }
                }

                //This is making the actual move :O
                ChessPiece piecewise = board.getPiece(move.getStartPosition());
                moveNSwitch(move, piecewise);


                return;
            }


        }
        //IF the move was not a validMove, if the piece was in check, if there is not a piece at the start position, or if the move was for the other team...
        else if (!wasValid ||  board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            //throw exception
            throw new InvalidMoveException(move.toString());
        }

        //Delete piece at current location, and move it to new location
        //Check if piece is at new location, then check to make sure it's the opposite piece type, then also check to make sure it's not a king
        else if (board.getPiece(move.getEndPosition()) != null) {
            if (board.getPiece(move.getEndPosition()).getTeamColor() != getTeamTurn() && board.getPiece(move.getEndPosition()).getPieceType() != ChessPiece.PieceType.KING) {
                board.removePiece(move.getEndPosition());
            }
            else if (board.getPiece(move.getEndPosition()).getTeamColor() == getTeamTurn()) {
                throw new InvalidMoveException(move.toString());
            }
        }

        //This is making the actual move :O
        ChessPiece piece = board.getPiece(move.getStartPosition());
        moveNSwitch(move, piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean check = false;
        //iterate through every square
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPos  = new ChessPosition(i, j);
                //check if enemy piece
                if (board.getPiece(currPos) != null && board.getPiece(currPos).getTeamColor() != teamColor) {
                    //check every move it makes
                    for (ChessMove move : validMoves(currPos)) {
                        //if the space it moves to is not null
                        if (board.getPiece(move.endPosition()) != null) {
                            //if it's a king it can move to
                            if (board.getPiece(move.endPosition()).getTeamColor() == teamColor && board.getPiece(move.endPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                                check = true;
                                return check;
                            }
                        }
                    }
                }
            }
        }


        return check;
    }
    //Move the piece, then switch team turns
    public void moveNSwitch(ChessMove move, ChessPiece piece) {
        board.removePiece(move.getStartPosition());
        if (move.promotionPiece() != null) {
            piece.setPieceType(move.promotionPiece());
        }
        board.addPiece(move.endPosition(), piece);

        //turn changer (I love turn changing)
        if (getTeamTurn() == WHITE) {
            setTeamTurn(BLACK);
        }
        else if (getTeamTurn() == BLACK) {
            setTeamTurn(WHITE);
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //You must be in check to be in checkmate, fellas
        if (isInCheck(teamColor)) {

            //loop through every square
            for (int k = 1; k <= 8; k++) {
                for (int l = 1; l <= 8; l++) {
                    ChessPosition currentPosition = new ChessPosition(k, l);
                    //ensure it's the right team's piece (and there's a piece there)
                    if(board.getPiece(currentPosition) != null) {
                            Collection<ChessMove> guessAndCheck = validMoves(currentPosition);
                            //for each possible move
                        if (guessAndCheck != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //You must not be in check to be in stalemate, fellas
        if (!isInCheck(teamColor)) {

            //loop through every square
            for (int k = 1; k <= 8; k++) {
                for (int l = 1; l <= 8; l++) {
                    ChessPosition currentPosition = new ChessPosition(k, l);
                    //ensure it's the right team's piece (and there's a piece there)
                    if(board.getPiece(currentPosition) != null) {
                        Collection<ChessMove> guessAndCheck = validMoves(currentPosition);
                        //for each possible move
                        if (guessAndCheck != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
