package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

public class CreateGameHandler implements Handler {
    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    private final Gson gson = new Gson();
    GameService gameService;
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        //Create Request object from Json String

        //Create Result Object from calling Service class to Create the Game

        //Turn the result into a json string

        //add it to the result part of a context object

        //If all goes well the status will be 200

        //if they are unauthorized set the status to 401

        //WHAT IS 500
    }
}
