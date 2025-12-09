package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.CreateGameResult;
import model.GameData;
import model.ListGamesResult;
import server.ResponseException;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

//error catching in
//don't use user input as gameID for join, use a hashmap

public class PostLoginUI {
    public HashMap<Integer, Integer> ids = new HashMap<>();
    private final ServerFacade facade;
    private final String authToken;
    public PostLoginUI(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }


    public void run() {

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (true) {
            assert result != null;
            if (result.equals("quit")) {break;}
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
            if (Objects.equals(cmd, "create") || Objects.equals(cmd, "observe")) {
                if (params.length < 1) {
                    throw new ResponseException(
                            "You missed a field somewhere, check your syntax!\n");
                }
                if (params.length > 1) {
                    throw new ResponseException(
                            "Too many fields given!\n");
                }
            }
            else if (Objects.equals(cmd, "join")) {
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
                case "create" -> create(params[0]);
                case  "list" -> list();
                case "join" -> join(params[0], params[1]);
                case "observe" -> observe(params[0]);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> "Invalid Command.";
            };


    }

    String create(String name) throws ResponseException {
        CreateGameResult result = facade.create(name, authToken);
        System.out.println("Successfully created the game!\n");
        return list();
    }

    String list() throws ResponseException {
        ListGamesResult result = facade.list(authToken);
        int publicId = 1;
        StringBuilder builda = new StringBuilder();
        builda.append("List of Games: \n");
        for (GameData game : result.games()) {
            ids.put(publicId, game.gameID());
            builda.append(publicId).append(" Game Name: ").append(game.gameName()).append(", White: ")
                    .append(game.whiteUsername()).append(", Black: ").append(game.blackUsername()).append("\n");
            publicId++;
        }
        return builda.toString();
    }

    public String join(String gameID, String playerColor) throws ResponseException {
        int gameIDInt = Integer.parseInt(gameID);

        if (!(gameIDInt > ids.size())) {
            String gameIDDb = ids.get(gameIDInt).toString();
            facade.join(playerColor, gameIDDb, authToken);
            GameplayUI ui =  new GameplayUI(authToken, gameIDInt, playerColor, facade);
            ui.run();
        }
        else {
            throw new ResponseException("This is not a valid gameID!");
        }

        return "Thank you for playing!\n";
    }

    public String observe(String gameID) throws ResponseException {
        int gameIDInt = Integer.parseInt(gameID);
        if (!(gameIDInt > ids.size())) {
            String gameIDDb = ids.get(gameIDInt).toString();
            GameplayUI ui = new GameplayUI(authToken, gameIDInt, null, facade);
            ui.run();
        }
        else {
            throw new ResponseException("This is not a valid gameID.\n");
        }

        return "Thank you for observing.\n";
    }

    public String logout() throws ResponseException {
        facade.logout(authToken);
        System.out.println("You're logged out!\n");
        return null;
    }

    public String quit() {
        System.out.println("Thanks for playing!");
        System.exit(0);
        return null;
        //somehow stop the program
    }

    private void printPrompt() {
        System.out.print(HELP);
    }

    public static final String HELP =
         SET_TEXT_COLOR_MAGENTA + "create <NAME> " + SET_TEXT_COLOR_WHITE + "- create a game\n" +
                 SET_TEXT_COLOR_MAGENTA + "list " + SET_TEXT_COLOR_WHITE + "- list all games\n" +
                 SET_TEXT_COLOR_MAGENTA + "join <ID> {WHITE | BLACK} " + SET_TEXT_COLOR_WHITE
                 + "- join a game on a specified team\n" +
                 SET_TEXT_COLOR_MAGENTA + "observe <ID> " + SET_TEXT_COLOR_WHITE + "- spectate a game\n" +
                 SET_TEXT_COLOR_MAGENTA + "logout " + SET_TEXT_COLOR_WHITE + "- return to logged out menu\n" +
                 SET_TEXT_COLOR_MAGENTA + "quit " + SET_TEXT_COLOR_WHITE + "- quit the program altogether\n" +
                 SET_TEXT_COLOR_MAGENTA + "help " + SET_TEXT_COLOR_WHITE + "- display this screen\n";

    public static String drawBoard(ChessBoard board, Collection<ChessMove> moves, String playerColor) {
        int start = 0;
        int end = 0;
        boolean increment = false;
        if (Objects.equals(playerColor, "black")) {
            start = 1;
            end = 8;
            increment = true;
        }
        else if (Objects.equals(playerColor, "white") || playerColor == null) {
            start = 8;
            end = 1;
        }


        StringBuilder s = new StringBuilder();
        s.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "  " +"a" + "  " + "b" + "  " + "c" + "  " + "d"
                + "  " + "e" + "  " + "f" + "  " + "g" + "  " + "h"  + EMPTY +
                "\n");
        if (increment) {
            for (int j = start;j <= end; j++) {
                s.append(j);
                for (int i = 1; i <=8; i++) {
                    s.append(pieceCreator(board, i, j, moves));
                }
                s.append(SET_BG_COLOR_DARK_GREY).append(SET_TEXT_COLOR_WHITE).append(j).append("\n");
            }
        }
        else {
            for (int j = start; j >= end; j--) {
                s.append(j);
                for (int i = 1; i <=8; i++) {
                    s.append(pieceCreator(board, i, j, moves));
                }
                s.append(SET_BG_COLOR_DARK_GREY).append(SET_TEXT_COLOR_WHITE).append(j).append("\n");
            }
        }
        s.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "  " +"a" + "  " + "b" + "  " + "c" + "  " + "d"
                + "  " + "e" + "  " + "f" + "  " + "g" + "  " + "h"  + EMPTY +
                "\n");
        return s.toString();
    }

    public static String pieceCreator(ChessBoard board, int i, int j, Collection<ChessMove> moves) {
        String s = "";
        boolean highlight = false;
        ChessPosition position = new ChessPosition(j, i);
        if (moves != null) {
            for (ChessMove move : moves) {
                ChessPosition positionMove = move.getEndPosition();
                if (Objects.equals(positionMove,position)) {
                    highlight = true;
                    break;
                }
            }
        }


        //What space is it
        if (highlight) {
            s+= SET_BG_COLOR_YELLOW;
        }
        else if (j % 2 == 0) {
            if (i % 2 == 0) {
                s+= SET_BG_COLOR_BLACK;
            }
            else {
                s+= SET_BG_COLOR_WHITE;
            }
        }
        else {
            if (i % 2 == 0) {
                s+=SET_BG_COLOR_WHITE;
            }
            else {
                s+=SET_BG_COLOR_BLACK;
            }
        }
        if (board.getPiece(new ChessPosition(j, i)) != null) {
            //If the piece is white or black
            if (board.getPiece(new ChessPosition(j, i)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                s+=SET_TEXT_COLOR_RED;
            }
            else {
                s+=SET_TEXT_COLOR_BLUE;
            }
            switch(board.getPiece(new ChessPosition(j, i)).getPieceType()) {
                case KNIGHT:
                    s+=BLACK_KNIGHT;
                    break;
                case BISHOP:
                    s+=BLACK_BISHOP;
                    break;
                case KING:
                    s+=BLACK_KING;
                    break;
                case QUEEN:
                    s+=BLACK_QUEEN;
                    break;
                case PAWN:
                    s+=BLACK_PAWN;
                    break;
                case ROOK:
                    s+=BLACK_ROOK;
                    break;
            }
        }
        else {
            s+=EMPTY;
        }


        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostLoginUI that = (PostLoginUI) o;
        return Objects.equals(ids, that.ids) && Objects.equals(facade, that.facade) && Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids, facade, authToken);
    }
}
