package fop.w11pchat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ChatServerTest {
    private static final int PORT = 3000;
    private static final String USERNAME = "TestUser";
    private static final String MESSAGE = "Hello, world!";
    private static final String DM_MESSAGE = "@TestUser2 This is a direct message";
    private static final String DM_RESPONSE = "[DM] TestUser: This is a direct message";
    private static final String WHOIS_RESPONSE = "[SERVER] Currently connected clients: TestUser TestUser2 ";
    private static final String LOGOUT_MESSAGE = "[SERVER] TestUser has logged out";
    private static final String PINGU_MESSAGE = "[PINGU] Did you know that penguins have a gland above their eyes that filters salt from seawater?";
    private static final String UNKNOWN_USER_ERROR = "[SERVER] Unknown user: TestUser3";
    private static final String INVALID_COMMAND_ERROR = "[SERVER] Invalid command: FOO";

    private ChatServer server;
    private Socket clientSocket1;
    private Socket clientSocket2;
    private ObjectOutputStream outputStream1;
    private ObjectOutputStream outputStream2;
    private ObjectInputStream inputStream1;
    private ObjectInputStream inputStream2;

    @Before
    public void setUp() throws IOException {
        server = new ChatServer(PORT);
        server.start();

        clientSocket1 = new Socket("localhost", PORT);
        clientSocket2 = new Socket("localhost", PORT);
        outputStream1 = new ObjectOutputStream(clientSocket1.getOutputStream());
        outputStream2 = new ObjectOutputStream(clientSocket2.getOutputStream());
        inputStream1 = new ObjectInputStream(clientSocket1.getInputStream());
        inputStream2 = new ObjectInputStream(clientSocket2.getInputStream());
    }

    @Test
    public void testSendMessage() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);
        outputStream2.writeObject(USERNAME + "2");

        outputStream1.writeObject(MESSAGE);
        String response = (String) inputStream1.readObject();
        assertEquals(MESSAGE, response);
        response = (String) inputStream2.readObject();
        assertEquals(MESSAGE, response);
    }


    @Test
    public void testSendDirectMessage() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);
        outputStream2.writeObject(USERNAME + "2");

        outputStream1.writeObject(DM_MESSAGE);
        String response = (String) inputStream1.readObject();
        assertEquals(DM_RESPONSE, response);
        response = (String) inputStream2.readObject();
        assertEquals(DM_RESPONSE, response);
    }

    @After
    public void tearDown() throws IOException {
        clientSocket1.close();
        clientSocket2.close();


    }




    @Test
    public void testSendDirectMessageToUnknownUser() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);

        outputStream1.writeObject("@TestUser3 This is a direct message to an unknown user");
        String response = (String) inputStream1.readObject();
        assertEquals(UNKNOWN_USER_ERROR, response);
    }

    @Test
    public void testWhoisCommand() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);
        outputStream2.writeObject(USERNAME + "2");

        outputStream1.writeObject("WHOIS");
        String response = (String) inputStream1.readObject();
        assertEquals(WHOIS_RESPONSE, response);
    }

    @Test
    public void testLogoutCommand() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);
        outputStream2.writeObject(USERNAME + "2");

        outputStream1.writeObject("LOGOUT");
        String response = (String) inputStream2.readObject();
        assertEquals(LOGOUT_MESSAGE, response);
    }

    @Test
    public void testPinguCommand() throws IOException, ClassNotFoundException {
        outputStream1.writeObject(USERNAME);
        outputStream2.writeObject(USERNAME + "2");

        outputStream1.writeObject("PINGU");
        String response = (String) inputStream1.readObject();
        assertEquals(PINGU_MESSAGE, response);
        response = (String) inputStream2.readObject();
        assertEquals(PINGU_MESSAGE, response);
    }

}



