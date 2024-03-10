package shared.messages;


import shared.connection.ConnectionPacketData;

import java.io.Serializable;

public class MessagePacket implements Serializable {
    public MessageType messageType;
    public MessagePayload messagePayload;

    protected MessagePacket(MessagePayload payload, MessageType type) {
        this.messagePayload = payload;
        this.messageType = type;
    }

    public static MessagePacket ConnectionMessage(ConnectionPacketData payload) {
        return new MessagePacket(payload, MessageType.CONNECT);
    }
}
