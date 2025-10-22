package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

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
    public void joinGame() throws Error{

    }
    //ToDo
    public ListGamesResult ListGames(ListGamesRequest request) throws Error{
        return null;
    }
    //ToDo
    public CreateGameResult CreateGame(CreateGameRequest request) throws Error {
        return null;
    }


}
