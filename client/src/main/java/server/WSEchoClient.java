package server;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;

public class WSEchoClient extends Endpoint {
    private final Gson gson = new Gson();
    public Session session;

//    public static void main(String[] args) throws Exception {
//        WsEchoClient client = new WsEchoClient();
//
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Enter a message you want to echo:");
//        while(true) {
//            client.send(scanner.nextLine());
//        }
//    }

    public void WsEchoClient() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
                System.out.println("\nEnter another message you want to echo:");
            }
        });
    }

    public void send(UserGameCommand message) throws IOException {
        String jsonMessage = gson.toJson(message);
        session.getBasicRemote().sendText(String.valueOf(jsonMessage));
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
