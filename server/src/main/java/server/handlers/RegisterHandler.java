package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;


public class RegisterHandler implements Handler {
    private Gson gson = new Gson();
    UserService userService;
    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
//        Json newInfo = toJson(ctx);
        /*RegisterRequest(newInfo);*/
        //if successful, set ctx status to 200
        //Give the info of the auth token and the username
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        String jsonString = gson.toJson(result);
        ctx.result(jsonString);
        ctx.status(200);


        //Failure methods:
        //If a random error, ctx status is 400, return the message and then Error: bad request

        //If name already taken, ctx status 403, and say already taken

        //If random error, set ctx to 500, and give a description of error

        //ctx.json(some string that needs to be returned);
    }
}
