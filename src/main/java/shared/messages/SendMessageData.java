package shared.messages;

public record SendMessageData(String who, String message, String sender) implements MessagePayload {
}
