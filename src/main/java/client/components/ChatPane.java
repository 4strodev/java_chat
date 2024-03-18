package client.components;

import client.ClientMessagingService;
import client.store.ChatsStore;
import client.store.UserStore;
import server.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatPane extends JPanel {
    private final JTextArea messageArea = new JTextArea();
    private final JTextField messageInputField = new JTextField();
    private final JButton sendMessageButton = new JButton("Send");
    private final JButton broadcastMessageButton = new JButton("Broadcast");
    private final ClientMessagingService messagingService = ClientMessagingService.getInstance();
    private final ChatsStore chatsStore = ChatsStore.getInstance();

    public ChatPane() {
        var constraints = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        this.add(this.messageArea, constraints);

        constraints.gridy = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        this.add(this.messageInputField, constraints);

        constraints.weightx = 0;
        constraints.gridx = 1;
        this.add(this.sendMessageButton, constraints);
        this.sendMessageButton.addActionListener(this::sendMessage);
        this.messageInputField.addActionListener(this::sendMessage);
        this.broadcastMessageButton.addActionListener(this::broadcastMessage);

        constraints.gridx = 2;
        this.add(this.broadcastMessageButton, constraints);

        chatsStore.selectOnly(state -> state.chatMessages).subscribe(messages -> {
            var selectedChat = chatsStore.snapshotOnly(state -> state.selectedChat);
            var messageList = messages.get(selectedChat);
            if (messageList == null) {
                messageList = new ArrayList<>();
                messages.put(selectedChat, messageList);
            }
            this.messageArea.setText("");

            for (var message : messageList) {
                this.messageArea.append(message + "\n");
            }
        });
    }

    private void renderMessages(ArrayList<String> messages) {
        chatsStore.snapshot();
    }

    private void broadcastMessage(ActionEvent event) {
        final var message = this.messageInputField.getText();
        try {
            this.messagingService.broadcastMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var state = this.chatsStore.snapshot();

        for(var entry : state.chatMessages.entrySet()) {
            var who = entry.getKey();
            var messageList = entry.getValue();
            if (messageList == null) {
                messageList = new ArrayList<>();
                state.chatMessages.put(who, messageList);
            }
            messageList.add(UserStore.getInstance().snapshotOnly(userState -> userState.nickname) + ": " + message);
            chatsStore.setState(state);
        }
    }

    private void sendMessage(ActionEvent event) {
        final var message = this.messageInputField.getText();
        final var who = this.chatsStore.snapshotOnly(state -> state.selectedChat);

        try {
            this.messagingService.sendMessage(who, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var state = this.chatsStore.snapshot();

        var messageList = state.chatMessages.get(who);
        if (messageList == null) {
            messageList = new ArrayList<>();
            state.chatMessages.put(who, messageList);
        }
        messageList.add(UserStore.getInstance().snapshotOnly(userState -> userState.nickname) + ": " + message);
        chatsStore.setState(state);
    }
}
