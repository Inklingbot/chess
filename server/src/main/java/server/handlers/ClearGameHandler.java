package server.handlers;

import dataAccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ClearGameHandler implements Handler {
    public GameService gameService;
    public ClearGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {gameService.clearGame();
            ctx.status(200);}
        catch (DataAccessException exception) {
            ctx.status(500);
        }


    }
}
