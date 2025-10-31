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
    private int id = 0;
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
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        id++;
        addGameToDB(id,  gameName, null, null, game);

        return new GameData(id, null, null, gameName, game);
    }

    public void addGameToDB(Integer id, String gameName, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException {
        String writingStatement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?);";
        String gameJSON = gson.toJson(game);
        String identification = String.valueOf(id);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.setString(1, identification);
                preparedStatement.setString(2, whiteUsername);
                preparedStatement.setString(3, blackUsername);
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, gameJSON);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }
    //when deserializing we have to recreate all the pieces and stuff so it might get messy :skull emoji:
    //write a type adapter, but maybe not because I used delegation instead of inheritance

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new java.util.ArrayList<>(List.of());
        for (int i = 1; i <= id; i++) {
            games.add(getGame(i));
        }
        return games;
    }

    @Override
    public void updateGameUserJoin(String color, String username, int gameID, GameData data) throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = " + gameID + ";";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    Integer id = Integer.parseInt(rs.getString(1));
                    String whitesUsername = rs.getString(2);
                    String blacksUsername = rs.getString(3);
                    String obtainedGameName = rs.getString(4);
                    String obtainedJSONString = rs.getString(5);

                    if (Objects.equals(color, "BLACK")) {
                        blacksUsername = username;
                        insertColorName(whitesUsername, blacksUsername, gameID, obtainedJSONString, obtainedGameName);
                    }
                    else if (Objects.equals(color, "WHITE")) {
                        whitesUsername = username;
                        insertColorName(whitesUsername, blacksUsername, gameID, obtainedJSONString, obtainedGameName);
                    }
                }



            }
        } catch (SQLException e) {
            throw new ResponseException("Unable to configure database" + e.getMessage());
        }

    }

    public void insertColorName (String whitesUsername, String blacksUsername, int gameID, String data, String gameName)
            throws DataAccessException {
                String writingStatement = """
UPDATE game SET gameID = ?, whiteUsername = ?, blackUsername = ?,
                gameName = ?, chessGame = ? WHERE gameID = ?;""";

        String identify = String.valueOf(id);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparingStatement = conn.prepareStatement(writingStatement)) {
                preparingStatement.setString(1, identify);
                preparingStatement.setString(2, whitesUsername);
                preparingStatement.setString(3, blacksUsername);
                preparingStatement.setString(4, gameName);
                preparingStatement.setString(5, data);
                preparingStatement.setInt(6, gameID);

                preparingStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
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
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    }





