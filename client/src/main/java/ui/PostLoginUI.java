package ui;
import model.CreateGameResult;
import model.ListGamesResult;
import model.LogoutRequest;
import server.ResponseException;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginUI {
    private ServerFacade facade;
    public PostLoginUI(ServerFacade facade) {
        this.facade = facade;
    }


    public void run() {

        System.out.println(help);

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
                case "create" -> create(params[1]);
                case  "list" -> list();
                case "join" -> join();
                case "observe" -> observe(params[1]);
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

        CreateGameResult result = facade.create(name);


        return result.toString();
    }

    String list() {
        //call the appropriate class
        return null;
    }

    public String join() {
        //somehow join the game and then move to the next UI I'm assuming
        //so call GameplayUI.run()?
        return null;
    }

    public String observe(String name) {
        //don't do anything??? Maybe find the game?
        return null;
    }

    public String logout() {
        return null;
    }

    public String quit() {
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

}
