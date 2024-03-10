package client.components;

import client.ClientMessagingService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AvailableChatsList extends JPanel {
    ClientMessagingService clientMessagingService = ClientMessagingService.getInstance();

    public AvailableChatsList() {
        java.util.List<String> users;
        try {
            users = clientMessagingService.getConnectedUsers();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        var chatListModel = new DefaultListModel<String>();
        for (var user : users) {
            chatListModel.addElement(user);
        }
        this.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        var chatList = new JList(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.add(chatList, constraints);
    }
}
