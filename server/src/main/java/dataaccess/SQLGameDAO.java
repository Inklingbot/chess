package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SQLGameDAO implements GameDAO{
    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        clearTable();
    }

    void clearTable() throws DataAccessException {
        String writingStatement = "TRUNCATE TABLE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getNextException()));
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The Server isn't running!");
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        int newID = addGameToDB(gameName, null, null, game);

        return new GameData(newID, null, null, gameName, game);
    }

    public int addGameToDB(String gameName, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException {
        String writingStatement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?);";
        String gameJSON = gson.toJson(game);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, gameJSON);

                preparedStatement.executeUpdate();
                try (var rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedGameID = rs.getInt(1);
                        return generatedGameID;
                    }
                    else {
                        throw new DataAccessException("Uh oh");
                    }
                }
            }

        } catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getNextException()));
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The server is not running!");
        }
    }
    //when deserializing we have to recreate all the pieces and stuff so it might get messy :skull emoji:
    //write a type adapter, but maybe not because I used delegation instead of inheritance

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new java.util.ArrayList<>(List.of());

        String statement = "SELECT * FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparingStatement = conn.prepareStatement(statement)) {

                ResultSet rs = preparingStatement.executeQuery();
                while (rs.next()) {
                    Integer gameID = rs.getInt("gameID");
                    String jsonGame = rs.getString("chessGame");
                    String gameName = rs.getString("gameName");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    ChessGame game = gson.fromJson(jsonGame, ChessGame.class);
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }

            }
        }
        catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The server is not running!");
        }
        return games;
    }



    @Override
    public void updateGameUserJoin(String color, String username, int gameID, GameData data) throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = " + "?;";
        String deleteStatement = "DELETE FROM game WHERE gameID = " + "?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String whitesUsername = rs.getString(2);
                    String blacksUsername = rs.getString(3);
                    String obtainedGameName = rs.getString(4);
                    String obtainedJSONString = rs.getString(5);

                    if (Objects.equals(color.toUpperCase(), "BLACK")) {
                        blacksUsername = username;
                        insertColorName(whitesUsername, blacksUsername, gameID, obtainedJSONString, obtainedGameName);
                    }
                    else if (Objects.equals(color.toUpperCase(), "WHITE")) {
                        whitesUsername = username;
                        insertColorName(whitesUsername, blacksUsername, gameID, obtainedJSONString, obtainedGameName);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException("Error: Unable to configure database" + e.getMessage());
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The server is not running!");
        }
    }



    public void insertColorName (String whitesUsername, String blacksUsername, int gameID, String data, String gameName)
            throws DataAccessException {
                String writingStatement = "UPDATE game SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?;";


        try (var conn = DatabaseManager.getConnection()) {
            try (var preparingStatement = conn.prepareStatement(writingStatement)) {
                preparingStatement.setInt(3, gameID);
                preparingStatement.setString(1, whitesUsername);
                preparingStatement.setString(2, blacksUsername);

                preparingStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getNextException()));
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The server is not running!");
        }

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sqlStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparingStatement = conn.prepareStatement(sqlStatement)) {
                preparingStatement.setInt(1, gameID);
                ResultSet rs = preparingStatement.executeQuery();
                if (rs.next()) {
                    String jsonGame = rs.getString("chessGame");
                    String gameName = rs.getString("gameName");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    ChessGame game = gson.fromJson(jsonGame, ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
                throw new DataAccessException("Game does not exist!");

            }
        }
        catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
        catch (DataAccessException d) {
            throw new DataAccessException("Error: The server is not running!");
        }
    }

    public void updateGame(ChessGame gameData, Integer gameID) throws DataAccessException {
        String sqlStatement = "UPDATE game SET chessGame = ? WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparingStatement = conn.prepareStatement(sqlStatement)) {
                String gameJson = gson.toJson(gameData);
                preparingStatement.setString(1, gameJson);
                preparingStatement.setInt(2, gameID);
                preparingStatement.executeUpdate();

            }
        }
        catch (SQLException ex) {
            throw new ResponseException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }
    }





