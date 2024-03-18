package shared.messages;

public record BroadcastMessageData(String message, String sender) implements MessagePayload {
}
