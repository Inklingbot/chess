package model;
import java.util.UUID;


public record AuthData(String authToken, String username) {

    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }



    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
