package ui;
import model.LoginResult;
import model.RegisterResult;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    ServerFacade facade = new ServerFacade("http://localhost:8080");

    public void run() {

        System.out.println(LOGO + " Welcome to 240 chess. Type Help to get started.");

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.equals("quit")) {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                String[] msgs = msg.split(":");
                System.out.print(msgs[1] + "\n");
            }
        }

        System.out.println();
    }

    public String eval(String input) throws ResponseException {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (Objects.equals(cmd, "register")) {
            if (params.length < 3) {
                throw new ResponseException(ResponseException.Code.ClientError,
                        "You missed a field somewhere, check your syntax!\n");
            }
        }
        if (Objects.equals(cmd, "login")) {
            if (params.length < 2) {
                throw new ResponseException(ResponseException.Code.ClientError,
                        "You missed a field somewhere, check your syntax!\n");
            }
        }
            return switch (cmd) {
                case "register" -> register(params[0], params[1], params[2]);
                case "quit" -> quit();
                case "login" -> login(params[0], params[1]);
                default -> "";
            };

    }

    public String register(String username, String pass, String email) throws ResponseException {
        RegisterResult result = facade.register(username, pass, email);
        //Store the username and authToken?
        PostLoginUI ui = new PostLoginUI(facade, result.authToken());
        System.out.println("Successfully Registered!");
        ui.run();
        //call the appropriate class for this?

        return null;
    }

    public String login(String username, String pass) throws ResponseException {
        LoginResult result = facade.login(username, pass);

        PostLoginUI ui = new PostLoginUI(facade, result.authToken());
        ui.run();
        return SET_TEXT_COLOR_YELLOW + "Thank you.\n";
    }

    public String quit() {
        System.out.println("Thanks for... wait you didn't even play? Do you not like my game bro?");
        System.exit(0);
        //Should do this on its own when the loop ends
        return "quit";
    }

    public static final String HELP =
            SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>"
            + SET_TEXT_COLOR_WHITE + " - to create an account \n"
            + SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> "
            + SET_TEXT_COLOR_WHITE + " - to play chess \n"
            + SET_TEXT_COLOR_BLUE + "help "
            + SET_TEXT_COLOR_WHITE + " - see this screen again \n"
            + SET_TEXT_COLOR_BLUE + "quit\n";

    private void printPrompt() {
        System.out.print(HELP);
    }

}
