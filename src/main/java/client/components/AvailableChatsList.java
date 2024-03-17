package client.components;

import client.ClientMessagingService;
import client.store.ChatsStore;
import client.store.UserStore;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AvailableChatsList extends JPanel {
    private JLabel nicknameLabel = new JLabel();
    public AvailableChatsList() {
        this.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;

        this.add(this.nicknameLabel, constraints);

        var chatList = new JList(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        chatList.setFixedCellHeight(50);


        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;        constraints.gridy = 1;
        this.add(chatList, constraints);
        this.onStart();
    }

    public void onStart() {
        userStore.selectOnly(state -> state.nickname).subscribe((nickname) -> this.nicknameLabel.setText(nickname));
        chatsStore.selectOnly(state -> state.chatList).subscribe(this::updateChatList);
        java.util.List<String> users;
        try {
            users = clientMessagingService.getConnectedUsers();
            var state = chatsStore.snapshot();
            state.setChatList(users);
            chatsStore.setState(state);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    ClientMessagingService clientMessagingService = ClientMessagingService.getInstance();
    UserStore userStore = UserStore.getInstance();
    ChatsStore chatsStore = ChatsStore.getInstance();
    DefaultListModel<String> chatListModel = new DefaultListModel<>();



    public void updateChatList(java.util.List<String> users) {
        chatListModel.clear();
        System.out.println("Chat list updated");
        for (var user : users) {
            if (user.equals(userStore.snapshotOnly(state -> state.nickname))) {
                continue;
            }
            chatListModel.addElement(user);
        }
    }
}
