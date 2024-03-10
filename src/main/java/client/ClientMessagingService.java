package client;

import server.connection.Connection;
import server.connection.ConnectionPacketData;
import server.message.MessagePacketData;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientMessagingService {
    private static ClientMessagingService instance;
    private Connection connection;

    protected ClientMessagingService() {
    }

    public static ClientMessagingService getInstance() {
        if (instance == null) {
            instance = new ClientMessagingService();
        }

        return instance;
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
