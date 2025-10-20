package server;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import io.javalin.*;
import passoff.exception.ResponseParseException;
import io.javalin.http.Context;
import service.GameService;


public class Server {

    private final Javalin javalin;
    private GameService gameService = new GameService();
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public Server(GameService gameService, AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clearGame)
                .post("/user", this::register)

        ;
        // Register your endpoints and exception handlers here.

//        ClearGameHandler clearGame = new ClearGameHandler();

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void clearGame(Context ctx) throws ResponseParseException {
        gameService.clearGame();
        ctx.status(200);
    }

    private void register(Context ctx) throws ResponseParseException {
        //if successful, set ctx status to 200
        //Give the info of the auth token and the username

        //Failure methods:
        //If a random error, ctx status is 400, return the message and then Error: bad request

        //If name already taken, ctx status 403, and say already taken

        //If random error, set ctx to 500, and give a description of error
    }


    public void stop() {
        javalin.stop();
    }

    public void clearGameRequest() {

    }
}
