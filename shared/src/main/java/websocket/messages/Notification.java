package websocket.messages;

public class Notification extends ServerMessage{
    String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION, message);

        this.message = message;
    }

}
