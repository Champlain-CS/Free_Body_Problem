package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract  class PhysicsObject extends Group {
    // Common properties for physics objects
    protected HashMap<Rope, Boolean> connectedRopes;


    public PhysicsObject() {
        // Initialize lists
        connectedRopes = new HashMap<Rope, Boolean>();
    }

    // Abstract methods that child classes must implement
    public abstract double getCenterX();

    public abstract double getCenterY();


    // Common method to update connected ropes
    protected void updateConnectedRopes() {
        for (Rope rope : connectedRopes.keySet()) {

            // Update start or end point of the rope depending on which is snapped
            if (connectedRopes.get(rope)) {
                rope.getLine().setStartX(getCenterX());
                rope.getLine().setStartY(getCenterY());
            }
            else {
                rope.getLine().setEndX(getCenterX());
                rope.getLine().setEndY(getCenterY());
            }
            //print the hashmap elemenets
            System.out.println(rope + " connected: " + connectedRopes.get(rope));
        }
    }

}