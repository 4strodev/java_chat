package client;

import server.message.OnMessageCallback;
import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import server.message.MessagePacketData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ClientMessagingService {
    private static ClientMessagingService instance;
    private Connection messaggingConnection;
    private Connection notificationsConnection;
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
        // Start event loop
        this.startEventLoop();

        if (this.messaggingConnection != null) {
            throw new Exception("Connection already established");
        }

        var socket = new Socket("localhost", 9468);
        this.messaggingConnection = new Connection(socket);

        this.messaggingConnection.send(new ConnectionPacketData(nickname));
        boolean connected = this.messaggingConnection.read();
        if (!connected) {
            this.messaggingConnection.close();
            return connected;
        }

        return connected;
    }

    private void startEventLoop() throws Exception {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9469);
                Socket socket = serverSocket.accept();
                notificationsConnection = new Connection(socket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while(true) {
                MessagePacketData messagePacketData;
                try {
                    messagePacketData = this.notificationsConnection.read();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                for (var callback : messageCallbacks.values()) {
                    try {
                        callback.handleMessage(messaggingConnection, messagePacketData);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        this.disconnect();
                        return;
                    }
                }
            }
        }).start();
    }

    public List<String> getConnectedUsers() throws IOException, ClassNotFoundException {
        this.messaggingConnection.send(new MessagePacketData("requestLoggedUsers", null));
        return this.messaggingConnection.read();
    }

    public void disconnect() {
        this.messaggingConnection.close();
        this.notificationsConnection.close();
    }
}
