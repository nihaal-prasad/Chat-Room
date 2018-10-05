package chat_client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {
    private static Socket SOCKET; // Socket for connecting to server
    private static Frame frame; // Frame for displaying GUI
    private static PrintWriter out; // Used to send data to server
    private static BufferedReader in; // Used to read data from server
    private static Encryptor encryptor; // Used to encrypt/decrypt data

    // Main method
    public static void main(String args[]) {
        try {
            // Ask for information about the server
            String ip = JOptionPane.showInputDialog("Enter the server's IP address:");
            int port = Integer.parseInt(JOptionPane.showInputDialog("Enter the port number:"));

            // Get the username
            String username = JOptionPane.showInputDialog("Enter your username:");

            // Create the frame
            frame = new Frame(username);
            frame.setTitle("   Chat Client   ");
            frame.setSize(350, 350);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.display("Setting up server connections on " + ip + ":" + port, Color.DARK_GRAY);

            // Create the encryptor
            encryptor = new Encryptor();

            // Setup the socket and I/O streams
            SOCKET = new Socket(ip, port);
            out = new PrintWriter(SOCKET.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));

            // Check if the client has connected
            if (SOCKET.isConnected()) {
                // Print out the connection status
                frame.display("Successfully connected to server at " + ip + ":" + port, Color.DARK_GRAY);

                // Receive the server's public key
                String serverPublicKey = receiveUnencrypted(); // Receive the client's public key
                encryptor.generatePublicKey(serverPublicKey);
                sendUnencrypted(encryptor.getPublicKey()); // Send our public key to the server
                encryptor.generateSharedKey(serverPublicKey); // Calculate the shared key

                // Receive our random color assignment from the server
                String[] assignedColorString = receive().split(" ");
                Color assignedColor = new Color(Integer.parseInt(assignedColorString[0]),
                        Integer.parseInt(assignedColorString[1]), Integer.parseInt(assignedColorString[2]));
                frame.setUserColor(assignedColor);
                
                // Send our username
                send(username, assignedColor);

                // Wait for a message to be received
                while (SOCKET.isConnected()) {
                    String data = receive();
                    Color c = new Color(Integer.parseInt(data.split(" ")[0]), Integer.parseInt(data.split(" ")[1]),
                            Integer.parseInt(data.split(" ")[2]));
                    String message = data.substring(data.indexOf(data.split(" ")[3]));
                    System.out.println(data);
                    System.out.println(message);
                    frame.display(message, c);
                }
            } else
                // State that the client is unable to connect to the server
                frame.display("Unable to connect to server at " + ip + ":" + port, Color.DARK_GRAY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Receives and decrypts a message from the server
    public static String receive() throws IOException {
        // Wait until the reader is ready
        String line;
        while ((line = in.readLine()) == null) {
            /* Do nothing until a line is read */}

        // Return the decrypted line
        return encryptor.decrypt(line);
    }

    // Sends a message to the server
    public static void send(String message, Color c) {
        String plaintext = c.getRed() + " " + c.getBlue() + " " + c.getGreen() + " " + message;
        out.println(encryptor.encrypt(plaintext));
    }

    // Send an unencrypted message
    public static void sendUnencrypted(String message) {
        out.println(message);
        out.flush();
    }

    // Receive an unencrypted message
    private static String receiveUnencrypted() throws IOException {
        // Wait until the reader is ready
        String line;
        while ((line = in.readLine()) == null) {
            /* Do nothing until a line is read */}

        // Return the decrypted line
        return line;
    }
}
