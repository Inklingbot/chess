package websocket.messages;

import model.GameData;

public class Error extends ServerMessage{
    String errorMessage;
    public Error(ServerMessageType type, GameData game) {
        super(type, game);
        this.type = errorMessage;
    }
}
