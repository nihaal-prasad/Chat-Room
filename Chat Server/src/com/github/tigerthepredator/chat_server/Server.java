package com.github.tigerthepredator.chat_server;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

public class Server {
    private static Frame frame; // Frame to display information
    private static ServerSocket serverSocket; // Server socket that will be used to connect to the server
    private static ArrayList<Color> colors; // A list of colors that the client can be randomly assigned

    // TODO: Optionally authenticate using a password

    // Main method
    public static void main(String args[]) {
        try {
            // Ask for the port number
            int port = Integer.parseInt(JOptionPane.showInputDialog("Enter the port number: "));

            // Start the server using the given port number
            serverSocket = new ServerSocket(port);

            // Create the frame
            frame = new Frame();
            frame.setTitle("   TigerThePredator's Chat Server   ");
            frame.setSize(350, 350);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Display messages about connections
            frame.display("Setting up server connections on port " + serverSocket.getLocalPort() + "...",
                    Color.DARK_GRAY);

            // Setup colors arraylist
            colors = new ArrayList<Color>();

            // Keep waiting for connections while the server is running
            frame.display("Waiting for connections", Color.DARK_GRAY);
            while (!serverSocket.isClosed()) {
                // Wait for the next client to connect
                Socket clientSocket = serverSocket.accept();

                // Initialize a new connection
                Connection c = new Connection(clientSocket);
                
                // Send them the color information
                Color assigned = assignColor();
                c.send(assigned.getRed() + " " + assigned.getBlue() + " " + assigned.getGreen());
                
                // Start the new connection
                new Thread(c).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Displays a message on the frame
    public static void display(String message, Color c) {
        frame.display(message, c);
    }

    // Broadcasts a message to every connection
    public static void broadcast(String message, Color c) {
        Connection.broadcast(message, c);
    }

    // Randomly assigns a color to a newly connected client
    // Makes sure that the same color cannot be assigned twice
    private static Color assignColor() {
        // If all of the colors have been assigned, recreate the list of colors
        if (colors.size() <= 0) {
            // Add all of the possible colors that the clients can be assigned
            colors.add(Color.BLUE);
            colors.add(Color.CYAN);
            colors.add(Color.GRAY);
            colors.add(Color.GREEN);
            colors.add(Color.MAGENTA);
            colors.add(Color.ORANGE);
            colors.add(Color.PINK);
            colors.add(Color.YELLOW);
            colors.add(new Color(137, 2, 121)); // Purple
            colors.add(new Color(137, 2, 56)); // Dark pink
            colors.add(new Color(127, 0, 27)); // Dark red
            colors.add(new Color(109, 36, 0)); // Brown
            colors.add(new Color(255, 176, 137)); // Light pink
            colors.add(new Color(86, 63, 51)); // Dark brown
            colors.add(new Color(127, 255, 0)); // Light grassy green
            colors.add(new Color(0, 255, 153)); // Blue green
            colors.add(new Color(70, 92, 127)); // Night blue
            colors.add(new Color(106, 0, 255)); // Bluish purple
            colors.add(new Color(61, 28, 107)); // Night purple
        }

        // Randomly select a color and then make sure that it cannot be assigned twice
        Random r = new Random();
        int index = r.nextInt(colors.size());
        Color c = colors.get(index);
        colors.remove(c);
        return c;
    }
}
