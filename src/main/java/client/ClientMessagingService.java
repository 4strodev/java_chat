package client;

import server.message.OnMessageCallback;
import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import server.message.MessagePacketData;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClientMessagingService {
    private static ClientMessagingService instance;
    private Connection connection;
    private HashMap<String, OnMessageCallback> messageCallbacks = new HashMap<>();

    protected ClientMessagingService() {
    }

    public static ClientMessagingService getInstance() {
        if (instance == null) {
            instance = new ClientMessagingService();
        }

        return instance;
    }

    public String onMessage(OnMessageCallback onMessageCallback) {
        var id = UUID.randomUUID().toString();
        this.messageCallbacks.put(id, onMessageCallback);
        return id;
    }

    public void removeMessageCallback(String id) {
        this.messageCallbacks.remove(id);
    }

    public boolean connect(String nickname) throws Exception {
        if (this.connection != null) {
            throw new Exception("Connection already established");
        }

        var socket = new Socket("localhost", 9468);
        this.connection = new Connection(socket);

        this.connection.send(new ConnectionPacketData(nickname));
        return this.connection.read();
    }

    public List<String> getConnectedUsers() throws IOException, ClassNotFoundException {
        this.connection.send(new MessagePacketData("requestLoggedUsers", null));
        return this.connection.read();
    }

    public void disconnect() {
        this.connection.close();
    }
}
