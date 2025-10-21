package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import com.google.gson.Gson;

public class UserService {
    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    //implements Register, login, and logout

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData userData = userDAO.getUser(request.username());

        userDAO.createUser(request.username(), request.password(), request.email());
        AuthData authData = authDAO.createAuth(request.username());
        RegisterResult result = new RegisterResult(request.username(), authData.authToken());


        return result;
    }
}
