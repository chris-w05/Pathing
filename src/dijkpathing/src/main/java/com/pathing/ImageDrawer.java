package com.pathing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageDrawer {

    public static void drawLineOnImage(String inputImagePath, String outputImagePath, int x1, int y1, int x2, int y2, Color color) throws IOException {
        // Read the image
        InputStream inputStream = ImgRead.class.getResourceAsStream("/" + inputImagePath); // Notice the leading slash
        if (inputStream == null) {
            throw new IOException("File not found: " + inputImagePath);
        }
        BufferedImage image = ImageIO.read(inputStream);

        // Get graphics object from the image
        Graphics2D g2d = image.createGraphics();

        // Set line color
        g2d.setColor(color);

        // Draw line on the image
        g2d.drawLine(x1, y1, x2, y2);

        // Dispose the graphics object
        g2d.dispose();

        // Write the modified image to the output file

        try{
            // Get the resources directory path
            String resourcesPath = "src/main/resources/";

            // Save the image to the resources directory
            File output = new File(resourcesPath + outputImagePath);
            ImageIO.write(image, "png", output);
        }
        catch (IOException e) {
            System.out.println("Error writing image: " + e.getMessage());
        }
    }

    public static void drawPathOnImage(String inputImagePath, String outputImagePath, List<Node> path, Color color) throws IOException {
        // Read the image
        InputStream inputStream = ImgRead.class.getResourceAsStream("/" + inputImagePath); // Notice the leading slash
        if (inputStream == null) {
            throw new IOException("File not found: " + inputImagePath);
        }
        BufferedImage image = ImageIO.read(inputStream);

        // Get graphics object from the image
        Graphics2D g2d = image.createGraphics();

        // Set line color
        g2d.setColor(color);

        // Draw lines along the path
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);
            g2d.drawLine(current.y, current.x, next.y, next.x);
        }

        // Dispose the graphics object
        g2d.dispose();

        // Write the modified image to the output file
        try {
            // Get the resources directory path
            String resourcesPath = "src/main/resources/";

            // Save the image to the resources directory
            File output = new File(resourcesPath + outputImagePath);
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            System.out.println("Error writing image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Define input and output image paths
            String inputImagePath = "input.png";
            String outputImagePath = "output.png";

            // Define line coordinates and color
            int x1 = 100;
            int y1 = 100;
            int x2 = 300;
            int y2 = 200;
            Color color = Color.RED;

            // Draw line on the image
            drawLineOnImage(inputImagePath, outputImagePath, x1, y1, x2, y2, color);

            System.out.println("Line drawn successfully!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}