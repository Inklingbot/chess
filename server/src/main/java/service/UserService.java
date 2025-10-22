package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DuplicateNameException;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {
    AuthDAO authDAO;
    UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    //implements Register, login, and logout

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
            if(request.username() == null || request.password() == null || request.email() == null) {
                throw new BadRequestResponse("Error: bad request");
            }
            boolean isTaken = false;
            UserData userData = userDAO.getUser(request.username());
            if (userData != null) {isTaken = true;}
            if (isTaken) {
                throw new DuplicateNameException("Error: already taken");
        }

            userDAO.createUser(request.username(), request.password(), request.email());
            AuthData authData = authDAO.createAuth(request.username());

            return new RegisterResult(request.username(), authData.authToken());
    }


    public LoginResult login(LoginRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        LoginResult result = null;

        UserData user = userDAO.getUser(request.username());
        if (user == null) {
            throw new UnauthorizedResponse("Error: unauthorized");
        }
        if (Objects.equals(user.password(), request.password())) {
            AuthData newData = authDAO.createAuth(request.username());
            result = new LoginResult(newData.username(), newData.authToken());
        }
        else {
            throw new UnauthorizedResponse("error: unauthorized");
        }

        return result;
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        AuthData data = authDAO.getAuth(request.authToken());
        if (data == null) {
            throw new UnauthorizedResponse("error: unauthorized");
        }

        authDAO.deleteAuth(request.authToken());
    }


}
