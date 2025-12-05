package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Gson gson = new Gson();

    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions == null) {
            Set<Session> newSession = new java.util.HashSet<>(Set.of());
            newSession.add(session);
            connections.put(gameID, newSession);
            return;
        }
        sessions.add(session);
        connections.put(gameID, sessions);
    }

    public void remove(Session session, Integer gameID) {
        Set<Session> sessions = connections.get(gameID);
        sessions.remove(session);
        connections.put(gameID, sessions);
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException {

        String json = gson.toJson(notification);
        Set<Session> sessions = connections.get(gameID);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(json);
                }
            }
        }
    }

    public void show(Session includeSession, ServerMessage notification, Integer gameID) throws IOException {
        String json = gson.toJson(notification);
        Set<Session> sessions = connections.get(gameID);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (c.equals(includeSession)) {
                    c.getRemote().sendString(json);
                }
            }
        }
    }
}