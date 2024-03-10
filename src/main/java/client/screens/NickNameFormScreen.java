package client.screens;

import client.ClientApp;
import client.store.UserStore;

import javax.swing.*;
import java.awt.*;

public class NickNameFormScreen extends JPanel implements ScreenStart {
    UserStore userStore = UserStore.getInstance();

    private JTextField nickNameInputField = new JTextField();
    private JButton confirmButton = new JButton();

    public NickNameFormScreen() {
        this.setLayout(new FlowLayout());
        this.add(this.nickNameInputField);
        this.nickNameInputField.setColumns(20);

        this.add(this.confirmButton);
        this.confirmButton.setText("Next");
    }

    @Override
    public void onStart(ClientApp app) {
        app.getWindow().setSize(800, 800);
        app.getWindow().setLocationRelativeTo(null);

        this.confirmButton.addActionListener(e -> {
            if (!this.nickNameInputField.getText().isBlank()) {
                userStore.setNickname(this.nickNameInputField.getText());
                app.setCurrentScreen(new ChatScreen());
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid name",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }
}
