package com.pathing;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Pather {
    /***
     * Constrains a float value to a lower and upper bound
     */
    private static float constrainToRange(float value, float low, float high) {
        if (value < low){
            return low;
        }
        else if (value > high){
            return high;
        }
        else{
            return value;
        }
    }

    /***
     * Finds shortest path to every point on a given map
     * @param grid Boolean map where false are open regions and true are obstacles
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @return A 2D array of Nodes, containing their position, the shortest distance to them, and the path to them
     */
    public static Node[][] findAllPaths(boolean[][] grid, int startX, int startY){
        int rows = grid.length;
        int cols = grid[0].length;

        Node[][] map = new Node[rows][cols];
        // int[] dx = {-4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0, -1, -2, -3, -4};
        // int[] dy = {0, -1, -2, -3, -4, -4, -3, -2, -1, 1, 2, 3, 4, 4, 3, 2, 1, 0};
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        // Use priority queue since there is a built-in compare to in Node. 
        // This means the shortest node will always be on top
        PriorityQueue<Node> unvisited = new PriorityQueue<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = new Node(i, j, Integer.MAX_VALUE);
            }
        }

        map[startX][startY] = new Node(startX, startY, 0);
        unvisited.add(map[startX][startY]);

        while (!unvisited.isEmpty()) {
            Node currentNode = unvisited.poll(); // poll() to remove and get the nearest node

            for (int i = 0; i < dx.length; i++) {
                int x = currentNode.x + dx[i];
                int y = currentNode.y + dy[i];
                if (x >= 0 && x < rows && y >= 0 && y < cols ) {
                    if( !grid[x][y] && !map[x][y].visited){
                        float distance = currentNode.distance + currentNode.distanceTo(map[x][y]);
                        if (distance < map[x][y].distance) {
                            map[x][y].distance = distance;
                            map[x][y].path = new ArrayList<>(currentNode.path); // Correct path updating
                            map[x][y].path.add(currentNode); // Add current node to the path
                            unvisited.add(map[x][y]); // Add or update the node in the priority queue
                        }
                    }
                    else{
                    unvisited.remove(map[x][y]);
                    }
                }
            }
        }

        return map;
    }

    /***
     * Finds path to given coordinate from specified coordinate, with positions relative to the size of the image (0 to 1)
     */
    public static List<Node> findPath(boolean[][] grid, float initX, float initY, float finalX, float finalY ){
        int rows = grid.length;
        int cols = grid[0].length;
        initX = constrainToRange(initX, 0,1);
        initY = constrainToRange(initY, 0,1);
        finalX = constrainToRange(finalX, 0,1);
        finalY = constrainToRange(finalY, 0,1);

        return findPath( grid, (int)(rows * initX), (int)(cols * initY), (int)(rows * finalX), (int)(cols * finalY));
    }

    public static List<Node> findPath(boolean[][] grid, int startX, int startY, int targetX, int targetY) {
        int rows = grid.length;
        int cols = grid[0].length;

        Node[][] map = new Node[rows][cols];
        // int[] dx = {-4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0, -1, -2, -3, -4};
        // int[] dy = {0, -1, -2, -3, -4, -4, -3, -2, -1, 1, 2, 3, 4, 4, 3, 2, 1, 0};
        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1};
        // Use priority queue since there is a built-in compare to in Node. 
        // This means the shortest node will always be on top
        PriorityQueue<Node> unvisited = new PriorityQueue<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = new Node(i, j, Integer.MAX_VALUE);
            }
        }

        map[startX][startY] = new Node(startX, startY, 0);
        unvisited.add(map[startX][startY]);

        while (!unvisited.isEmpty()) {
            Node currentNode = unvisited.poll(); // poll() to remove and get the nearest node

            if (currentNode.x == targetX && currentNode.y == targetY) {
                return currentNode.path;
            }

            for (int i = 0; i < dx.length; i++) {
                int x = currentNode.x + dx[i];
                int y = currentNode.y + dy[i];
                if (x >= 0 && x < rows && y >= 0 && y < cols ) {
                    if( !grid[x][y] && !map[x][y].visited){
                        float distance = currentNode.distance + currentNode.distanceTo(map[x][y]);
                        if (distance < map[x][y].distance) {
                            map[x][y].distance = distance;
                            map[x][y].path = new ArrayList<>(currentNode.path); // Correct path updating
                            map[x][y].path.add(currentNode); // Add current node to the path
                            unvisited.add(map[x][y]); // Add or update the node in the priority queue
                        }
                    }
                    else{
                    unvisited.remove(map[x][y]);
                    }
                }
            }
        }

        System.out.println("Failed to find route");
        return new ArrayList<>();
    }



   
}
