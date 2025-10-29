package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        storeUserPassword(username, password, email);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }


    void storeUserPassword (String username, String password, String email) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        //write password into database with the other user info

        writeHashedPasswordToDatabase(username, hashedPassword, email);
    }

    private void writeHashedPasswordToDatabase(String username, String hashedPassword, String email) throws DataAccessException {
        String writingStatement = "INSERT INTO user (username, password, email)" +
                "VALUES (" + username + ", " + hashedPassword + ", "  + email + ");";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        var hashedPassword = readHashedPasswordFromDatabase(username);
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    String readHashedPasswordFromDatabase(String username) {
        return null;
    }

    //might be an issue with duplicate db's

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(256),
            password LONGTEXT,
            email VARCHAR(256)
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
