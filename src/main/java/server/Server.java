package server;

import server.message.MessagePacketData;
import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import shared.messages.SendMessageData;
import shared.users.User;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private final ServerMessageService messageService;
    private final HashMap<String, User> connectedUsers = new HashMap<>();
    private Logger logger;

    public Server(int numOfThreads) {
        this.messageService = new ServerMessageService(numOfThreads);
        this.logger = message -> System.out.println("LOG: " + message);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void init() {
        this.messageService.onConnection(this::onConnection);
        this.messageService.onMessage(this::onMessage);
    }

    public void broadcast(MessagePacketData data) {
        for (var user : this.connectedUsers.values()) {
            try {
                user.notificationsConnection().send(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onMessage(Connection connection, MessagePacketData messagePacketData) {
        switch (messagePacketData.messageType()) {
            case MessagePacketData.CLIENT_REQUEST_CONNECTED_USERS -> {
                var users = this.connectedUsers.values().stream().map(User::nickName).toList();
                try {
                    connection.send(users);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    connection.close();
                }
                this.broadcast(new MessagePacketData(MessagePacketData.SERVER_USER_LIST_UPDATED, new ArrayList<>(users)));
            }
            case MessagePacketData.CLIENT_NEW_MESSAGE -> {
                // TODO send message to desired client
                var messageData = (SendMessageData) messagePacketData.data();
                var user = connectedUsers.get(messageData.who());
                if (user == null) {
                    return;
                }

                try {
                    user.notificationsConnection()
                            .send(new MessagePacketData(
                                    MessagePacketData.SERVER_NEW_INCOMING_MESSAGE,
                                    messageData
                            ));
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
            notificationSocket = new Socket(messaggingConnection.netAddress(), data.port());
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
        this.logger.log(String.format("New user %s", user.nickName()));
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
