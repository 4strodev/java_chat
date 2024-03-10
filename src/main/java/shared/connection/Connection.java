package shared.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Connection {
    public final Socket socket;
    public final ObjectInputStream in;
    public final ObjectOutputStream out;

    public Connection(Socket socket) throws Exception {
        this.socket = socket;
        this.out = this.getObjectOutputStream();
        if (out == null) {
            throw new Exception("Error getting output stream");
        }
        this.in = this.getObjectInputStream();
        if (in == null) {
            throw new Exception("Error getting input stream");
        }
    }

    public String netAddress() {
        return this.socket.getInetAddress().getHostAddress();
    }

    public ObjectOutputStream getObjectOutputStream() {
        try {
            return new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            return null;
        }
    }

    public ObjectInputStream getObjectInputStream() {
        try {
            return new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void send(Object object) throws IOException {
        this.out.writeObject(object);
        this.out.flush();
    }

    public <T> T read() throws IOException, ClassNotFoundException {
        return (T) this.in.readObject();
    }

    public void close() {
        System.out.println("Closing socket");
        try {
            this.out.close();
            this.in.close();
            this.socket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
