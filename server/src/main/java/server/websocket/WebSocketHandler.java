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
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final Gson gson = new Gson();
    boolean playerResigned = false;
    boolean mateReached = false;

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
            //first check if they previously checkmated
            if (mateReached) {
                var errorCheckmate = new ErrorMessage("You cannot move anymore, a player has won the game!");
                connections.show(session, errorCheckmate);
            }
            //then check if a player has currently resigned
            else if (playerResigned) {
                var resignedMessage = new ErrorMessage("A player has resigned, you cannot move right now.");
                connections.show(session, resignedMessage);

            }
            //to actually make a move, first check that they are a real user
            else if (authDAO.getAuth(authToken) != null) {
                //grab the user and the game
                AuthData auth = authDAO.getAuth(authToken);
                String user = auth.username();
                GameData gameData = gameDAO.getGame(gameID);
                ChessGame currGame = gameData.chessGame();
                //if they're check/stalemated
                if (currGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                        currGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                        currGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                        currGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                    mateReached = true;
                    var errorCheckmate = new ErrorMessage("You cannot move anymore, a player has won the game!");
                    connections.show(session, errorCheckmate);
                    return;
                }
                //make the move
                gameData.chessGame().makeMove(move);
                gameDAO.updateGame(gameData.chessGame(), gameID);
                String game = gson.toJson(gameData);
                var notification = new LoadGame(game);
                connections.broadcast(null, notification);
                var moveNotify = new Notification(user + " made move " + move.toString());
                connections.broadcast(session, moveNotify);
            }

            else {
                ServerMessage notification = new ErrorMessage("This didn't work, but ngl I don't know why bro");
                connections.broadcast(null, notification);
            }
        }
        catch (DataAccessException e) {
            var dataNotStored = new ErrorMessage("You might not believe me," +
                    " but the data here is corrupted or not stored?");
            connections.show(session, dataNotStored);
        } catch (InvalidMoveException e) {
            var wrongMove = new ErrorMessage("This is not a valid move, Genevieve.");
            connections.show(session, wrongMove);
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
        connections.broadcast(session, new Notification("Player left the game."));
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        try {
            if (authDAO.getAuth(authToken) != null && !playerResigned) {
                GameData game = gameDAO.getGame(gameID);
                String username = authDAO.getAuth(authToken).username();
                connections.broadcast(null, new Notification(username + " resigned"));
                connections.remove(session);
                playerResigned = true;
                //Somehow remove the player
            }
            else if (playerResigned) {
                connections.show(session, new ErrorMessage("You cannot resign!"));
            }
            else {
                connections.show(session, new ErrorMessage("You cannot resign!"));
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