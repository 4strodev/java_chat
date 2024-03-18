package server;

import com.formdev.flatlaf.FlatLightLaf;
import shared.messages.MessagePacketData;
import shared.messages.MessageType;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class ServerGUI extends JFrame {

    private final JTextArea areatext;
    private final Server server;
    private final JTextField alertMessageInput = new JTextField();
    private final JButton alertButton = new JButton("Send alert");

    public ServerGUI(Server server) {
        this.server = server;

        // Setting theme
        FlatLightLaf.setup();
        // Set the window size
        setBounds(1200, 300, 280, 350);
        // Creating the panel where the UI elements will be drawn
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();

        // Creating the text area where the messages will be printed
        areatext = new JTextArea();
        areatext.setEditable(false);
        // Adding text area to panel
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.gridx = 0;
        contentPanel.add(areatext, constraints);


        constraints.weighty = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(alertMessageInput, constraints);

        constraints.gridy = 2;
        contentPanel.add(alertButton, constraints);

        alertButton.addActionListener((event) -> {
            server.broadcast(new MessagePacketData(MessageType.SERVER_ALERT, this.alertMessageInput.getText()));
        });

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
