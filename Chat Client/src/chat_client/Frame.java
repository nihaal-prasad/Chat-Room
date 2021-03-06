package chat_client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private JPanel panel; // Panel for everything
    private JTextPane messages; // Text pane for all of the chat messages
    private JTextField typeField; // Text field for typing out messages
    private Color userColor; // The user's specific color

    // Constructor
    public Frame(String username) {
        // Initialize the messages component
        messages = new JTextPane();
        messages.setEditable(false);

        // Initialize the typeField component
        typeField = new JTextField();
        typeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!typeField.getText().equals("")) {
                    // Get the text, display it, and broadcast it
                    String text = username + "> " + typeField.getText();
                    Client.send(text, userColor);

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
    }

    // Change the color of the user
    public void setUserColor(Color c) {
        userColor = c;
    }
}
