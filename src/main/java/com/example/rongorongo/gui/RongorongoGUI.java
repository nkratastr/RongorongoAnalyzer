package com.example.rongorongo.gui;

import com.example.rongorongo.Interface.IService;
import com.example.rongorongo.service.Service;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RongorongoGUI extends JFrame {
    private JTextArea inputArea;
    private JTextArea symbolResultArea;
    private JTextArea frequencyResultArea;
    private JButton analyzeButton;
    private IService service;
    private Font rongorongoFont;
    private Font defaultFont;
    private static final String FONT_PATH = "src/main/resources/com/example/rongorongo/rongorongo.ttf";

    public RongorongoGUI() {
        service = new Service();
        try {
            service.initialize();
            loadRongorongoFont();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to initialize: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        defaultFont = new Font("Arial", Font.PLAIN, 36);

        setTitle("Rongorongo Analyzer");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputArea = new JTextArea();
        inputArea.setFont(rongorongoFont.deriveFont(60f));

        symbolResultArea = new JTextArea();
        symbolResultArea.setEditable(false);
        symbolResultArea.setFont(rongorongoFont.deriveFont(60f));

        frequencyResultArea = new JTextArea();
        frequencyResultArea.setEditable(false);
        frequencyResultArea.setFont(defaultFont);

        analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(e -> performAnalysis());

        JScrollPane inputScroll = new JScrollPane(inputArea);
        JScrollPane symbolResultScroll = new JScrollPane(symbolResultArea);
        JScrollPane frequencyResultScroll = new JScrollPane(frequencyResultArea);

        JSplitPane resultSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, symbolResultScroll, frequencyResultScroll);
        resultSplitPane.setResizeWeight(0.5);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, resultSplitPane);
        mainSplitPane.setResizeWeight(0.5);

        add(mainSplitPane, BorderLayout.CENTER);
        add(analyzeButton, BorderLayout.SOUTH);
    }

    private void loadRongorongoFont() {
        try {
            rongorongoFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(rongorongoFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load Rongorongo font: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performAnalysis() {
        String inputText = inputArea.getText();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to analyze.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("GUI - Input text: " + inputText);
        System.out.println("GUI - Input length: " + inputText.length());

        Map<Character, Integer> frequencyMap = service.performFrequencyAnalysis(inputText);
        double ioc = service.calculateIOC(inputText);

        System.out.println("GUI - Frequency map: " + frequencyMap);
        System.out.println("GUI - IOC: " + ioc);

        int totalChars = inputText.length();

        StringBuilder symbolResult = new StringBuilder();
        StringBuilder frequencyResult = new StringBuilder();

        frequencyResult.append("Frequency Analysis:\n\n");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        DecimalFormat dfPercentage = new DecimalFormat("00.00", symbols);
        DecimalFormat dfIOC = new DecimalFormat("0.0000", symbols);

        List<Map.Entry<Character, Integer>> sortedEntries = frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        int count = 1;
        double totalPercentage = 0;
        for (Map.Entry<Character, Integer> entry : sortedEntries) {
            char symbol = entry.getKey();
            int frequency = entry.getValue();
            double percentage = (double) frequency / totalChars * 100;
            totalPercentage += percentage;

            symbolResult.append(String.format("%c\n", symbol));
            frequencyResult.append(String.format("%d ---------> %s%%\n", count, dfPercentage.format(percentage)));
            count++;

            System.out.println("GUI - Symbol: " + symbol + ", Frequency: " + frequency + ", Percentage: " + dfPercentage.format(percentage) + "%");
        }

        System.out.println("GUI - Total percentage: " + dfPercentage.format(totalPercentage) + "%");

        frequencyResult.append(String.format("\nIndex of Coincidence: %s\n", dfIOC.format(ioc)));

        symbolResultArea.setText(symbolResult.toString());
        frequencyResultArea.setText(frequencyResult.toString());
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RongorongoGUI gui = new RongorongoGUI();
            gui.setVisible(true);
        });
    }
}