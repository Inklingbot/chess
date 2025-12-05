package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import server.ResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;
import server.WSEchoClient;
import websocket.commands.UserGameCommand;

public class GameplayUI {
    String authToken;
    WSEchoClient client = new WSEchoClient();
    Integer gameID;
    String playerColor;
    ChessGame game;
    public GameplayUI(String authToken, Integer gameID, String playerColor) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;

    }

    public void run() {
        client.WsEchoClient();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome to the game!");
        System.out.println(redraw());

        var result = "";

        while (true) {
            assert result != null;
            if (result.equals("quit")) break;
            printPrompt();
            boolean leave = false;
            String line = scanner.nextLine();
            if (Objects.equals(line, "logout")) {
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

    public String eval(String input) throws ResponseException {
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
            case "help" -> printPrompt();
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "make" -> move(params[1], params[2]);
            case "resign" -> resign();
            case "highlight" -> legals(params[2]);
            default -> "";
        };
    }

        private void printPrompt() {
            System.out.print(HELP);
        }

        public static final String HELP =
                SET_TEXT_COLOR_GREEN + "Help " + SET_TEXT_COLOR_WHITE + "- see this screen again\n" +
                        SET_TEXT_COLOR_GREEN + "Redraw Chess Board " + SET_TEXT_COLOR_WHITE +
                        "- display board again\n" +
                        SET_TEXT_COLOR_GREEN + "Leave " + SET_TEXT_COLOR_WHITE
                        + "- Leave the game, go back to previous menu.\n" +
                        SET_TEXT_COLOR_GREEN + "Make Move <startPos.> <endPos.>" + SET_TEXT_COLOR_WHITE +
                        "- Make a valid move on your turn\n" +
                        SET_TEXT_COLOR_GREEN + "Resign " + SET_TEXT_COLOR_WHITE +
                        "- give up and forfeit the game.\n" +
                        SET_TEXT_COLOR_GREEN + "Highlight Legal Moves <piecePos.>" + SET_TEXT_COLOR_WHITE +
                        "- show what move a piece can make\n";


    public String redraw() {
        if (Objects.equals(playerColor, "white")) {
            return PostLoginUI.drawBoardWhite(board);
        }
        else if (Objects.equals(playerColor, "black")){
            return PostLoginUI.drawBoardBlack(board);
        }
        else {
            return PostLoginUI.drawBoardWhite(board);
        }
    }

    public String leave() throws IOException {
        UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        client.send(leave);
        return ("You have left the game.");
    }

    public String move(String startPos, String endPos) {
        ChessPosition position1 =  new ChessPosition();
        ChessPosition position2 = new ChessPosition();
        //TODO: Find some way to prompt the user for promotion IF it's in promotion territory
        ChessMove move = new ChessMove(position1, position2, null);

        return("Move made.");
    }

    public String resign() throws IOException {
        UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        client.send(resign);
        return ("You have resigned.");
    }

    public void legals(String pos) {

    }


}
