package server.message;

import shared.messages.MessagePayload;

public record MessagePacketData(String messageType, Object data) implements MessagePayload {
}
