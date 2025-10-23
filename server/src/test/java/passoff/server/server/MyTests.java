package passoff.server.server;
import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.*;

import java.net.HttpURLConnection;
import java.util.Locale;


public class MyTests {

    private static UserData existingUser;
    private static RegisterRequest newUser;
    private static GameService gameService;
    private static UserService userService;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    private String existingAuth;

    @BeforeAll
    public static void init() {
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(authDAO, gameDAO, userDAO);
        userService = new UserService(authDAO, userDAO);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new RegisterRequest("NewUser", "newUserPassword", "nu@mail.com");

        }

    @Test
    @DisplayName("Clear Game Pos")
    public void clearGamePositive() throws DataAccessException {
        //Create game Data
        gameDAO.createGame("ayo");
        userDAO.createUser("Gabe", "Gabe", "Gabe");
        authDAO.createAuth("Gabe");
        //Clear the Game
        gameService.clearGame();
        //Check it's cleared
        Assertions.assertNotNull(gameDAO.listGames());
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("Register Game Pos")
    public void registerGamePositive() throws DataAccessException{
        RegisterRequest request = new RegisterRequest("gabriel!", "MyPasswordIs", "gmenden1@byu.edu");

        userService.register(request);

        Assertions.assertNotNull(userDAO.getUser("gabriel!"));

        gameService.clearGame();
    }

    @Test
    @DisplayName("Register Game Neg")
    public void registerGameNegative() throws DataAccessException{
        //Creates a request
        RegisterRequest request = new RegisterRequest(null, "MyPasswordIs", "gmenden1@byu.edu");
        //Looks for the correct Error as it runs request
        Assertions.assertThrows(BadRequestResponse.class, () -> userService.register(request), "Error: bad request");
        gameService.clearGame();
    }

    @Test
    @DisplayName("Join Game Pos")
    public void joinGamePositive() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("feliz", "meButanAuthtoken", "me@gmail.com"));
        CreateGameResult resultOfCreate = gameService.createGame(new CreateGameRequest(registerResult.authToken(), "Feliz Halloween"));


        JoinGameRequest request = new JoinGameRequest(registerResult.authToken(), "WHITE", resultOfCreate.gameID());

        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(request);
        });
        gameService.clearGame();
    }

    @Test
    @DisplayName("Join Game Neg")
    public void joinGameNegative() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("feliz", "meButanAuthtoken", "me@gmail.com"));
        CreateGameResult resultOfCreate = gameService.createGame(new CreateGameRequest(registerResult.authToken(), "Feliz Halloween"));
        JoinGameRequest request = new JoinGameRequest("Incorrect Authtoken", "WHITE", resultOfCreate.gameID());

        Assertions.assertThrows(UnauthorizedResponse.class, () -> gameService.joinGame(request), "Error: unauthorized");
        gameService.clearGame();
    }

    @Test
    @DisplayName("Create Game Pos")
    public void createGamePositive() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));

        CreateGameRequest request = new CreateGameRequest(registerResult.authToken(), "myGame");

        Assertions.assertDoesNotThrow(() -> {
            gameService.createGame(request);
        });
        gameService.clearGame();
    }

    @Test
    @DisplayName("Create Game Neg")
    public void createGameNegative() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));

        CreateGameRequest request = new CreateGameRequest(registerResult.authToken(), null);

        Assertions.assertThrows(BadRequestResponse.class, () -> {
            gameService.createGame(request);

        });
        gameService.clearGame();
    }

    @Test
    @DisplayName("List Games Pos")
    public void listGamePositive() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));
        CreateGameRequest request = new CreateGameRequest(registerResult.authToken(), "myGame");
        gameService.createGame(request);

        Assertions.assertNotNull(gameService.listGames(new ListGamesRequest(registerResult.authToken())));
        gameService.clearGame();
    }

    @Test
    @DisplayName("List Games Neg")
    public void listGameNegative() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));
        CreateGameRequest request = new CreateGameRequest(registerResult.authToken(), "myGame");
        gameService.createGame(request);

        Assertions.assertThrows(UnauthorizedResponse.class, () -> {
            gameService.listGames(new ListGamesRequest("Wrong Authtoken"));

        });

        gameService.clearGame();
    }

    @Test
    @DisplayName("Login Pos")
    public void loginPositive() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));
        Assertions.assertDoesNotThrow(() -> {
            userService.login(new LoginRequest("me", "me"));
        });

        gameService.clearGame();
    }

    @Test
    @DisplayName("Login Neg")
    public void loginNegative() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));

        Assertions.assertThrows(UnauthorizedResponse.class, () -> {
            userService.login(new LoginRequest("me", "ME?"));

        });

        gameService.clearGame();
    }

    @Test
    @DisplayName("Logout Pos")
    public void logoutPositive() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));
        LoginResult result = userService.login(new LoginRequest("me", "me"));

        Assertions.assertDoesNotThrow(() -> {
            userService.logout(new LogoutRequest(result.authToken()));
        });

        gameService.clearGame();

    }

    @Test
    @DisplayName("Logout Neg")
    public void logoutNegative() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("me", "me", "me too"));
        LoginResult result = userService.login(new LoginRequest("me", "me"));

        Assertions.assertThrows(UnauthorizedResponse.class, () -> {
            userService.logout(new LogoutRequest("This isn't the right AuthToken!"));

        });

        gameService.clearGame();

    }




}
