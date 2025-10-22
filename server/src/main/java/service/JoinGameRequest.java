package service;
import dataAccess.AuthDAO;
public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {

}
