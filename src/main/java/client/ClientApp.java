package client;

import client.screens.ScreenStart;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ClientApp {
    private JFrame window;
    private JPanel currentScreen;

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

    public void applyLifeCycleHooks(JPanel panel) {
        if (panel instanceof ScreenStart) {
            ((ScreenStart) panel).onStart(this);
        }
    }

    public JFrame getWindow() {
        return this.window;
    }

    public void setCurrentScreen(JPanel panel) {
        this.window.getContentPane().removeAll();
        this.window.getContentPane().add(panel);
        this.currentScreen = panel;
        this.applyLifeCycleHooks(this.currentScreen);
        this.window.revalidate();
    }
}
