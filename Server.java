import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static ArrayList<ChatHandler> activeSessionList = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Chat Server Started on Port 5000...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                DataInputStream reader = new DataInputStream(socket.getInputStream());
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

                String username = reader.readUTF();

                ChatHandler handler = new ChatHandler(socket, username, reader, writer);
                activeSessionList.add(handler);

                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}