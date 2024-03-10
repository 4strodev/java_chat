package client.screens;

import client.ClientApp;

import javax.swing.*;

public abstract class Component extends JPanel {
    public abstract void onStart(ClientApp app);

    public abstract void onDestroy(ClientApp app);
}
