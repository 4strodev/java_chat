package server;

import server.connection.Connection;
import server.connection.ConnectionPacketData;
import server.message.MessagePacketData;
import shared.users.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
        System.out.println("Handling new message");
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

    private void onConnection(Connection connection) {
        ConnectionPacketData data;
        try {
            data = connection.read();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        var user = new User(connection, data.nickName());

        if (this.connectedUsers.containsKey(user.nickName())) {
            try {
                connection.send(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                connection.close();
            }
        }
        this.connectedUsers.put(user.nickName(), user);
        try {
            connection.send(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            connection.close();
        }
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
