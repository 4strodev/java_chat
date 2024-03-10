package server.message;

import server.connection.Connection;

@FunctionalInterface
public interface OnMessageCallback {
    void handleMessage(Connection connection, MessagePacketData message);
}
