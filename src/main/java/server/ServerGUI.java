package server;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class ServerGUI extends JFrame {

    private final JTextArea areatext;
    private final Server server;

    public ServerGUI(Server server) {
        this.server = server;


        // Setting theme
        FlatLightLaf.setup();
        // Set the window size
        setBounds(1200, 300, 280, 350);
        // Creating the panel where the UI elements will be drawn
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        // Creating the text area where the messages will be printed
        areatext = new JTextArea();
        areatext.setEditable(false);
        // Adding text area to panel
        contentPanel.add(areatext, BorderLayout.CENTER);
        // Adding panel to frame
        add(contentPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Setting window visible
        setVisible(true);

        this.server.setLogger((String message) -> this.areatext.append("LOG: " + message + "\n"));
    }

    /**
     * Starts the server messageType service and handling connections
     * @throws IOException
     */
    public void startServer() {
        this.server.init();
        this.server.start();
    }
}
