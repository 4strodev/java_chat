package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseWindowHandler extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        ClientMessagingService.getInstance().disconnect();
    }
}
