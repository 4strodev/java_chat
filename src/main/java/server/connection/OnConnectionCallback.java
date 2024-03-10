package server.connection;

import shared.connection.Connection;

@FunctionalInterface
public interface OnConnectionCallback {
    void handleConnection(Connection connection);
}
