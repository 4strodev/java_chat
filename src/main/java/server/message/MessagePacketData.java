package server.message;

import shared.messages.MessagePayload;

public record MessagePacketData(String messageType, Object data) implements MessagePayload {
    public static final String CLIENT_REQUEST_CONNECTED_USERS = "REQUEST_CONNECTED_USERS";
    public static final String SERVER_USER_LIST_UPDATED = "SERVER_USER_LIST_UPDATED";
}
