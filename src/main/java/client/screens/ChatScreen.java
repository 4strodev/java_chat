package client.screens;

import client.ClientApp;
import client.ClientMessagingService;
import client.components.ChatPane;
import client.components.ChatSelector;
import client.store.UserStore;

import java.awt.*;

public class ChatScreen extends Component {
    ClientMessagingService clientMessaging = ClientMessagingService.getInstance();
    UserStore userStore = UserStore.getInstance();

    public ChatScreen() {
    }

    @Override
    public void onStart(ClientApp app) {
        // Establish messaggingConnection
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
        constraints.weighty = 0;
        constraints.weightx = 0;
        this.add(new ChatSelector(), constraints);

        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.weightx = 1;
        this.add(new ChatPane(), constraints);
    }

    @Override
    public void onDestroy(ClientApp app) {

    }
}
