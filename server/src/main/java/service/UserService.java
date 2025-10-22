package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DuplicateNameException;
import dataAccess.UserDAO;
import io.javalin.http.BadRequestResponse;
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
//ToDo
    public LoginResult login(LoginRequest request) throws DataAccessException {


        return null;
    }
    //ToDo
    public void logout(LogoutRequest request) throws DataAccessException {

    }


}
