package dataaccess;

import model.AuthData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public void clear() throws DataAccessException {
        clearTable();
    }


    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = writeAuthDataToDB(username);
        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String writingStatement = "SELECT authToken, username FROM auth WHERE authToken = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.setString(1, authToken);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String username = rs.getString(2);
                    return new AuthData(authToken, username);
                }

                return null;

            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String writingStatement = "DELETE from auth WHERE authToken = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(writingStatement);
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    void clearTable() throws DataAccessException {
        String writingStatement = "TRUNCATE TABLE auth";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    private String writeAuthDataToDB(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        String writingStatement = "INSERT INTO auth (authToken, username) " +
                "VALUES (?, ?);";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.setString(1, newAuthToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
                return newAuthToken;
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }
}
