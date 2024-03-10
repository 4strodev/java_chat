package server.connection;

import shared.messages.MessagePayload;

public record ConnectionPacketData(String nickName) implements MessagePayload {}
