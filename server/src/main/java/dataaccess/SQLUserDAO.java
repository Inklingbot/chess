package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    @Override
    public void clear() throws DataAccessException {
        clearTable();
    }


    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        storeUserPassword(username, password, email);
    }

    //user
    //\"
    //rs.next
    //index 1 for getString

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String writingStatement = "SELECT username, password, email FROM user WHERE username = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String user = rs.getString(1);
                    String password = rs.getString(2);
                    String email = rs.getString(3);

                    return new UserData(user, password, email);
                }

                return null;

            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }




    void storeUserPassword (String username, String password, String email) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        //write password into database with the other user info
        writeHashedPasswordToDatabase(username, hashedPassword, email);
    }

    void clearTable() throws DataAccessException {
        String writingStatement = "TRUNCATE TABLE user";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    private void writeHashedPasswordToDatabase(String username, String hashedPassword, String email) throws DataAccessException {

        String writingStatement = "INSERT INTO user (username, password, email)" +
                "VALUES (?, ?, ?);";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(writingStatement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getNextException()));
        }
    }

    //might be an issue with duplicate db's

}
