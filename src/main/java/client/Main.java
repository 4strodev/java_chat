package client;

import client.screens.NickNameFormScreen;

public class Main {
    public static void main(String[] args) {
        ClientApp app = new ClientApp();
        app.init();
        app.setCurrentScreen(new NickNameFormScreen());
        app.start();
    }
}
