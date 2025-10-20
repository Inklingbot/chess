package dataAccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {

    void clear() throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;


}
