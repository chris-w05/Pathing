package com.pathing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.imageio.ImageIO;

interface BooleanExpression {

    boolean evaluate(int r, int g, int b);
}

/**
 *
 * @author chris
 */
public class ImgRead {

    // private static final boolean[][] kernel = {
    //     {true, false, true},
    //     {false, false, false},
    //     {true, false, true}
    // };
    private static class Coordinate{
        public int x;
        public int y;

        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
        }

        public float dist(int u, int v){
            return (float)Math.sqrt( Math.pow(u - x, 2) + Math.pow(v - y, 2));
        }

        public static float distBetween(int u, int v, int a, int b ){
            return (float)Math.sqrt( Math.pow(u - a, 2) + Math.pow(v - b, 2));
        }
    }

    public static int[][][] readImageToArr(String fileToRead) {
        try {
            InputStream inputStream = ImgRead.class.getResourceAsStream(fileToRead);
            if (inputStream == null) {
                throw new IOException("File not found: " + fileToRead);
            }
            BufferedImage image = ImageIO.read(inputStream);

            int width = image.getWidth();
            int height = image.getHeight();

            int[][][] pixels = new int[height][width][3];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    pixels[y][x][0] = (rgb >> 16) & 0xFF; // Red
                    pixels[y][x][1] = (rgb >> 8) & 0xFF;  // Green
                    pixels[y][x][2] = (rgb) & 0xFF;       // Blue
                }
            }

            // Now you have the image data in the pixels array
            // You can access it like pixels[y][x][0] for Red, pixels[y][x][1] for Green, and pixels[y][x][2] for Blue
            // Example: Print the RGB values of the pixel at (0, 0)
            // System.out.println("Red: " + pixels[0][0][0]);
            // System.out.println("Green: " + pixels[0][0][1]);
            // System.out.println("Blue: " + pixels[0][0][2]);
            System.out.println("image read successfully");
            return pixels;

        } catch (IOException e) {
            System.out.println("IOException in readImageToArr.\nError: " + e.getMessage());
            int[][][] empty = new int[0][0][0];
            return empty;
        }
    }

    public static void writeNewImage(int[][][] img, String fileName) {
        int height = img.length;
        int width = img[0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = (img[y][x][0] << 16) | (img[y][x][1] << 8) | img[y][x][2];
                bufferedImage.setRGB(x, y, rgb);
            }
        }

        try {
            // Get the resources directory path
            String resourcesPath = "src/main/resources/";

            // Save the image to the resources directory
            File output = new File(resourcesPath + fileName);
            ImageIO.write(bufferedImage, "png", output);
            System.out.println("Image written successfully.");
        } catch (IOException e) {
            System.out.println("Error writing image: " + e.getMessage());
        }
    }

    public static boolean[][] convolveImgArr(boolean[][] imageToConvolve, int pixelSize) {
        int width = imageToConvolve.length / (pixelSize);
        int length = imageToConvolve[0].length / (pixelSize);
        boolean[][] convolvedImg = new boolean[width][length];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {

                boolean isOpen = true;
                int imgI = i * pixelSize;
                int imgJ = j * pixelSize;

                for (int k = imgI; (k < imgI + pixelSize - 1) && k < imageToConvolve.length; k++) {
                    for (int l = imgJ; (l < imgJ + pixelSize - 1) && l < imageToConvolve[k].length; l++) {
                        isOpen &= imageToConvolve[k][l];
                    }
                }

                convolvedImg[i][j] = isOpen;

            }
        }

        return convolvedImg;
    }

    /**
     * Converts an image into a boolean array using a boolean leamnda expression
     *
     * @param img image array to be converted
     * @param expression Boolean expression to evaluate as true when the pixel is considered an obstacle
     * @return 2D array of pixles that meet the citeria set by expression input
     */
    public static boolean[][] imgToBool(int[][][] img, BooleanExpression expression) {
        boolean[][] out = new boolean[img.length][img[0].length];

        for (int i = 0; i < img.length; i++) {
            //System.out.println();
            for (int j = 0; j < img[i].length; j++) {
                int r = img[i][j][0];
                int g = img[i][j][1];
                int b = img[i][j][2];
                //System.out.print(r + "," + g + "," + b + "|");
                out[i][j] = expression.evaluate(r, g, b);
                //System.out.print(out[i][j] + " ");
            }
        }
        System.out.println();
        return out;
    }

    public static int[][][] boolToImg(boolean[][] img) {
        int[][][] out = new int[img.length][img[0].length][3];

        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[i].length; j++) {
                if (img[i][j]) {
                    out[i][j][0] = 0;
                    out[i][j][1] = 0;
                    out[i][j][2] = 0;
                } else {
                    out[i][j][0] = 255;
                    out[i][j][1] = 255;
                    out[i][j][2] = 255;
                }
            }
        }

        return out;
    }
    
    public static boolean[][] smoothEdges2(boolean[][] img, int radius) {
    int height = img.length;
    int width = img[0].length;

    boolean[][] smoothedImg = new boolean[height][width];

    // Iterate over each pixel in the image
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int count = 0;

            // Iterate over neighboring pixels within the specified radius
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    int nx = x + dx;
                    int ny = y + dy;

                    // Check if the neighboring pixel is within the image bounds
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        if (img[ny][nx]) { // If neighboring pixel is white (true)
                            count++;
                        }
                    }
                }
            }

            // Update the value of the current pixel based on the count and radius
            smoothedImg[y][x] = (count >= (2 * radius + 1) * (2 * radius + 1) / 2);
        }
    }

    return smoothedImg;
}
    
    public static boolean[][] smoothEdges( boolean[][] img, int radius ){
        int height = img.length;
        int width = img[0].length;
        Stack<Coordinate> coords = new Stack<>();
        boolean[][] smoothedImg = new boolean[height][width];
        boolean[][] kernel = createKernel( radius );

        //finds all pixels that are obstacles
        for( int i = 0; i < height; i ++){
            for( int j = 0; j < width; j ++){
                //current index is false, meaning it is a barrier
                if(img[i][j]){
                    //System.out.println("Added coord at " + i + ", " + j);
                    Coordinate coord = new Coordinate(i, j);
                    coords.add(coord);
                    smoothedImg[i][j] = false;
                }
            }
        }

        System.out.println("Img num pixels: " + (height * width));
        System.out.println("Number of coordinates found: " + coords.size());

        while(!coords.empty()){
            Coordinate point = coords.pop();
            smoothedImg = applyKernelAtCoordinate(img, kernel, point.x, point.y);
        }
        return smoothedImg;
    }

    private static boolean[][] applyKernelAtCoordinate(boolean[][] img, boolean[][] kernel, int x, int y) {
        int size = kernel.length;
        int radius = size / 2;

        for (int i = x - radius; i <= x + radius && i < img.length; i++) {
            for (int j = y - radius; j <= y + radius && j < img[0].length; j++) {
                if (i >= 0 && j >= 0){
                    if (kernel[i - x + radius][j - y + radius]) {
                        img[i][j] = true; // Sets regions close to x and y equal to true
                    }
                }
            }
        }
        return img;
    }

    private static boolean[][] createKernel( int radius){
        int size = radius * 2 - 1;
        boolean[][] kernel = new boolean[size][size];
        for(int i = 0; i < size; i ++){
            for( int j = 0; j < size; j ++){
                if( Coordinate.distBetween(i, j, radius - 1, radius - 1) > radius - 1){
                    kernel[i][j] = false;
                }
                else{
                    kernel[i][j] = true;
                }
            }
        }

        return kernel;
    }


    public static <T> void disp(T[][] array) {
        for (T[] array1 : array) {
            System.out.println();
            for (T item : array1) {
                System.out.print(item + " ");
            }
        }
        System.out.println();
    }

    public static void disp(int[][][] array) {
        for (int[][] array1 : array) {
            System.out.println();
            for (int[] array11 : array1) {
                System.out.print(array11[0] + "," + array11[1] + "," + array11[2] + " | ");
            }
        }
        System.out.println();
    }

    public static void disp(boolean[][] array) {
        System.out.println("Width of " + array[0].length);
        System.out.println("Length of " + array.length);

        for (boolean[] array1 : array) {
            System.out.println();
            for (int j = 0; j < array1.length; j++) {
                System.out.print(array1[j] + " ");
            }
        }
        System.out.println();
    }
}
