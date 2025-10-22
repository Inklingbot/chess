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
import service.JoinGameRequest;

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
            String errorJson = createJsonError("Error: Data not stored");
            ctx.result(errorJson);
            ctx.status(500);

        }
        catch(DuplicateNameException e) {
            String errorJson = createJsonError("Error: already taken");
            ctx.result(errorJson);
            ctx.status(403);
        }
        catch(BadRequestResponse b) {
            String errorJson = createJsonError("Error: bad request");
            ctx.result(errorJson);
            ctx.status(400);
        }
        catch(UnauthorizedResponse u) {
            String errorJson = createJsonError("Error: unauthorized");
            ctx.result(errorJson);
            ctx.status(401);
        }



        //Do the logic for JoinGame in the Service class

        //if all goes well set Status to 200

        //If they have no authToken for it then set status to 401

        //WHAT IS 500
    }
    public String createJsonError(String error) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", error);
        return gson.toJson(jsonObject);
    }
}
