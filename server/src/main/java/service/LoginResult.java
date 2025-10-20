package service;

public record LoginResult(String username, String authToken) {

    //If success, returns a JSON object of the format written in the documentation

    //If error it returns a json object of the format writting in the documentation
}
