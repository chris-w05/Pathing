package com.pathing;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    int x, y;
    float distance;
    List<Node> path;
    boolean visited;

    Node(int x, int y, float distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.path = new ArrayList<>();
        visited = false;
    }

    public float distanceTo( Node node){
        return (float)Math.sqrt( Math.pow((double)(node.x - x), 2) + Math.pow((double)(node.y - y),2) );
    }

    @Override
    public int compareTo(Node other) {
        return Float.compare(this.distance, other.distance);
    }
}