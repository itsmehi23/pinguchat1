package fop.w11pchat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatClientTest {
    private static final int PORT = 3000;
    private static final String SERVER_ADDRESS = "localhost";
    private static final String USERNAME = "TestUser";
    private static final String MESSAGE = "Hello, world!";
    private static final String DM_MESSAGE = "@TestUser2 This is a direct message";
    private static final String DM_RESPONSE = "[DM] TestUser: This is a direct message";
    private static final String WHOIS_RESPONSE = "[SERVER] Currently connected clients: TestUser TestUser2 ";
    private static final String LOGOUT_MESSAGE = "[SERVER] TestUser has logged out";
    private static final String PINGU_MESSAGE = "[PINGU] Did you know that penguins have a gland above their eyes that filters salt from seawater?";
    private static final String UNKNOWN_USER_ERROR = "[SERVER] Unknown user: TestUser3";
    private static final String INVALID_COMMAND_ERROR = "[SERVER] Invalid command: FOO";

    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Scanner scanner;




    @Before
    public void setUp() throws IOException {
        clientSocket = new Socket(SERVER_ADDRESS, PORT);
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        scanner = new Scanner(System.in);
    }

    @Test
    public void testSendMessage() throws IOException, ClassNotFoundException {
        outputStream.writeObject(USERNAME);

        outputStream.writeObject(MESSAGE);
        String response = (String) inputStream.readObject();
        assertEquals(MESSAGE, response);
    }

    @Test
    public void testSendDirectMessage() throws IOException, ClassNotFoundException {
        outputStream.writeObject(USERNAME);

        outputStream.writeObject(DM_MESSAGE);
        String response = (String) inputStream.readObject();
        assertEquals(DM_RESPONSE, response);
    }

    @Test
    public void testSendDirectMessageToUnknownUser() throws IOException, ClassNotFoundException {
        outputStream.writeObject(USERNAME);

        outputStream.writeObject("@TestUser3 This is a direct message to an unknown user");
        String response = (String) inputStream.readObject();
        assertEquals(UNKNOWN_USER_ERROR, response);
    }


    @Test
    public void testWhoisCommand() throws IOException, ClassNotFoundException {

        outputStream.writeObject(USERNAME);

        outputStream.writeObject("WHOIS");
        String response = (String) inputStream.readObject();
        assertEquals(WHOIS_RESPONSE, response);
        }

    @Test
        public void testLogoutCommand() throws IOException, ClassNotFoundException {
            outputStream.writeObject(USERNAME);

            outputStream.writeObject("LOGOUT");
            String response = (String) inputStream.readObject();
            assertTrue(response.startsWith(LOGOUT_MESSAGE));
        }

        @Test
        public void testPinguCommand() throws IOException, ClassNotFoundException {
            outputStream.writeObject(USERNAME);

            outputStream.writeObject("PINGU");
            String response = (String) inputStream.readObject();
            assertEquals(PINGU_MESSAGE, response);
        }

        @After
        public void tearDown() throws IOException {
            clientSocket.close();
            scanner.close();
        }



    }
