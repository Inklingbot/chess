package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import model.ListGamesRequest;
import model.ListGamesResult;

public class ListGamesHandler implements Handler {
    private final Gson gson = new Gson();
    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }
    GameService gameService;
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        //Try to get the AuthToken in the ListGamesRequest object
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isBlank()) {
                throw new UnauthorizedResponse();
            }
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = gameService.listGames(request);
            String jsonString = gson.toJson(result);
            ctx.result(jsonString);
            ctx.status(200);
        }
        catch (UnauthorizedResponse u) {
            String errorJson = LogOutHandler.createJsonError(gson, "Error: You're unauthorized for this action!\n");
            ctx.result(errorJson);
            ctx.status(401);
        }
        catch(DataAccessException e) {
            String errorJson = LogOutHandler.createJsonError(gson, "Error: THis data doesn't exist," +
                    " or we couldn't access it!\n");
            ctx.result(errorJson);
            ctx.status(500);
        }
    }

    }

