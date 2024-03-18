package shared.messages;

public class MessageType {
    public static final String CLIENT_REQUEST_CONNECTED_USERS = "REQUEST_CONNECTED_USERS";
    public static final String SERVER_USER_LIST_UPDATED = "SERVER_USER_LIST_UPDATED";
    public static final String SERVER_NEW_INCOMING_MESSAGE = "SERVER_NEW_INCOMING_MESSAGE";
    public static final String CLIENT_NEW_MESSAGE = "CLIENT_NEW_MESSAGE";
    public static final String CLIENT_BROADCAST_MESSAGE = "CLIENT_BROADCAST_MESSAGE";
    public static final String SERVER_NEW_BROADCAST_MESSAGE = "SERVER_NEW_BROADCAST_MESSAGE";
    protected MessageType() {
    }
}
