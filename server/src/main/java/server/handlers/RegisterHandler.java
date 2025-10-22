package server.handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.RegisterRequest;
import service.RegisterResult;
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
            try {
                RegisterResult result = userService.register(request);
                String jsonString = gson.toJson(result);
                ctx.result(jsonString);
                ctx.status(200);
            }
            catch(DataAccessException e) {
                //If the name is already taken set it to 403
                ctx.status(403);
            }

        } catch (BadRequestResponse b) {
            //If a bad request set it to 400
            ctx.status(400);
        }

        //WHAT IS 500

    }
}