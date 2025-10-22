package server.handlers;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LoginHandler implements Handler {
    private final Gson gson = new Gson();
    UserService userService;
    public LoginHandler(UserService userService) {this.userService = userService;}

    @Override
    public void handle(@NotNull Context ctx) {
        //Create a loginRequest Object

        //Call the userService class

        //Turn the loginResult into a json string

        //if it worked set status to 200

        //If the username or password doesn't exist set status to 401

        //If it's a  bad response set status to 400

        //Otherwise, set status to 500????
    }





}
