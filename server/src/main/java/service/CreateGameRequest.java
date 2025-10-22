package service;

import chess.ChessGame;

public record CreateGameRequest(String authToken, String gameName) {

}
