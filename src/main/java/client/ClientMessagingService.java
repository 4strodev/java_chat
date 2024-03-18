package client;

import client.store.UserStore;
import server.message.OnMessageCallback;
import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import shared.messages.BroadcastMessageData;
import shared.messages.MessagePacketData;
import shared.messages.MessageType;
import shared.messages.SendMessageData;

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
    private ServerSocket serverSocket;

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
        this.serverSocket = new ServerSocket(0);
        // Start event loop
        this.startNotificationServer();

        if (this.messaggingConnection != null) {
            throw new Exception("Connection already established");
        }

        var socket = new Socket("localhost", 9468);
        this.messaggingConnection = new Connection(socket);

        this.messaggingConnection.send(new ConnectionPacketData(nickname, this.serverSocket.getLocalPort()));
        boolean connected = this.messaggingConnection.read();
        if (!connected) {
            this.messaggingConnection.close();
            return connected;
        }

        return connected;
    }

    private void startNotificationServer() {
        new Thread(() -> {
            try {
                Socket socket = serverSocket.accept();
                notificationsConnection = new Connection(socket);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (true) {
                MessagePacketData messagePacketData;
                try {
                    messagePacketData = this.notificationsConnection.read();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                    return;
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
        this.messaggingConnection.send(new MessagePacketData(MessageType.CLIENT_REQUEST_CONNECTED_USERS, null));
        return this.messaggingConnection.read();
    }

    public void sendMessage(String who, String message) throws IOException {
        var nickName = UserStore.getInstance().snapshotOnly(state -> state.nickname);
        var messagePacketData = new MessagePacketData(MessageType.CLIENT_NEW_MESSAGE, new SendMessageData(who, message, nickName));
        this.messaggingConnection.send(messagePacketData);
    }

    public void broadcastMessage(String message) throws IOException {
        var nickName = UserStore.getInstance().snapshotOnly(state -> state.nickname);
        var messagePacketData = new MessagePacketData(MessageType.CLIENT_BROADCAST_MESSAGE, new BroadcastMessageData(message, nickName));
        this.messaggingConnection.send(messagePacketData);
    }

    public void disconnect() {
        try {
            var nickname = UserStore.getInstance().snapshotOnly(state -> state.nickname);
            this.messaggingConnection.send(new MessagePacketData(MessageType.CLIENT_DISCONNECT, nickname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.messaggingConnection.close();
        this.notificationsConnection.close();
    }
}
