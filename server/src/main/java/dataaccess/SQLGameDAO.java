package dataaccess;

import model.GameData;
import passoff.exception.ResponseParseException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {

        //make a var statement that is a SQL statement (basically create a new table????)

        //turn the gameName and a new Game into a json string
        //use an executeUpdate function that will update everything in the game
        //return the new game
        return null;
    }

    //when deserializing we have to recreate all the pieces and stuff so it might get messy :skull emoji:
    //write a type adapter, but maybe not because I used delegation instead of inheritance

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGameUserJoin(String color, String username, int gameID, GameData data) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            gameID INT,
            whiteUsername VARCHAR(20),
            blackUsername VARCHAR(20),
            gameName VARCHAR(20),
            chessGame LONGTEXT
            /*all the information for a table */
            )
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }


}


