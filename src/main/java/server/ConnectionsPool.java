package server;

import server.connection.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionsPool {
    private final HashMap<String, Connection> _internalPool = new HashMap<>();

    public ConnectionsPool() {
    }

    public void addConnection(Connection connection) {
        this._internalPool.put(connection.id, connection);
    }

    public void removeConnection(String id) {
        this._internalPool.remove(id);
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(this._internalPool.values());
    }
}
