package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    //implements Register, login, and logout

    public RegisterResult register(RegisterRequest request) throws DataAccessException {

        try {
            UserData userData = userDAO.getUser(request.username());
            throw new DataAccessException(null);
        } catch (DataAccessException e) {
            userDAO.createUser(request.username(), request.password(), request.email());
            AuthData authData = authDAO.createAuth(request.username());

            return new RegisterResult(request.username(), authData.authToken());
        }
    }
//ToDo
    public LoginResult login(LoginRequest request) throws DataAccessException {


        return null;
    }
    //ToDo
    public void logout(LogoutRequest request) throws DataAccessException {

    }


}
