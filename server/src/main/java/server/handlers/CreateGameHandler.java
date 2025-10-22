package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import service.*;

public class CreateGameHandler implements Handler {
    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    private final Gson gson = new Gson();
    GameService gameService;
    @Override
    public void handle(@NotNull Context ctx) {
        try {String authToken = ctx.header("authorization");
            String gameName = ctx.body();
            service.CreateGameNameRequest requestName = gson.fromJson(gameName, CreateGameNameRequest.class);
            CreateGameRequest request = new CreateGameRequest(authToken, requestName.gameName());
            CreateGameResult result = gameService.createGame(request);
            String jsonString = gson.toJson(result);
            ctx.result(jsonString);
            ctx.status(200);
        }
        catch (BadRequestResponse b) {
            String errorJson = createJsonError("Error: bad request");
            ctx.result(errorJson);
            ctx.status(400);
        }
        catch(UnauthorizedResponse u) {
            String errorJson = createJsonError("Error: unauthorized");
            ctx.result(errorJson);
            ctx.status(401);
        }
        catch(DataAccessException e) {
            String errorJson = createJsonError("Error: Data not stored");
            ctx.result(errorJson);
            //If the data don't be accessin' set it to 500
            ctx.status(500);
        }
    }
    public String createJsonError(String error) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", error);
        return gson.toJson(jsonObject);
    }
}
