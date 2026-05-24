import java.io.*;
import java.net.*;

public class ChatHandler implements Runnable {

    Socket socket;
    String username;
    DataInputStream reader;
    DataOutputStream writer;
    String zone = "PUBLIC";

    public ChatHandler(Socket socket, String username, DataInputStream reader, DataOutputStream writer) {
        this.socket = socket;
        this.username = username;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = reader.readUTF();

                // Broadcast message to everyone in the same room/zone
                for (ChatHandler client : Server.activeSessionList) {
                    if (client.zone.equals(this.zone)) {
                        client.writer.writeUTF("[" + zone + "] " + username + ": " + msg);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(username + " disconnected.");
            Server.activeSessionList.remove(this);
            try {
                socket.close();
            } catch (IOException ex) {}
        }
    }
}