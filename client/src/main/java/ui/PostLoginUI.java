package ui;
import model.CreateGameResult;
import model.ListGamesResult;
import model.LogoutRequest;
import server.ResponseException;
import server.ServerFacade;
import ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    private ServerFacade facade;
    private String authToken;
    public PostLoginUI(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }


    public void run() {

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params[0]);
                case  "list" -> list();
                case "join" -> join(params[0], params[1]);
                case "observe" -> observe(params[0]);
                case "logout" -> logout();
                case "quit" -> quit();
                case "help" -> "";
                default -> "";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }

    }

    String create(String name) throws ResponseException {
        CreateGameResult result = facade.create(name, authToken);
        return result.toString();
    }

    String list() throws ResponseException {
        //call the appropriate class
        //find authToken
        ListGamesResult result = facade.list(authToken);

        return result.toString();
    }

    public String join(String playerColor, String gameID) throws ResponseException {
        facade.join(playerColor, gameID, authToken);

        //display the board (starting state)
        return boardInitial;
    }

    public String observe(String gameName) {

        //don't do anything??? Maybe find the game?
        return boardInitial;
    }

    public String logout() throws ResponseException {
        facade.logout(authToken);
        return null;
    }

    public String quit() {
        System.out.println("Thanks for playing!");
        return null;
        //somehow stop the program
    }

    private void printPrompt() {
        System.out.print(help);
    }

    public static final String help = """
         [38;5;12m create <NAME> [38;5;0m - create a game
         [38;5;12m list [38;5;0m - list all games
         [38;5;12m join <ID> {WHITE | BLACK} [38;5;0m - Join a game on specified team
         [38;5;12m observe <ID> [38;5;0m - spectate a game
         [38;5;12m logout [38;5;0m - return to the logged out menu
         [38;5;12m quit [38;5;0m - quit the program altogether
         [38;5;12m help [38;5;0m - display this screen
         """;

    public static final String boardInitial = SET_BG_COLOR_DARK_GREY + EMPTY + "abcdefgh" + EMPTY + "\n" + "8" + SET_BG_COLOR_WHITE
            + BLACK_ROOK + SET_BG_COLOR_BLACK + BLACK_KNIGHT + SET_BG_COLOR_WHITE + BLACK_BISHOP + SET_BG_COLOR_BLACK
            + BLACK_QUEEN + SET_BG_COLOR_WHITE + BLACK_KING + SET_BG_COLOR_BLACK + BLACK_BISHOP + SET_BG_COLOR_WHITE
            + BLACK_KNIGHT + SET_BG_COLOR_BLACK + BLACK_ROOK + SET_BG_COLOR_DARK_GREY + "8\n7" + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE
            + BLACK_PAWN + SET_BG_COLOR_BLACK + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_BLACK
            + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_DARK_GREY + "7\n6" + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + "6\n5" + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + "5\n4" + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + "4\n3" + SET_BG_COLOR_WHITE
            + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY
            + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK + EMPTY + SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_BLACK
            + EMPTY + SET_BG_COLOR_DARK_GREY + "3\n2" + SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK
            + WHITE_PAWN + SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE
            + WHITE_PAWN + SET_BG_COLOR_BLACK + WHITE_PAWN +SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_BLACK
            + WHITE_PAWN + SET_BG_COLOR_DARK_GREY + "2\n1" + SET_BG_COLOR_BLACK + WHITE_ROOK + SET_BG_COLOR_WHITE
            + WHITE_KNIGHT + SET_BG_COLOR_BLACK + WHITE_BISHOP + SET_BG_COLOR_WHITE + WHITE_QUEEN + SET_BG_COLOR_BLACK
            + WHITE_KING + SET_BG_COLOR_WHITE + WHITE_BISHOP + SET_BG_COLOR_BLACK + WHITE_KNIGHT + SET_BG_COLOR_WHITE
            + WHITE_ROOK + SET_BG_COLOR_DARK_GREY + "1\n" + EMPTY + "abcdefgh" + EMPTY;


}
