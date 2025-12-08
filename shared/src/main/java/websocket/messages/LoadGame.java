package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage{
    String game;
    public LoadGame(String game) {
        super(ServerMessageType.LOAD_GAME, game);
        this.game = game;
    }
}
