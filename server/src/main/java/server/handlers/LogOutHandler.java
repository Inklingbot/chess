package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LogOutHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    @Override
    public void handle(@NotNull Context context) throws Exception {
        //Create a logoutRequest object

        //Call the service class

        //Turn the Result object that came back into a jsonString

        //if all that worked, Status is 200 hooray

        //If the person has no authorization set it to 401

        //WHAT IS 500
    }
}
