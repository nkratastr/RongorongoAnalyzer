package com.example.rongorongo.service;

import com.example.rongorongo.Interface.IService;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

public class Service implements IService {



    private static final int FONT_SIZE = 50;
    private static final String FONT_PATH = "C:/Users/Laptop/Documents/java_projects/rongorongofont/rongorongo.ttf";
    private Font rongorongoFont;

    @Override
    public void initialize() throws Exception {
        rongorongoFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH)).deriveFont(Float.valueOf(FONT_SIZE));
    }
    @Override
    public Map<Character, Integer> performFrequencyAnalysis(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }



    @Override
    public double calculateIOC(String text) {
        Map<Character, Integer> frequencyMap = performFrequencyAnalysis(text);
        int totalChars = text.length();
        double sum = 0;

        for (int frequency : frequencyMap.values()) {
            sum += (frequency * (frequency - 1));
        }

        double ioc = totalChars > 1 ? sum / (totalChars * (totalChars - 1)) : 0;
        System.out.println("Service - IOC calculation: sum = " + sum + ", totalChars = " + totalChars + ", IOC = " + ioc);
        return ioc;
    }

    @Override
    public String renderGlyphToAscii(char c) {
        BufferedImage image = new BufferedImage(FONT_SIZE, FONT_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(rongorongoFont);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(String.valueOf(c), 0, fm.getAscent());
        g2d.dispose();

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < FONT_SIZE; y++) {
            for (int x = 0; x < FONT_SIZE; x++) {
                sb.append((image.getRGB(x, y) & 0xFF000000) != 0 ? "█" : " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void saveFrequencyChart(Map<Character, Integer> frequencyMap, String outputPath) {
        List<Map.Entry<Character, Integer>> list = new ArrayList<>(frequencyMap.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        int width = FONT_SIZE * 10;
        int height = FONT_SIZE * Math.min(10, list.size());
        BufferedImage chart = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = chart.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(rongorongoFont);

        for (int i = 0; i < Math.min(10, list.size()); i++) {
            char c = list.get(i).getKey();
            g2d.drawString(String.valueOf(c), i * FONT_SIZE, FONT_SIZE);
        }

        g2d.dispose();

        try {
            ImageIO.write(chart, "png", new File(outputPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String analyzeText(String text) {
        StringBuilder result = new StringBuilder();
        Map<Character, Integer> frequencyMap = performFrequencyAnalysis(text);
        double ioc = calculateIOC(text);

        result.append("Frequency Analysis:\n");
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            result.append(renderGlyphToAscii(entry.getKey())).append(": ").append(entry.getValue()).append("\n");
        }

        result.append("\nIndex of Coincidence: ").append(ioc).append("\n");

        saveFrequencyChart(frequencyMap, "frequency_chart.png");
        result.append("\nFrequency chart saved as 'frequency_chart.png'\n");

        return result.toString();
    }
}