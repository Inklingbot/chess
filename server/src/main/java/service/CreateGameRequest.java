package service;

import chess.ChessGame;

public record CreateGameRequest(String authToken, String gameName) {

    String getAuthToken(String authToken) {
        //find the authToken using find if it's in a map, otherwise SQL it
        //if it matches the passed in
        //return
        return null;
    }

    void createGame(String gameName) {
        //Create a new ChessGame object with identifier gameName, and store it in the GameData
        //this might need to go in the CreateGameResult Class?

    }
}
