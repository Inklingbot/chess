package server;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    Gson gson = new Gson();

    void notify(ServerMessage newMessage, String message);
}
