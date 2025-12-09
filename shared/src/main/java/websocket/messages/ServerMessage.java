package websocket.messages;

import chess.ChessGame;
import model.GameData;

import java.util.Objects;
import com.google.gson.Gson;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType type;

    public ServerMessage(ServerMessageType type) {
        this.type = type;
    }

    public ServerMessageType getType() {
        return type;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
