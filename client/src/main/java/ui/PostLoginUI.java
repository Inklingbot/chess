package ui;
import model.CreateGameResult;
import model.ListGamesResult;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    private final ServerFacade facade;
    private final String authToken;
    public PostLoginUI(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }


    public void run() {

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {
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

            } catch (Throwable e) {
                var msg = e.toString();
                String[] msgs = msg.split(":");
                for (int i = 1; i < msgs.length; i++)  {
                    System.out.println(msgs[i]);
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
                    throw new ResponseException(ResponseException.Code.ClientError,
                            "You missed a field somewhere, check your syntax!\n");
                }
            }
            else if (Objects.equals(cmd, "join")) {
                if (params.length < 2) {
                    throw new ResponseException(ResponseException.Code.ClientError,
                            "You missed a field somewhere, check your syntax!\n");
                }
            }
            return switch (cmd) {
                case "create" -> create(params[0]);
                case  "list" -> list();
                case "join" -> join(params[0], params[1]);
                case "observe" -> observe(params[0]);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> "";
            };


    }

    String create(String name) throws ResponseException {
        CreateGameResult result = facade.create(name, authToken);
        return result.toString();
    }

    String list() throws ResponseException {
        ListGamesResult result = facade.list(authToken);
        return result.toString();
    }

    public String join(String gameID, String playerColor) throws ResponseException {
        facade.join(playerColor, gameID, authToken);

        //display the board (starting state)
        if (Objects.equals(playerColor, "white")) {
            return BOARD_INITIAL;
        }
        else {
            return BOARD_INITIAL_2;
        }

    }

    public String observe(String gameName) {

        //don't do anything??? Maybe find the game?
        return BOARD_INITIAL;
    }

    public String logout() throws ResponseException {
        facade.logout(authToken);
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

    public static final String BOARD_INITIAL = SET_BG_COLOR_DARK_GREY + "  a   b  c   d   e   f  g  h" + EMPTY +
            "\n" + "8" + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_RED
            + BLACK_ROOK + SET_BG_COLOR_BLACK + BLACK_KNIGHT + SET_BG_COLOR_WHITE + BLACK_BISHOP + SET_BG_COLOR_BLACK
            + BLACK_QUEEN + SET_BG_COLOR_WHITE + BLACK_KING + SET_BG_COLOR_BLACK + BLACK_BISHOP + SET_BG_COLOR_WHITE
            + BLACK_KNIGHT + SET_BG_COLOR_BLACK + BLACK_ROOK + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "8\n7"
            + SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE
            + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "7\n6"
            + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_RED
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "6\n5" + SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY
            + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "5\n4" + SET_TEXT_COLOR_RED + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "4\n3" + SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY
            + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "3\n2" + SET_BG_COLOR_WHITE + WHITE_PAWN
            + SET_BG_COLOR_BLACK
            + WHITE_PAWN + SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE
            + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK
            + WHITE_PAWN + SET_BG_COLOR_DARK_GREY + "2\n1" + SET_BG_COLOR_BLACK + WHITE_ROOK + SET_BG_COLOR_WHITE
            + WHITE_KNIGHT + SET_BG_COLOR_BLACK + WHITE_BISHOP + SET_BG_COLOR_WHITE + WHITE_QUEEN + SET_BG_COLOR_BLACK
            + WHITE_KING + SET_BG_COLOR_WHITE + WHITE_BISHOP + SET_BG_COLOR_BLACK + WHITE_KNIGHT + SET_BG_COLOR_WHITE
            + WHITE_ROOK + SET_BG_COLOR_DARK_GREY + "1\n" + "  a   b  c   d   e  f   g  h" + EMPTY + "\n";


    public static final String BOARD_INITIAL_2 = SET_BG_COLOR_DARK_GREY + "  a   b  c   d   e   f  g  h" + EMPTY +
            "\n" + "8" + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLUE
            + BLACK_ROOK + SET_BG_COLOR_BLACK + BLACK_KNIGHT + SET_BG_COLOR_WHITE + BLACK_BISHOP + SET_BG_COLOR_BLACK
            + BLACK_KING + SET_BG_COLOR_WHITE + BLACK_QUEEN + SET_BG_COLOR_BLACK + BLACK_BISHOP + SET_BG_COLOR_WHITE
            + BLACK_KNIGHT + SET_BG_COLOR_BLACK + BLACK_ROOK + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "8\n7"
            + SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE
            + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "7\n6"
            + SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLUE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "6\n5" + SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY
            + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "5\n4" + SET_TEXT_COLOR_BLUE + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "4\n3" + SET_TEXT_COLOR_RED + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY
            + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "3\n2" + SET_BG_COLOR_WHITE + WHITE_PAWN
            + SET_BG_COLOR_BLACK + SET_TEXT_COLOR_RED
            + WHITE_PAWN + SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE
            + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK
            + WHITE_PAWN + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "2\n1" + SET_TEXT_COLOR_RED
            + SET_BG_COLOR_BLACK + WHITE_ROOK + SET_BG_COLOR_WHITE
            + WHITE_KNIGHT + SET_BG_COLOR_BLACK + WHITE_BISHOP + SET_BG_COLOR_WHITE + WHITE_KING + SET_BG_COLOR_BLACK
            + WHITE_QUEEN + SET_BG_COLOR_WHITE + WHITE_BISHOP + SET_BG_COLOR_BLACK + WHITE_KNIGHT + SET_BG_COLOR_WHITE
            + WHITE_ROOK + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE
            +  "1\n" + "  a   b  c   d   e  f   g  h" + EMPTY + "\n";



}
