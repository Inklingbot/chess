package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();

    HashMap<Integer, Boolean> resignChecker = new HashMap<>();

    private final ConnectionManager connections = new ConnectionManager();
    GameDAO gameDAO;
    UserDAO userDAO;
    AuthDAO authDAO;
    public WebSocketHandler(GameDAO gameDAO, UserDAO userDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand message = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (message.getCommandType()) {
                case CONNECT -> connect(message.getAuthToken(), message.getGameID(), ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand move = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(move.getMove(), move.getAuthToken(), move.getGameID(), ctx.session);
                }
                case LEAVE -> exit(message.getAuthToken(), message.getGameID(), ctx.session);
                case RESIGN -> resign(message.getAuthToken(), message.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void makeMove(ChessMove move, String authToken, Integer gameID, Session session) throws IOException {
        try {
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame currGame = gameData.chessGame();
            //first check if they previously checkmated
            if (currGame.getGameOver() == true) {
                var errorCheckmate = new ErrorMessage("You cannot make this move, the game is over!");
                connections.show(session, errorCheckmate, gameID);
            }
            //to actually make a move, first check that they are a real user
            else if (authDAO.getAuth(authToken) != null) {
                //grab the user and the game
                boolean correctUser = false;
                AuthData auth = authDAO.getAuth(authToken);
                String user = auth.username();
                if (currGame.getTeamTurn() == WHITE) {
                    if (Objects.equals(user, gameData.whiteUsername())) {
                        correctUser = true;
                    }
                }
                else if (currGame.getTeamTurn() == BLACK) {
                    if (Objects.equals(user, gameData.blackUsername())) {
                        correctUser = true;
                    }
                }

                if (!correctUser) {
                    var ErrorNotAllowed = new ErrorMessage("You're not allowed to move!");
                    connections.show(session, ErrorNotAllowed, gameID);
                    return;
                }

                //if they're checkmated or stalemated
                if (currGame.isInCheckmate(WHITE) ||
                        currGame.isInCheckmate(BLACK) ||
                        currGame.isInStalemate(WHITE) ||
                        currGame.isInStalemate(BLACK)) {
                    currGame.setGameOver(true);
                    gameDAO.updateGame(currGame, gameID);
                    var errorCheckmate = new ErrorMessage("You cannot move anymore, a player has won the game!");
                    connections.show(session, errorCheckmate, gameID);
                    return;
                }
                //make the move
                gameData.chessGame().makeMove(move);
                gameDAO.updateGame(gameData.chessGame(), gameID);
                String game = gson.toJson(gameData.chessGame());
                var notification = new LoadGame(game);
                connections.broadcast(null, notification, gameID);
                var moveNotify = new Notification(user + " made move " + move);
                connections.broadcast(session, moveNotify, gameID);
            }

            else {
                ServerMessage notification = new ErrorMessage("This didn't work, but ngl I don't know why bro");
                connections.show(session, notification, gameID);
            }
        }
        catch (DataAccessException e) {
            var dataNotStored = new ErrorMessage("You might not believe me," +
                    " but the data here is corrupted or not stored?");
            connections.show(session, dataNotStored, gameID);
        } catch (InvalidMoveException e) {
            var wrongMove = new ErrorMessage("This is not a valid move, Genevieve.");
            connections.show(session, wrongMove, gameID);
        }
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException {
        //Add the session to the list
        connections.add(session, gameID);
        resignChecker.put(gameID, false);

        //Somehow obtain the game to display to the client
        try {
            if (authDAO.getAuth(authToken) != null) {
                GameData gameData = gameDAO.getGame(gameID);
                    AuthData auth = authDAO.getAuth(authToken);
                    String user = auth.username();
                    //Create a variable that will load the game
                    String game = gson.toJson(gameData);
                    var notification = new LoadGame(game);
                    connections.show(session, notification, gameID);
                    if (Objects.equals(gameData.whiteUsername(), user)) {
                        ServerMessage notify = new Notification(user + " has Joined on White.");
                        connections.broadcast(session, notify, gameID);
                    }
                    else if (Objects.equals(gameData.blackUsername(), user)) {
                        ServerMessage notify = new Notification(user + " has Joined on Black.");
                        connections.broadcast(session, notify, gameID);
                    }
                    else {
                        ServerMessage notify = new Notification(user + " has Joined as an Observer.");
                        connections.broadcast(session, notify, gameID);
                    }
            }
            else {
                var notification = new ErrorMessage("You're unauthorized! Please try again.");
                //Send the Message to the Client
                connections.show(session, notification, gameID);
            }
        } catch (DataAccessException e) {
            var notification = new ErrorMessage("This gameID is invalid! Please choose another.");
            //Send the Message to the Client
            connections.show(session, notification, gameID);
        }

    }

    private void exit(String authToken, Integer gameID, Session session) throws IOException {
        connections.remove(session, gameID);
        //How do I send a specific message, like the "Player left the game?"
        connections.broadcast(session, new Notification("Player left the game."), gameID);

        try {
            String username = authDAO.getAuth(authToken).username();
            GameData game = gameDAO.getGame(gameID);
            if (Objects.equals(game.whiteUsername(), username)) {
                gameDAO.updateGameUserJoin("WHITE", null, gameID, game);
            }
            else if (Objects.equals(game.blackUsername(), username)) {
                gameDAO.updateGameUserJoin("BLACK", null, gameID, game);
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        try {
            boolean resignation = resignChecker.get(gameID);
            if (authDAO.getAuth(authToken) != null && !resignation) {
                GameData game = gameDAO.getGame(gameID);
                String username = authDAO.getAuth(authToken).username();
                if (!Objects.equals(game.whiteUsername(), username) &&
                        !Objects.equals(game.blackUsername(), username)) {
                    var observerIsDumb = new ErrorMessage("You're an Observer, you cannot resign!");
                    connections.show(session, observerIsDumb, gameID);
                    return;
                }
                connections.broadcast(null, new Notification(username + " resigned"), gameID);
                connections.remove(session, gameID);
                resignChecker.put(gameID, true);
                ChessGame chessGame = game.chessGame();
                chessGame.setGameOver(true);
                gameDAO.updateGame(chessGame, gameID);
                //Somehow remove the player
            }
            else if (resignation) {
                connections.show(session, new ErrorMessage("You cannot resign!"), gameID);
            }
            else {
                connections.show(session, new ErrorMessage("You cannot resign!"), gameID);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


}






//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }