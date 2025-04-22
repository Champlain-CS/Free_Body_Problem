package com.example.free_body_problem;

import javafx.scene.Group;


import java.util.HashMap;

public abstract  class PhysicsObject extends Group {
    // Common properties for physics objects
    protected HashMap<Rope, Boolean> connectedRopes;
    public int numberOfRopes  = 0;



    public PhysicsObject() {
        // Initialize lists
        connectedRopes = new HashMap<>();
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
        }
        numberOfRopes = connectedRopes.size();
    }



}