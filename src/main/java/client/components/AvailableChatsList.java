package client.components;

import client.ClientMessagingService;
import client.store.ChatsStore;
import client.store.UserStore;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AvailableChatsList extends JPanel {
    ClientMessagingService clientMessagingService = ClientMessagingService.getInstance();
    UserStore userStore = UserStore.getInstance();
    ChatsStore chatsStore = ChatsStore.getInstance();
    JComboBox<String> chatSelector = new JComboBox<>();
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


        constraints.gridy = 1;
        this.add(chatSelector, constraints);
        chatSelector.addActionListener((actionEvent) -> {
            var selectedIndex = chatSelector.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }
            var whoami = userStore.snapshotOnly(state -> state.nickname);
            var selectedChat = chatsStore.snapshotOnly(state -> state.chatList.stream().filter(nick -> !nick.equals(whoami)).toList().get(selectedIndex));
            var state = chatsStore.snapshot();
            state.selectedChat = selectedChat;
            System.out.println(state.chatList);
            chatsStore.setState(state);
        });
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

    public void updateChatList(java.util.List<String> users) {
        chatSelector.removeAllItems();
        for (var user : users) {
            if (user.equals(userStore.snapshotOnly(state -> state.nickname))) {
                continue;
            }
            chatSelector.addItem(user);
        }
    }
}
