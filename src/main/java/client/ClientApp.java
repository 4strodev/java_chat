package client;

import client.screens.Component;
import client.store.ChatsStore;
import com.formdev.flatlaf.FlatLightLaf;
import shared.messages.BroadcastMessageData;
import shared.messages.MessagePacketData;
import shared.messages.MessageType;
import shared.messages.SendMessageData;

import javax.swing.*;
import java.util.ArrayList;

public class ClientApp {
    private JFrame window;
    private Component currentScreen;
    private ChatsStore chatsStore = ChatsStore.getInstance();

    public ClientApp() {
        FlatLightLaf.setup();
    }

    public void init() {
        this.window = new JFrame("Client chat");
        this.window.setSize(1600, 900);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLocationRelativeTo(null);

        ClientMessagingService.getInstance().onMessage((connection, messagePacketData) -> {
            switch (messagePacketData.messageType()) {
                case MessageType.SERVER_USER_LIST_UPDATED -> {
                    var state = this.chatsStore.snapshot();
                    ArrayList<String> chatList = (ArrayList<String>) messagePacketData.data();
                    state.setChatList(chatList);
                    this.chatsStore.setState(state);
                }
                case MessageType.SERVER_NEW_INCOMING_MESSAGE -> {
                    var messageData = (SendMessageData) messagePacketData.data();
                    var state = ChatsStore.getInstance().snapshot();
                    state.addMessage(messageData.sender(), messageData.sender() + ": " + messageData.message());
                    this.chatsStore.setState(state);
                }
                case MessageType.SERVER_NEW_BROADCAST_MESSAGE -> {
                    var messageData = (BroadcastMessageData) messagePacketData.data();
                    var state = ChatsStore.getInstance().snapshot();
                    state.addMessage(messageData.sender(), messageData.sender() + ": " + messageData.message());
                    this.chatsStore.setState(state);
                }
            }
        });
    }

    public void start() {
        this.window.setVisible(true);
    }

    public JFrame getWindow() {
        return this.window;
    }

    public void setCurrentScreen(Component panel) {
        if (this.currentScreen != null) {
            this.currentScreen.onDestroy(this);
        }
        this.currentScreen = panel;
        this.window.setContentPane(panel);
        this.currentScreen.onStart(this);
        this.window.revalidate();
    }
}
