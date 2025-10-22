package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class ListGamesHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    @Override
    public void handle(@NotNull Context context) throws Exception {
        //Try to get the AuthToken in the ListGamesRequest object

        //Create a result class after doing logic in the Service Class

        //Turn Result Class into jsonString

        //Throw that in the result context

        //If all goes well set status to 200

        //If no authToken then set status to 401

        //WHAT IS 500
    }
}
