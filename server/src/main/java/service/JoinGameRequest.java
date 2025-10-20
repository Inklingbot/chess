package service;

public record JoinGameRequest(String authToken) {

    void joinGame () {

    }

    boolean getAuthToken(String authToken) {
        //check SQL or other database and then match this authtoken to other one
    }
}
