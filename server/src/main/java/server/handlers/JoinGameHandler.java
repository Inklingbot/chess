package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.DuplicateNameException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.JoinGameRequest;

public class JoinGameHandler implements Handler {
    private final Gson gson = new Gson();
    GameService gameService;
    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public void handle(@NotNull Context ctx) {
        //Create request object from json
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameRequest finishedRequest = new JoinGameRequest(authToken, request.playerColor(), request.gameID());
            gameService.joinGame(finishedRequest);
            ctx.status(200);

        }
        catch(DataAccessException d) {
            String errorJson = createJsonError("Error: This data doesn't exist, or we couldn't access it!\n");
            ctx.result(errorJson);
            ctx.status(500);

        }
        catch(DuplicateNameException e) {
            String errorJson = createJsonError("Error: Color already taken!\n");
            ctx.result(errorJson);
            ctx.status(403);
        }
        catch(BadRequestResponse b) {
            String errorJson = createJsonError("Error: You missed a field somewhere, check your syntax!\n");
            ctx.result(errorJson);
            ctx.status(400);
        }
        catch(UnauthorizedResponse u) {
            String errorJson = createJsonError("Error: You're unauthorized for this action!\n");
            ctx.result(errorJson);
            ctx.status(401);
        }




    }
    public String createJsonError(String error) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", error);
        return gson.toJson(jsonObject);
    }
}
