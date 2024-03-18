package server.message;

import shared.connection.Connection;
import shared.messages.MessagePacketData;

@FunctionalInterface
public interface OnMessageCallback {
    void handleMessage(Connection connection, MessagePacketData message);
}
