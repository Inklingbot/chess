package service;

import dataaccess.*;
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

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        if (request.gameID() == null || request.authToken() == null || request.playerColor() == null) {
            throw new BadRequestResponse("Error: bad request");
        }

        if((!Objects.equals(request.playerColor(), "BLACK") && !Objects.equals(request.playerColor(), "WHITE"))) {
            throw new BadRequestResponse("Error: already taken");
        }

        GameData data = gameDAO.getGame(request.gameID());
        if ((Objects.equals(request.playerColor(), "BLACK") && data.blackUsername() == null)
                || (Objects.equals(request.playerColor(), "WHITE") && data.whiteUsername() == null)) {
            AuthData authData = authDAO.getAuth(request.authToken());
            if (authData == null) {
                throw new UnauthorizedResponse("error: unauthorized");
            }
                gameDAO.updateGameUserJoin(request.playerColor(), authData.username(), request.gameID(), gameDAO.getGame(request.gameID()));

        } else {
            throw new DuplicateNameException("Error: already taken");
        }
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        AuthData data = authDAO.getAuth(request.authToken());
        if (data == null) {
            throw new UnauthorizedResponse("error: unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }


    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        AuthData data = authDAO.getAuth(request.authToken());
        if (data == null) {
            throw new UnauthorizedResponse("error: unauthorized");
        }

        if (request.authToken() == null || request.gameName() == null) {
            throw new BadRequestResponse();
        }

        GameData game = gameDAO.createGame(request.gameName());

        return new CreateGameResult(game.gameID());
    }


}

