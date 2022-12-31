package fop.w11pchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_SERVER_PORT = 3000;

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Scanner scanner;

    public ChatClient() {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
    }

    public ChatClient(String serverAddress,int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            connectToServer();
            startMessageThread();
            startInputThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    private void startMessageThread() {
        Thread messageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = (String) inputStream.readObject();
                        System.out.println(message);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        messageThread.start();
    }

    private void startInputThread() {
        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String message = scanner.nextLine();
                    try {
                        outputStream.writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        inputThread.start();
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        if (args.length == 2) {
            String serverAddress = args[0];
            int serverPort = Integer.parseInt(args[1]);
            client = new ChatClient(serverAddress, serverPort);
        } else if (args.length == 1) {
            int serverPort = Integer.parseInt(args[0]);
            client = new ChatClient(DEFAULT_SERVER_ADDRESS, serverPort);
        }
        client.start();
    }
}

