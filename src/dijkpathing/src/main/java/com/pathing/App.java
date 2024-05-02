package com.pathing;

import java.awt.Color;
import java.io.IOException;
import java.util.List;



public class App 
{
    public static void main(String[] args) throws IOException {
        System.out.println("Running image compression");

        //DisplayImage img = new DisplayImage();
        //img.displayImage("/test.bmp");
    
        int[][][] imgArr = ImgRead.readImageToArr("/MapWithHoles.png");
        boolean[][] boolImg = ImgRead.imgToBool(imgArr, (r,g,b) -> r < 100 && g < 100 && b < 100);
        boolean[][] smoothImage = ImgRead.smoothEdges(boolImg, 5);
        boolean[][] compImg = ImgRead.convolveImgArr(smoothImage, 5);
        List<Node> path = Pather.findPath(compImg, 0, 0, .3f, .9f);
        List<Node> path2 = Pather.findPath(compImg, .3f, .9f, .5f, .5f);
        // for( int[] coord : path){
        //     System.out.println( coord[0] + " " + coord[1]);
        // }
        ImgRead.writeNewImage( ImgRead.boolToImg(compImg), "/compressed.png");
        ImageDrawer.drawPathOnImage("compressed.png", "/compressedWithLines2.png", path, Color.red);
        ImageDrawer.drawPathOnImage("compressedWithLines2.png", "/compressedWithLines2.png", path2, Color.red);
        

    }
}
