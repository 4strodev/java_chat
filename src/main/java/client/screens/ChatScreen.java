package client.screens;

import client.ClientApp;
import client.ClientMessagingService;
import client.components.AvailableChatsList;
import client.components.ChatPane;
import client.store.UserStore;

import javax.swing.*;
import java.awt.*;

public class ChatScreen extends JPanel implements ScreenStart {
    ClientMessagingService clientMessaging = ClientMessagingService.getInstance();
    UserStore userStore = UserStore.getInstance();

    public ChatScreen() {
    }

    @Override
    public void onStart(ClientApp app) {
        // Establish connection
        try {
            var connected = clientMessaging.connect(userStore.snapshotOnly((state) -> state.nickname));
            if (!connected) {
                app.setCurrentScreen(new NickNameFormScreen());
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        this.setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.weightx = 1;

        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(.5);
        splitPane.add(new AvailableChatsList());
        splitPane.add(new ChatPane());

        this.add(splitPane, constraints);

        JOptionPane.showMessageDialog(this, "Connected successfully!");
    }
}
