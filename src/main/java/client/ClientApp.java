package client;

import client.screens.Component;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ClientApp {
    private JFrame window;
    private Component currentScreen;

    public ClientApp() {
        FlatLightLaf.setup();
    }

    public void init() {
        this.window = new JFrame("Client chat");
        this.window.setSize(1600, 900);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLocationRelativeTo(null);
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
