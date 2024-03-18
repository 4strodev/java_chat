package server;

import shared.messages.BroadcastMessageData;
import shared.messages.MessagePacketData;
import shared.connection.Connection;
import shared.connection.ConnectionPacketData;
import shared.messages.MessageType;
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
            System.out.println("Sending message to: " + user.nickName());
            try {
                user.notificationsConnection().send(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onMessage(Connection connection, MessagePacketData messagePacketData) {
        switch (messagePacketData.messageType()) {
            case MessageType.CLIENT_REQUEST_CONNECTED_USERS -> {
                var users = this.connectedUsers.values().stream().map(User::nickName).toList();
                try {
                    connection.send(users);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    connection.close();
                }
                this.broadcast(new MessagePacketData(MessageType.SERVER_USER_LIST_UPDATED, new ArrayList<>(users)));
            }
            case MessageType.CLIENT_NEW_MESSAGE -> {
                var messageData = (SendMessageData) messagePacketData.data();
                this.logger.log("New message from: " + messageData.sender());
                var user = connectedUsers.get(messageData.who());
                if (user == null) {
                    throw new RuntimeException("User not found");
                }

                try {
                    user.notificationsConnection()
                            .send(new MessagePacketData(
                                    MessageType.SERVER_NEW_INCOMING_MESSAGE,
                                    messageData
                            ));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                logger.log(String.format("Message from %s sent to %s", messageData.sender(), messageData.who()));
            }
            case MessageType.CLIENT_BROADCAST_MESSAGE -> {
                var messageData = (BroadcastMessageData) messagePacketData.data();
                this.logger.log("New message from: " + messageData.sender());

                this.broadcast(new MessagePacketData(
                        MessageType.SERVER_NEW_BROADCAST_MESSAGE,
                        messageData
                ));
            }
            case MessageType.CLIENT_DISCONNECT -> {
                var who = (String) messagePacketData.data();
                System.out.println(who + ": Disconnected");
                this.logger.log(who + ": Disconnected");
                this.connectedUsers.remove(who);
                var users = connectedUsers.values().stream().map(User::nickName).toList();
                this.broadcast(new MessagePacketData(MessageType.SERVER_USER_LIST_UPDATED, new ArrayList<>(users)));
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
        System.out.println("Server listening: ");
    }
}
