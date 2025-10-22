package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private int id = 0;
    private final HashMap<Integer, GameData> games = new HashMap<>();
    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public GameData createGame(String gameName) {
        GameData game = new GameData(++id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return game;
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> collectionOfGames = games.values();


        return collectionOfGames;
    }

    public void updateGameUserJoin(String color, String username, int gameID, GameData data) {

        if (Objects.equals(color, "WHITE")) {
            GameData newData = new GameData(gameID, username, data.blackUsername(), data.gameName(), data.chessGame());
            games.put(gameID, newData);
        }
        else if(Objects.equals(color, "BLACK")) {
            GameData newData = new GameData(gameID, data.whiteUsername(), username, data.gameName(), data.chessGame());
            games.put(gameID, newData);
        }
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
}
