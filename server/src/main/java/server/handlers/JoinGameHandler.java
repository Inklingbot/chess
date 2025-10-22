package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class JoinGameHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    @Override
    public void handle(@NotNull Context context) {
        //Create request object from json

        //Do the logic for JoinGame in the Service class

        //if all goes well set Status to 200

        //If they have no authToken for it then set status to 401

        //WHAT IS 500
    }
}
