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
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

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
                case CONNECT -> enter(message.getAuthToken(), message.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(null, message.getAuthToken(), message.getGameID(), ctx.session);
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
                GameData game = gameDAO.getGame(gameID);
                //Create a variable that will load the game
                game.chessGame().makeMove(move);

                var notification = new ServerMessage(LOAD_GAME, game);
                connections.show(session, notification);
            }
            var notification = new ServerMessage(ERROR, null);
            //Send the Message to the Client
            connections.show(session, notification);
        }
        catch (InvalidMoveException i) {

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void enter(String authToken, Integer gameID, Session session) throws IOException {
        //Add the session to the list
        connections.add(session);

        //Somehow obtain the game to display to the client
        try {
            if (authDAO.getAuth(authToken) != null) {
                GameData game = gameDAO.getGame(gameID);
                //Create a variable that will load the game

                var notification = new ServerMessage(LOAD_GAME, game);
                connections.show(session, notification);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new ServerMessage(ERROR, null);
        //Send the Message to the Client
        connections.show(session, notification);
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
                connections.broadcast(null, new ServerMessage(NOTIFICATION, null));
                connections.remove(session);
                //Somehow remove the player
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var notification = new ServerMessage(ERROR, null);
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
