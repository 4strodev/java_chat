package server.connection;

import server.connection.Connection;
import server.connection.ConnectionPacketData;

import java.io.IOException;

@FunctionalInterface
public interface OnConnectionCallback {
    void handleConnection(Connection connection);
}
