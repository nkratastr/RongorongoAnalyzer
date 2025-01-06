package com.example.rongorongo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class RongorongoViewer extends JFrame {
    private Font rongorongoFont;

    public RongorongoViewer() {
        setTitle("Rongorongo Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        try {
            rongorongoFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:/Users/Laptop/Documents/java_projects/rongorongofont/rongorongo.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(rongorongoFont);

            // Create a text area with the Rongorongo font
            JTextArea textArea = new JTextArea();
            textArea.setFont(rongorongoFont.deriveFont(Font.PLAIN, 48));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            // Add the text area to a scroll pane
            JScrollPane scrollPane = new JScrollPane(textArea);
            add(scrollPane, BorderLayout.CENTER);

            // Add a label with instructions
            JLabel label = new JLabel("Type here to use Rongorongo font:");
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            add(label, BorderLayout.NORTH);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading Rongorongo font");
            add(errorLabel, BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RongorongoViewer viewer = new RongorongoViewer();
            viewer.setVisible(true);
        });
    }
}
