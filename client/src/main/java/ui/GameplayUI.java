package ui;


import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.ListGamesResult;
import server.ResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

import server.ServerFacade;
import server.WSEchoClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class GameplayUI {
    String authToken;
    WSEchoClient client = new WSEchoClient();
    Integer gameID;
    String playerColor;
    private final ServerFacade facade;
    GameData game;
    public GameplayUI(String authToken, Integer gameID, String playerColor, ServerFacade facade) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.facade = facade;
    }

    public void run() {
        Scanner scanner;
        try {
            client.WsEchoClient();
            client.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
            scanner = new Scanner(System.in);
            System.out.print("Welcome to the game!\n");
            updateGameInUI();
            System.out.println(redraw());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var result = "";
        while (true) {
            assert result != null;
            if (result.equals("quit")) break;
            System.out.println(printPrompt());
            boolean leave = false;
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("leave")) {
                leave = true;
            }
            try {
                result = eval(line);
                if (result != null) {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
                if (leave) {
                    break;
                }
            }
            catch(NumberFormatException n) {
                System.out.println("Please use the format \" 1 \" for the game number.");
            }
            catch (Throwable e) {
                var msg = e.toString();
                String[] msgs = msg.split(":");
                for (int i = 1; i < msgs.length; i++)  {
                    System.out.println(msgs[i]);
//                    System.out.println(msg);
                }
            }
        }
        System.out.println();
    }

    private void updateGameInUI() throws ResponseException {
        ListGamesResult listResult = facade.list(authToken);
        for (GameData game : listResult.games()) {
            if (gameID == game.gameID()) {
                this.game = game;
            }
        }
    }

    public String eval(String input) throws ResponseException, IOException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (Objects.equals(cmd, "redraw")) {
            if (params.length < 2) {
                throw new ResponseException(
                        "You missed a field somewhere, check your syntax!\n");
            }
            if (params.length > 2) {
                throw new ResponseException(
                        "Too many fields given!\n");
            }
        }
        else if (Objects.equals(cmd, "make") || Objects.equals(cmd, "highlight")) {
            if (params.length < 3) {
                throw new ResponseException(
                        "You missed a field somewhere, check your syntax!\n");
            }
            if (params.length > 3) {
                throw new ResponseException(
                        "Too many fields given!\n");
            }
        }

        return switch (cmd) {
            case "help" -> "";
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "make" -> move(params[1], params[2]);
            case "resign" -> resign();
            case "highlight" -> legals(params[2]);
            default -> "Invalid command.";
        };
    }

        private String printPrompt() {
            return HELP;
        }

        public static final String HELP =
                SET_TEXT_COLOR_GREEN + "Help " + SET_TEXT_COLOR_WHITE + "- see this screen again\n" +
                        SET_TEXT_COLOR_GREEN + "Redraw Chess Board " + SET_TEXT_COLOR_WHITE +
                        "- display board again\n" +
                        SET_TEXT_COLOR_GREEN + "Leave " + SET_TEXT_COLOR_WHITE
                        + "- Leave the game, go back to previous menu.\n" +
                        SET_TEXT_COLOR_GREEN + "Make Move a-h1-8 a-h1-8" + SET_TEXT_COLOR_WHITE +
                        "- Make a valid move on your turn\n" +
                        SET_TEXT_COLOR_GREEN + "Resign " + SET_TEXT_COLOR_WHITE +
                        "- give up and forfeit the game.\n" +
                        SET_TEXT_COLOR_GREEN + "Highlight Legal Moves a-h1-8" + SET_TEXT_COLOR_WHITE +
                        "- show what move a piece can make\n";


    public String redraw() {
        if (Objects.equals(playerColor, "white")) {
            return PostLoginUI.drawBoardWhite(game.chessGame().getBoard(), null);
        }
        else if (Objects.equals(playerColor, "black")){
            return PostLoginUI.drawBoardBlack(game.chessGame().getBoard(), null);
        }
        else {
            return PostLoginUI.drawBoardWhite(game.chessGame().getBoard(), null);
        }
    }

    public String leave() throws IOException {
        UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        client.send(leave);
        return ("You have left the game.");
    }

    public String move(String startPos, String endPos) throws IOException {
        ChessPosition position1 =  parseMove(startPos);
        ChessPosition position2 = parseMove(endPos);
        ChessPiece.PieceType promotionPiece = null;
        if (game.chessGame().getBoard().getPiece(position1).getPieceType() == ChessPiece.PieceType.PAWN
                && position2.getRow() == 8) {
            boolean inputPiece = false;
            Scanner scanner2 = new Scanner(System.in);
            while (!inputPiece) {
                System.out.println("Please select what piece you'd like to promote this pawn to.");
                String promote = scanner2.nextLine();
                if (promote.equalsIgnoreCase("Knight")) {
                    promotionPiece = ChessPiece.PieceType.KNIGHT;
                    inputPiece = true;
                }
                else if (promote.equalsIgnoreCase("Queen")) {
                    promotionPiece = ChessPiece.PieceType.QUEEN;
                    inputPiece = true;
                }
                else if (promote.equalsIgnoreCase("Rook")) {
                    promotionPiece = ChessPiece.PieceType.ROOK;
                    inputPiece = true;
                }
                else if (promote.equalsIgnoreCase("Bishop")) {
                    promotionPiece = ChessPiece.PieceType.BISHOP;
                    inputPiece = true;
                }
                else {
                    System.out.println("This is not a valid piece you can promote the pawn to! Try again!");
                }
            }
            scanner2.close();
        }
        ChessMove move = new ChessMove(position1, position2, promotionPiece);
        MakeMoveCommand moving = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        client.send(moving);
        System.out.println("Move made.\n");
        return redraw();
    }

    public String resign() throws IOException {
        UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        client.send(resign);
        return ("You have resigned.");
    }

    public String legals(String pos) {
        ChessPosition position = parseMove(pos);
        Collection<ChessMove> moves = game.chessGame().validMoves(position);
        if (Objects.equals(playerColor, "white")) {
            return PostLoginUI.drawBoardWhite(game.chessGame().getBoard(), moves);
        }
        else if (Objects.equals(playerColor, "black")){
            return PostLoginUI.drawBoardBlack(game.chessGame().getBoard(), moves);
        }
        return "Error?";
    }

    public ChessPosition parseMove (String pos) {
        String col = pos.substring(0, 1);
        String row = pos.substring(1, 2);
        if (col.equalsIgnoreCase("a")) {
            col = "1";
        }
        else if (col.equalsIgnoreCase("b")) {
            col = "2";
        }
        else if (col.equalsIgnoreCase("c")) {
            col = "3";
        }
        else if (col.equalsIgnoreCase("d")) {
            col = "4";
        }
        else if (col.equalsIgnoreCase("e")) {
            col = "5";
        }
        else if (col.equalsIgnoreCase("f")) {
            col = "6";
        }
        else if (col.equalsIgnoreCase("g")) {
            col = "7";
        }
        else if (col.equalsIgnoreCase("h")) {
            col = "8";
        }

        return new ChessPosition(Integer.parseInt(row), Integer.parseInt(col));
    }


}
