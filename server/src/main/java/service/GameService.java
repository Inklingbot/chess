package service;

import dataAccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;

import java.util.Objects;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }
    //Implements ClearGame, ViewGame, and JoinGame, also maybe ListGames as well

    public void clearGame() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
    //ToDo
    public void joinGame(JoinGameRequest request) throws DataAccessException {
        if (request.gameID() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        GameData data = gameDAO.getGame(request.gameID());
        if(Objects.equals(request.playerColor(), "Black") && data.blackUsername() != null
        || Objects.equals(request.playerColor(), "White") && data.WhiteUsername() != null) {
            AuthData authData = authDAO.getAuth(request.authToken());
            if (authData == null) {
                throw new UnauthorizedResponse("error: unauthorized");
            }
            gameDAO.updateGame(request.gameID(), gameDAO.getGame(request.gameID()));
        }
        else {
            throw new DuplicateNameException("Name taken");
        }
    }

    public ListGamesResult ListGames(ListGamesRequest request) throws Error, DataAccessException {
        AuthData data = authDAO.getAuth(request.authToken());
        if (data == null) {
            throw new UnauthorizedResponse("error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }

    //ToDo
    public CreateGameResult CreateGame(CreateGameRequest request) throws Error {
        return null;
    }


}
