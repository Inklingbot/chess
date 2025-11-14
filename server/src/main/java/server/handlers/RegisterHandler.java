package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.DuplicateNameException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;


public class RegisterHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
    @Override
    public void handle(@NotNull Context ctx) {

        try{
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
                RegisterResult result = userService.register(request);
                String jsonString = gson.toJson(result);
                ctx.result(jsonString);
                ctx.status(200);

        } catch (BadRequestResponse b) {
            String errorJson = createJsonError("Error: bad request\n");
            ctx.result(errorJson);
            ctx.status(400);
        }
        catch(DataAccessException e) {
            String errorJson = createJsonError("Error: This data doesn't exist, or we couldn't access it!\n");
            ctx.result(errorJson);
            //If the data don't be accessin' set it to 500
            ctx.status(500);
        }
        catch(DuplicateNameException d) {
            String errorJson = createJsonError("Error: Your username is already taken!\n");
            ctx.result(errorJson);
            //If the name is taken set it to 403
            ctx.status(403);
        }
        catch(ArrayIndexOutOfBoundsException a) {
            String errorJson = createJsonError("Error: You missed a field somewhere, check your syntax!\n");
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