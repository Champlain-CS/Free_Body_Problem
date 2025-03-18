package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;

public class Pulley extends PhysicsObject {
    public Group circleGroup;
    // List to store multiple connected ropes
    public List<Rope> connectedRopes;
    // Lists to track which end of each rope is connected
    public List<Boolean> ropeStartSnapped;
    public List<Boolean> ropeEndSnapped;

    public Pulley(double x, double y, double outerRadius, double innerRadius, Color outerColor, Color innerColor) {
        Circle outerCircle = new Circle(x, y, outerRadius, outerColor);
        outerCircle.setStroke(null);

        Circle innerCircle = new Circle(x, y, innerRadius, innerColor);
        innerCircle.setStroke(null);

        circleGroup = new Group(outerCircle, innerCircle);

        // Initialize the lists


        // Set userData for all components
        innerCircle.setUserData(this);
        outerCircle.setUserData(this);
        circleGroup.setUserData(this);
    }
    public double getCenterX(){
        return circleGroup.getBoundsInParent().getCenterX();
    }
    public double getCenterY(){
        return circleGroup.getBoundsInParent().getCenterY();
    }

    public Group getCircleGroup() {
        return circleGroup;
    }

    // Add a rope to the pulley's connected ropes
    public void addRope(Rope rope, boolean isStartSnapped) {
        connectedRopes.add(rope);
        if (isStartSnapped) {
            ropeStartSnapped.add(true);
            ropeEndSnapped.add(false);
        } else {
            ropeStartSnapped.add(false);
            ropeEndSnapped.add(true);
        }
    }

    // Remove a rope from the pulley
    public void removeRope(Rope rope) {
        int index = connectedRopes.indexOf(rope);
        if (index != -1) {
            connectedRopes.remove(index);
            ropeStartSnapped.remove(index);
            ropeEndSnapped.remove(index);
        }
    }

    public void addDragListener() {
        circleGroup.setOnMousePressed(event -> {
            circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        circleGroup.setOnMouseDragged(event -> {
            double[] offset = (double[]) circleGroup.getUserData();
            double deltaX = event.getSceneX() - offset[0];
            double deltaY = event.getSceneY() - offset[1];

            // Update the position of all circles in the group
            for (Node node : circleGroup.getChildren()) {
                Circle circle = (Circle) node;
                circle.setCenterX(circle.getCenterX() + deltaX);
                circle.setCenterY(circle.getCenterY() + deltaY);
            }

            // Get the center position of the pulley (using first circle)
            Circle firstCircle = (Circle) circleGroup.getChildren().get(0);
            double centerX = firstCircle.getCenterX();
            double centerY = firstCircle.getCenterY();

            updateConnectedRopes();

            circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        circleGroup.setOnMouseReleased(event -> {
            // Handle mouse release if needed
        });
    }
}