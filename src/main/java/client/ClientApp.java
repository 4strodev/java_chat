package client;

import client.screens.Component;
import client.store.ChatsStore;
import com.formdev.flatlaf.FlatLightLaf;
import server.message.MessagePacketData;

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
                case MessagePacketData.SERVER_USER_LIST_UPDATED -> {
                    System.out.println("User list updated");
                    var state = this.chatsStore.snapshot();
                    ArrayList<String> chatList = (ArrayList<String>) messagePacketData.data();
                    state.setChatList(chatList);
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
