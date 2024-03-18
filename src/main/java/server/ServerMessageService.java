package server;

import shared.connection.Connection;
import server.connection.OnConnectionCallback;
import shared.messages.MessagePacketData;
import server.message.OnMessageCallback;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMessageService {
    private ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final HashMap<String, OnConnectionCallback> connectionCallbacks = new HashMap<>();
    private final HashMap<String, OnMessageCallback> messageCallbacks = new HashMap<>();

    public ServerMessageService(int numOfThreads) {
        this.executorService = Executors.newFixedThreadPool(numOfThreads);
    }

    public void startEventLoop(Connection connection) {
        for (var callback : connectionCallbacks.values()) {
            callback.handleConnection(connection);
        }

        while (true) {
            MessagePacketData messagePacketData;
            try {
                messagePacketData = connection.read();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (var callback : messageCallbacks.values()) {
                try {
                    callback.handleMessage(connection, messagePacketData);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    connection.close();
                    return;
                }
            }
        }
    }

    public void start() throws Exception {
        this.serverSocket = new ServerSocket(9468);
        while (true) {
            final var messaggingSocket = this.serverSocket.accept();
            var messaggingConnection = new Connection(messaggingSocket);

            this.executorService.execute(() -> this.startEventLoop(messaggingConnection));
        }
    }

    public String onConnection(OnConnectionCallback callback) {
        String id = UUID.randomUUID().toString();
        this.connectionCallbacks.put(id, callback);
        return id;
    }

    public String onMessage(OnMessageCallback callback) {
        String id = UUID.randomUUID().toString();
        this.messageCallbacks.put(id, callback);
        return id;
    }

    public void removeConnectionCallback(String id) {
        this.connectionCallbacks.remove(id);
    }
    public void removeMessageCallback(String id) {
        this.messageCallbacks.remove(id);
    }

    public void stop() throws IOException {
        if (!this.serverSocket.isClosed()) {
            this.serverSocket.close();
        }
    }
}
