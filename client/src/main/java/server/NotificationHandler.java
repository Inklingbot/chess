package server;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    Gson GSON = new Gson();

    void notify(ServerMessage newMessage, String message);
}
