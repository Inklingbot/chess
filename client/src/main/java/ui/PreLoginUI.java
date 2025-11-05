package ui;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;


import static ui.EscapeSequences.LOGO;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginUI {

    public void run() {
        System.out.println(LOGO + " Welcome to 240 chess. Type Help to get started.");
        System.out.print(help());

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
                case "register" -> register(params);
                case "quit" -> quit();
                case "help" -> run();
                case "login" -> login(params);
                default -> run();
            };
        } catch (ResponseException ex) {
                return ex.getMessage();
            }

    }

    public static final String help = """
            [38;5;12m register <USERNAME> <PASSWORD> <EMAIL> [38;5;0m - to create an account
            [38;5;12m login <USERNAME> < PASSWORD> [38;5;0m - to play chess
            [38;5;12m quit [38;5;0m - playing chess
            [38;5;12m help [38;5;0m - see this screen again
            """;

    private void printPrompt() {
        System.out.print(help);
    }


    public String help() {
        return null;
    }
}
