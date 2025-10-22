package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import dataAccess.DuplicateNameException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.JoinGameRequest;
import service.RegisterRequest;
import service.UserService;

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
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(request);

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
