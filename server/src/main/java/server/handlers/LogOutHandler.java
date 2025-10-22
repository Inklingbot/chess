package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import service.LogoutRequest;
import service.UserService;

public class LogOutHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;

    public LogOutHandler(UserService userService) {
        this.userService = userService;
    }
    @Override
    public void handle(@NotNull Context ctx) {
        //Create a logoutRequest object
        try {
            String authToken = ctx.header("Authorization");
            if (authToken == null || authToken.isBlank()) {
                throw new UnauthorizedResponse();
            }
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse u) {
            String errorJson = createJsonError("Error: unauthorized");
            ctx.result(errorJson);
            ctx.status(401);
        }
        catch(DataAccessException e) {
            String errorJson = createJsonError("Error: Data not stored");
            ctx.result(errorJson);
            ctx.status(500);
        }
    }

    public String createJsonError(String error) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", error);
        return gson.toJson(jsonObject);
    }
}
