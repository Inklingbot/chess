package server;

import Services.ClearGameService;
import io.javalin.*;
import passoff.exception.ResponseParseException;
import io.javalin.http.Context;


public class Server {

    private final Javalin javalin;
    private ClearGameService clearGameService;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clearGame)
        ;
        // Register your endpoints and exception handlers here.

//        ClearGameHandler clearGame = new ClearGameHandler();

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void clearGame(Context ctx) throws ResponseParseException {
        getClearGameService().ClearGameRequest();
        ctx.status(200);
    }


    public void stop() {
        javalin.stop();
    }

    ClearGameService getClearGameService() {
        return clearGameService;
    }
}
