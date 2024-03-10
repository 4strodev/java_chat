package server;

import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import server.message.MessagePacketData;
import shared.users.User;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private final ServerMessageService messageService;
    private final HashMap<String, User> connectedUsers = new HashMap<>();

    public Server(int numOfThreads) {
        this.messageService = new ServerMessageService(numOfThreads);
    }

    public void init() {
        this.messageService.onConnection(this::onConnection);
        this.messageService.onMessage(this::onMessage);
    }

    private void onMessage(Connection connection, MessagePacketData messagePacketData) {
        switch (messagePacketData.messageType()) {
            case "requestLoggedUsers" -> {
                var users = this.connectedUsers.values().stream().map(User::nickName).toList();
                try {
                    connection.send(users);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    connection.close();
                }
            }
            default -> {
                System.out.println("Unexpected message type");
                connection.close();
            }
        }
    }

    private void onConnection(Connection messaggingConnection) {
        ConnectionPacketData data;
        try {
            data = messaggingConnection.read();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (this.connectedUsers.containsKey(data.nickName())) {
            try {
                messaggingConnection.send(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                messaggingConnection.close();
            }
        }
        try {
            messaggingConnection.send(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            messaggingConnection.close();
        }

        Connection notificationConnection;
        Socket notificationSocket;
        try {
            notificationSocket = new Socket(messaggingConnection.netAddress(), 9469);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            notificationConnection = new Connection(notificationSocket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var user = new User(messaggingConnection, notificationConnection, data.nickName());
        this.connectedUsers.put(data.nickName(), user);



    }

    public void start() {
        Thread thread = new Thread(() -> {
            try {
                this.messageService.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
}
