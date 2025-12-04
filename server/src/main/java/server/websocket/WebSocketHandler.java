package server.websocket;

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
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();

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
                case MAKE_MOVE -> makeMove(message.getMove(), message.getAuthToken(), message.getGameID(), ctx.session);
                case LEAVE -> exit(message.getAuthToken(), message.getGameID(), ctx.session);
                case RESIGN -> resign(message.getAuthToken(), message.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void makeMove(ChessMove move, String authToken, Integer gameID, Session session) throws IOException {
        try {
            if (authDAO.getAuth(authToken) != null) {
                GameData gameData = gameDAO.getGame(gameID);
                //Create a variable that will load the game
                gameData.chessGame().makeMove(move);


                String game = gson.toJson(gameData);
                var notification = new LoadGame(game);
                connections.broadcast(null, notification);

            }
            else {
                ServerMessage notification = new ErrorMessage("This didn't work");
                connections.broadcast(null, notification);
            }

            //Send the Message to the Client

        }
        catch (InvalidMoveException i) {

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException {
        //Add the session to the list
        connections.add(session);

        //Somehow obtain the game to display to the client
        try {
            if (authDAO.getAuth(authToken) != null) {
                GameData gameData = gameDAO.getGame(gameID);
                    AuthData auth = authDAO.getAuth(authToken);
                    String user = auth.username();
                    //Create a variable that will load the game
                    String game = gson.toJson(gameData);
                    var notification = new LoadGame(game);
                    connections.show(session, notification);
                    ServerMessage notify = new Notification(user + " has Joined.");
                    connections.broadcast(session, notify);


            }
            else {
                var notification = new ErrorMessage("You're unauthorized! Please try again.");
                //Send the Message to the Client
                connections.show(session, notification);
            }
        } catch (DataAccessException e) {
            var notification = new ErrorMessage("This gameID is invalid! Please choose another.");
            //Send the Message to the Client
            connections.show(session, notification);
        }

    }

    private void exit(String authToken, Integer gameID, Session session) throws IOException {
        connections.remove(session);
        //How do I send a specific message, like the "Player left the game?"
        connections.broadcast(null, new Notification("Player left the game."));
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        //How do I send a message that says "Player resigned, Player Wins!"
        try {
            if (authDAO.getAuth(authToken) != null) {
                GameData game = gameDAO.getGame(gameID);
                connections.broadcast(null, new Notification("Player resigned"));
                connections.remove(session);
                //Somehow remove the player
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new ErrorMessage("This isn't working");
        //Send the Message to the Client
        connections.show(session, notification);
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
}
