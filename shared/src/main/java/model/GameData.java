package model;

import chess.ChessGame;

public record GameData(int gameID, String WhiteUsername, String blackUsername, String gameName, ChessGame chessGame) {

}
