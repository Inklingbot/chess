package ui;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.NotificationHandler;
import server.ResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

import server.ServerFacade;
import server.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class GameplayUI implements NotificationHandler {
    String authToken;
    WebSocketFacade clientWebsocketFacade;
    Integer gameID;
    String playerColor;
    GameData game;
    ChessGame chessGame;
    ServerFacade facade;
    public GameplayUI(String authToken, Integer gameID, String playerColor, ServerFacade facade) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.facade = facade;
        this.chessGame = null;
    }

    public void run() {
        Scanner scanner;
        try {
            clientWebsocketFacade = new WebSocketFacade("http://localhost:8080",
                            new Notification("Connected.\n"), this);

            clientWebsocketFacade.joinGame(authToken, gameID, playerColor);
            scanner = new Scanner(System.in);
            System.out.print("Welcome to the game!\n");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var result = "";
        while (true) {
            assert result != null;
            if (result.equals("quit")) {break;};
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

//    private void updateGameInUI() throws ResponseException {
//        ListGamesResult listResult = facade.list(authToken);
//        for (GameData game : listResult.games()) {
//            if (gameID == game.gameID()) {
//                this.game = game;
//            }
//        }
//    }

    public String eval(String input) throws ResponseException, IOException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (Objects.equals(cmd, "redraw"))  {
            if (params.length > 0) {
                throw new ResponseException(
                        "Too many fields given!\n");
            }
        }
        if (Objects.equals(cmd, "highlight")) {
            if (params.length > 1) {
                throw new ResponseException(
                        "Too many fields given!\n");
            }
            if (params.length < 1) {
                throw new ResponseException("You missed a field somewhere, check your syntax!\n");
            }
        }
        else if (Objects.equals(cmd, "move")) {
            if (params.length < 2) {
                throw new ResponseException(
                        "You missed a field somewhere, check your syntax!\n");
            }
            if (params.length > 2) {
                throw new ResponseException(
                        "Too many fields given!\n");
            }
        }

        return switch (cmd) {
            case "help" -> "";
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move(params[0], params[1]);
            case "resign" -> resign();
            case "highlight" -> legals(params[0]);
            default -> "Invalid command.";
        };
    }

        private String printPrompt() {
            return HELP;
        }

        public static final String HELP =
                SET_TEXT_COLOR_GREEN + "Help " + SET_TEXT_COLOR_WHITE + "- see this screen again\n" +
                        SET_TEXT_COLOR_GREEN + "Redraw " + SET_TEXT_COLOR_WHITE +
                        "- display board again\n" +
                        SET_TEXT_COLOR_GREEN + "Leave " + SET_TEXT_COLOR_WHITE
                        + "- Leave the game, go back to previous menu.\n" +
                        SET_TEXT_COLOR_GREEN + "Move a-h1-8 a-h1-8" + SET_TEXT_COLOR_WHITE +
                        "- Make a valid move on your turn\n" +
                        SET_TEXT_COLOR_GREEN + "Resign " + SET_TEXT_COLOR_WHITE +
                        "- give up and forfeit the game.\n" +
                        SET_TEXT_COLOR_GREEN + "Highlight a-h1-8 " + SET_TEXT_COLOR_WHITE +
                        "- show what moves a piece can make\n";


    public String redraw() {
        try {
            if (Objects.equals(playerColor, "white")) {
                return PostLoginUI.drawBoardWhite(chessGame.getBoard(), null);
            }
            else if (Objects.equals(playerColor, "black")){
                return PostLoginUI.drawBoardBlack(chessGame.getBoard(), null);
            }
            else {
                return PostLoginUI.drawBoardWhite(chessGame.getBoard(), null);
            }
        }
        catch(Exception e) {
            System.out.println("There was an exception.");
        }
        return "";
    }

    public String leave() throws IOException, ResponseException {
        clientWebsocketFacade.leaveGame(authToken, gameID);
        return ("You have left the game.");
    }

    public String move(String startPos, String endPos) throws IOException, ResponseException {
        ChessPosition position1 =  parseMove(startPos);
        ChessPosition position2 = parseMove(endPos);
        ChessPiece.PieceType promotionPiece = null;
        if (chessGame.getBoard().getPiece(position1).getPieceType() == ChessPiece.PieceType.PAWN
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
        clientWebsocketFacade.makeMove(move, authToken, gameID);
        System.out.println("Move made.\n");
//        updateGameInUI();
        return "";
    }

    public String resign() throws ResponseException {
        clientWebsocketFacade.resign(authToken, gameID);
        return ("You have resigned.\n");
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


    @Override
    public void notify(ServerMessage newMessage, String message) {
        if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGame outMessage = GSON.fromJson(message, LoadGame.class);

            ChessGame game = GSON.fromJson(outMessage.getGame(), ChessGame.class);
            this.game = new GameData(0, "white", "black", "name", game);
            this.chessGame = game;
            System.out.println(redraw());
        }
        else if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage outMessage = GSON.fromJson(message, ErrorMessage.class);
            System.out.println(SET_TEXT_COLOR_RED + outMessage.toString());
        }
        else if (newMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            Notification outMessage = GSON.fromJson(message, Notification.class);
            System.out.println(SET_TEXT_COLOR_MAGENTA + outMessage.toString());
        }
    }
}
