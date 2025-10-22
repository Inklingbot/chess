package server;

import dataAccess.*;
import io.javalin.*;
import server.handlers.*;
import service.GameService;
import service.UserService;

import static io.javalin.apibuilder.ApiBuilder.before;


public class Server {

    private final Javalin javalin;
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private GameService gameService = new GameService(authDAO, gameDAO, userDAO);
    private UserService userService = new UserService(authDAO, userDAO);


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //Deletes all games
        javalin.delete("/db", context -> new ClearGameHandler(gameService).handle(context));
        //Registers User
        javalin.post("/user", context -> new RegisterHandler(userService).handle(context));
        //Login for a User
        javalin.post("/session", context -> new LoginHandler(userService).handle(context));
        //Logout for a user
        javalin.delete("/session", context -> new LogOutHandler(userService).handle(context));
        //List Games
        javalin.get("/game", context -> new ListGamesHandler(gameService).handle(context));
        //Create a Game
        javalin.post("/game", context -> new CreateGameHandler(gameService).handle(context));
        //Join a Game
        javalin.put("/game", context -> new JoinGameHandler(gameService).handle(context));



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
