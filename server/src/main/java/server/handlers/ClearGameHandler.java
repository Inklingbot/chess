package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ClearGameHandler implements Handler {
    Gson gson = new Gson();
    public GameService gameService;
    public ClearGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public void handle(@NotNull Context ctx) {
        try {gameService.clearGame();
            ctx.status(200);}
        catch (DataAccessException exception) {
            ctx.result(createJsonError("Error: This data doesn't exist!"));
            ctx.status(500);
        }




    }
    public String createJsonError(String error) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", error);
        return gson.toJson(jsonObject);
    }
}
