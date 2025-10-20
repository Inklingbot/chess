package model;

import chess.ChessGame;

public record GameData(int gameID, String WhiteUsername, String blackUsername, String gameName, ChessGame ChessGame) {

    public GameData(int gameID, String WhiteUsername, String blackUsername, String gameName, ChessGame ChessGame) {
        this.gameID = gameID;
        this.WhiteUsername = WhiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.ChessGame = ChessGame;
    }

    @Override
    public String gameName() {
        return gameName;
    }
}
