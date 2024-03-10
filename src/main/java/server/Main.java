package server;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Creating a new instance of server.MarcServidor that inherits from JFrame
        // this creates a new window. Also, it creates a thread that executes
        // the server logic
        ServerGUI marc = new ServerGUI(new Server(8));
        marc.startServer();
        // When the window is closed then exit program
        marc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

