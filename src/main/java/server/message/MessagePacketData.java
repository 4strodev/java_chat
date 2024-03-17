package server.message;

import shared.messages.MessagePayload;

public record MessagePacketData(String messageType, Object data) implements MessagePayload {
    public static final String CLIENT_REQUEST_CONNECTED_USERS = "REQUEST_CONNECTED_USERS";
    public static final String SERVER_USER_LIST_UPDATED = "SERVER_USER_LIST_UPDATED";
    public static final String SERVER_NEW_INCOMING_MESSAGE = "SERVER_NEW_INCOMING_MESSAGE";
    public static final String CLIENT_NEW_MESSAGE = "CLIENT_NEW_MESSAGE";
}
