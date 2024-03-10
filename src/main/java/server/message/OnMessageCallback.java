package server.message;

import shared.connection.Connection;

@FunctionalInterface
public interface OnMessageCallback {
    void handleMessage(Connection connection, MessagePacketData message);
}
