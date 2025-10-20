package dataAccess;

import model.GameData;

import javax.xml.crypto.Data;

public interface GameDAO {
    void clear() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    String listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    /*
    createGame: Create a new game.
getGame: Retrieve a specified game with the given game ID.
listGames: Retrieve all games.
updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
This is used when players join a game or when a move is made.
     */
}
