package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGame extends ServerMessage{
    String game;
    public LoadGame(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getGame() {
        return game;
    }
}
