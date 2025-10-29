package server.handlers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import model.LoginRequest;
import model.LoginResult;
import service.UserService;

public class LoginHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    public LoginHandler(UserService userService) {this.userService = userService;}

    @Override
    public void handle(@NotNull Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = userService.login(request);
            String jsonString = gson.toJson(result);
            ctx.result(jsonString);
            ctx.status(200);
        } catch (DataAccessException e) {
            String errorJson = createJsonError("Error: Data not stored");
            ctx.result(errorJson);
            ctx.status(500);
        }
        catch (BadRequestResponse b) {
            String errorJson = createJsonError("Error: bad request");
            ctx.result(errorJson);
            ctx.status(400);
        }
        catch (UnauthorizedResponse u) {
            String errorJson = createJsonError("Error: unauthorized");
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
