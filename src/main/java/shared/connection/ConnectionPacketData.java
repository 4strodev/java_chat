package shared.connection;

import shared.messages.MessagePayload;

public record ConnectionPacketData(String nickName, int port) implements MessagePayload {}
