package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int id = 0;
    private final HashMap<Integer, GameData> games = new HashMap<>();
    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public GameData createGame(String gameName) {
        GameData game = new GameData(id++, null, null, gameName, new ChessGame());
        games.put(id, game);
        return game;
    }
//How can I access certain members of my object in the hashmap?
    @Override
    public String listGames() {
//        return games.values.getGameName();
        return null;
    }

    @Override
    public void updateGame() {

    }



    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }
}
