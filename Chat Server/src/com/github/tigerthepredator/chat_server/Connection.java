package com.github.tigerthepredator.chat_server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Connection implements Runnable {
    private static ArrayList<Connection> connections = new ArrayList<Connection>(); // A list of all of the connections
    private final Socket SOCKET; // Socket connection
    private Encryptor encryptor; // Used for encryption
    private PrintWriter out; // Used to send data to client
    private BufferedReader in; // Used to read data from client
    private boolean running; // Used to determine whether this connection is running or not

    // TODO: Add encryption
    // TODO: Associate each connection with a particular color

    // Constructor
    public Connection(Socket s) throws IOException {
        // Broadcast a message to everyone
        broadcast("Server> Client from " + s.getInetAddress() + " is attempting to connect.", Color.RED);
        Server.display("Server> Client from " + s.getInetAddress() + " is attempting to connect.", Color.RED);

        // Set the socket
        SOCKET = s;

        // Create I/O streams
        out = new PrintWriter(SOCKET.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));

        // Setup encryptor do key exchange
        encryptor = new Encryptor();
        sendUnencrypted(encryptor.getPublicKey()); // Send our public key to the client
        String clientPublicKey = receiveUnencrypted(); // Receive the client's public key
        encryptor.generateSharedKey(clientPublicKey); // Calculate the shared key

        // Add this connection
        connections.add(this);

        // Set running equal to true
        running = true;
    }

    @Override
    public void run() {
        try {
            // Broadcast their name to the chat group
            String nameData = receive();
            Color c1 = new Color(Integer.parseInt(nameData.split(" ")[0]), Integer.parseInt(nameData.split(" ")[1]),
                    Integer.parseInt(nameData.split(" ")[2]));
            String name = nameData.substring(nameData.indexOf(nameData.split(" ")[3]));
            Server.display(name + " has joined the chat room from " + SOCKET.getInetAddress() + ".", c1);
            broadcast(name + " has joined the chat room from " + SOCKET.getInetAddress() + ".", c1);
            
            // While this is running, broadcast any message that has been received
            while (running) {
                // Receive the data
                String data = receive();

                // Split up data (data should be split into message data and color data)
                // The color data is the first three numbers, which represent RGB values
                // The rest of the data is the message data
                Color c = new Color(Integer.parseInt(data.split(" ")[0]), Integer.parseInt(data.split(" ")[1]),
                        Integer.parseInt(data.split(" ")[2]));
                String message = data.substring(data.indexOf(data.split(" ")[3]));

                Server.display(message, c);
                broadcast(message, c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Encrypt and send a message
    public void send(String message) {
        out.println(encryptor.encrypt(message));
        out.flush();
    }

    // Send an unencrypted message
    public void sendUnencrypted(String message) {
        out.println(message);
        out.flush();
    }

    // Receive and decrypt a message
    private String receive() throws IOException {
        // Wait until the reader is ready
        String line;
        while ((line = in.readLine()) == null) {
            /* Do nothing until a line is read */}

        // Return the decrypted line
        return encryptor.decrypt(line);
    }

    // Receive an unencrypted message
    private String receiveUnencrypted() throws IOException {
        // Wait until the reader is ready
        String line;
        while ((line = in.readLine()) == null) {
            /* Do nothing until a line is read */}

        // Return the decrypted line
        return line;
    }

    // Broadcasts to every single connection
    public static void broadcast(String message, Color color) {
        for (Connection c : connections)
            c.send(color.getRed() + " " + color.getBlue() + " " + color.getGreen() + " " + message);
    }
}
