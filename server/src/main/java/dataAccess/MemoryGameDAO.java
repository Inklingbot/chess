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

    @Override
    public String listGames() {
        StringBuilder listOfGames = new StringBuilder();
        GameData gameCheck = games.get(1);
        if (gameCheck == null) {
            return listOfGames.toString();
        }
        for (int i = 1; i < id; i++) {
            GameData game = games.get(id);
            listOfGames.append(game.gameName()).append("\n");
        }

        return listOfGames.toString();
    }

    @Override
    public void updateGame(int gameID, GameData data) {
        games.put(gameID, data);
    }



    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
}
