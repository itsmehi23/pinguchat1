package fop.w11pchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int DEFAULT_PORT = 3000;
    private static final int MAX_CLIENTS = 50;
    private  ServerSocket serverSocket;
    private Socket socket;

    private List<ClientThread> clientThreads;

    public ChatServer() {
        this(DEFAULT_PORT);
    }

    public ChatServer(int DEFAULT_PORT) {
        // write about server//

        clientThreads = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println("Chat server started on port " + DEFAULT_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThreads.add(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private class ClientThread extends Thread {
        //private Socket socket;//
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private String username;

        public ClientThread(Socket clientSocket) {

        }

        public void run() {
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());

                // Read the first message from the client, which should be the username
                username = (String) inputStream.readObject();
                System.out.println(username + " has joined the chat");

                // Send a message to all clients that a new user has joined
                sendMessageToAll("[SERVER] " + username + " has joined the chat");

                while (true) {
                    String message = (String) inputStream.readObject();
                    if (message.startsWith("@")) {
                        // This is a direct message to a specific user
                        String[] parts = message.split("\\s", 2);
                        String recipient = parts[0].substring(1);
                        String messageBody = parts[1];
                        sendMessageToUser(recipient, "[DM] " + username + ": " + messageBody);
                    } else if (message.equals("WHOIS")) {
                        // Client is requesting a list of currently connected clients
                        StringBuilder sb = new StringBuilder();
                        sb.append("[SERVER] Currently connected clients: ");
                        for (ClientThread clientThread : clientThreads) {
                            sb.append(clientThread.username + " ");
                        }
                        sendMessageToUser(username, sb.toString());
                    } else if (message.equals("LOGOUT")) {
                        // Client is requesting to log out
                        System.out.println(username + " has logged out");
                        sendMessageToAll("[SERVER] " + username + " has logged out");
                        break;
                    } else if (message.equals("PINGU")) {
                        // Client is sending a "PINGU" message to all clients
                        sendMessageToAll("[PINGU] Did you know that penguins have a gland above their eyes that filters salt from seawater?");
                    } else {
                        // This is a regular message to all clients
                        sendMessageToAll("[CHAT] " + username + ": " + message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // Connection to client was lost
                System.out.println(username + " has disconnected");
                sendMessageToAll("[SERVER] " + username + " has disconnected");
            } finally {
                clientThreads.remove(this);
                close();
            }
        }

        public void sendMessageToAll(String message) {
            for (ClientThread clientThread : clientThreads) {
                if (clientThread != this) {
                    clientThread.sendMessage(message);
                }
            }
        }

        public void sendMessageToUser(String recipient, String message) {
            for (ClientThread clientThread : clientThreads) {
                if (clientThread.username.equals(recipient)) {
                    clientThread.sendMessage(message);
                    return;
                }
            }
            // If we reach here, it means the recipient was not found
            sendMessage("[SERVER] No user with the username '" + recipient + "' was found");
        }

        public void sendMessage(String message) {
            try {
                outputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(DEFAULT_PORT);
        server.socket = server.serverSocket.accept();
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            server = new ChatServer(DEFAULT_PORT);
        }
        server.start();
    }
}


    
