package com.github.tigerthepredator.chat_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Frame extends JFrame {
    // IDK why I need this, but eclipse gives me a warning if I don't have it
    private static final long serialVersionUID = 4185788286557260974L;

    private File log; // The log file will contain all the auditing information
    private PrintWriter logWriter; // Print writer for the log file

    private JPanel panel; // Panel for everything
    private JTextPane messages; // Text pane for all of the chat messages
    private JTextField typeField; // Text field for typing out messages

    // Constructor
    public Frame() {
        // Initialize the messages component
        messages = new JTextPane();
        messages.setEditable(false);

        // Initialize the typeField component
        typeField = new JTextField();
        typeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!typeField.getText().equals("")) {
                    // Get the text, display it, and broadcast it
                    String text = "Server> " + typeField.getText();
                    display(text, Color.RED);
                    Server.broadcast(text, Color.RED);

                    // Erase the text
                    typeField.setText("");
                }
            }
        });

        // Initialize our panel and add our components
        panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(messages), BorderLayout.CENTER);
        panel.add(typeField, BorderLayout.SOUTH);

        // Add our panel
        add(panel);
        
        try {
            // Initialize the log file
            log = File.createTempFile("chat_server", ".log");
            logWriter = new PrintWriter(log);
            display(("Log file created at " + log.getAbsolutePath()), Color.DARK_GRAY);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    // Displays something onto the text area
    public void display(String message, Color c) {
        // Get the caret position of the text pane
        int caret = messages.getDocument().getLength();

        // Get the style context
        StyleContext sc = StyleContext.getDefaultStyleContext();

        // Create the attribute set
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        messages.setCharacterAttributes(aset, false);

        // Append the message
        try {
            messages.getDocument().insertString(caret, message + "\n", aset);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        // Log whatever was displayed
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        logWriter.println(message + "\n@ " + df.format(dateobj));
        logWriter.println();
        logWriter.flush();
    }
}
