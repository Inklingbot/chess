package model;

import chess.ChessGame;

public record GameData(int gameID, String WhiteUsername, String blackUsername, String gameName, ChessGame chessGame) {

    public GameData(int gameID, String WhiteUsername, String blackUsername, String gameName, ChessGame chessGame) {
        this.gameID = gameID;
        this.WhiteUsername = WhiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.chessGame = chessGame;
    }

    @Override
    public String gameName() {
        return gameName;
    }

}
