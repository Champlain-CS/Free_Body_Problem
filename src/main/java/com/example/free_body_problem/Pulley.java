package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class Pulley extends PhysicsObject {
    public Group circleGroup;
    private Pane parentContainer;
    private Double lastDragDelta = null;
    private double radius;
    public List<Box> connectedBoxes = new ArrayList<>();
// Store the outer radius for boundary calculations

    public Pulley(double x, double y, double outerRadius, double innerRadius, Color outerColor, Color innerColor, Pane parentContainer) {
        this.parentContainer = parentContainer;
        this.radius = outerRadius;

        Circle outerCircle = new Circle(x, y, outerRadius, outerColor);
        outerCircle.setStroke(null);

        Circle innerCircle = new Circle(x, y, innerRadius, innerColor);
        innerCircle.setStroke(null);

        circleGroup = new Group(outerCircle, innerCircle);

        // Set userData for all components
        innerCircle.setUserData(this);
        outerCircle.setUserData(this);
        circleGroup.setUserData(this);

        addDragListener();
    }

    public double getCenterX() {
        return circleGroup.getBoundsInParent().getCenterX();
    }

    public double getCenterY() {
        return circleGroup.getBoundsInParent().getCenterY();
    }

    public Group getCircleGroup() {
        return circleGroup;
    }

    public Double getLastDragDelta() {
        return lastDragDelta;
    }

    public void setLastDragDelta(Double delta) {
        this.lastDragDelta = delta;
    }

    public void addDragListener() {
        circleGroup.setOnMousePressed(event -> {
            lastDragDelta = null;
            circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        circleGroup.setOnMouseDragged(event -> {
            double[] initialPress = (double[]) circleGroup.getUserData();
            double offsetX = event.getSceneX() - initialPress[0];
            double offsetY = event.getSceneY() - initialPress[1];

            // Get the center position of the pulley (using first circle)
            Circle firstCircle = (Circle) circleGroup.getChildren().get(0);
            double centerX = firstCircle.getCenterX();
            double centerY = firstCircle.getCenterY();

            // Calculate proposed new position
            double newX = centerX + offsetX;
            double newY = centerY + offsetY;

            // Constrain to parent bounds
            newX = Math.max(radius, Math.min(newX, parentContainer.getWidth() - radius));
            newY = Math.max(radius, Math.min(newY, parentContainer.getHeight() - radius));

            // Apply rope constraints
            newX = applyRopeConstraints(newX);

            // Only update if position has changed
            if (newX != centerX || newY != centerY) {
                double deltaX = newX - centerX;
                double deltaY = newY - centerY;

                // Update the position of all circles in the group
                for (Node node : circleGroup.getChildren()) {
                    Circle circle = (Circle) node;
                    circle.setCenterX(circle.getCenterX() + deltaX);
                    circle.setCenterY(circle.getCenterY() + deltaY);
                }

                initialPress[0] = event.getSceneX();
                initialPress[1] = event.getSceneY();
                circleGroup.setUserData(initialPress);
                lastDragDelta = Math.sqrt(offsetX * offsetX + offsetY * offsetY);

                updateConnectedRopes();
            }
        });

        circleGroup.setOnMouseReleased(event -> {
            lastDragDelta = null;
        });
    }

    public void setPosition(double x, double y) {
        // Apply basic container constraints first
        double constrainedX = Math.max(radius, Math.min(x, parentContainer.getWidth() - radius));
        double constrainedY = Math.max(radius, Math.min(y, parentContainer.getHeight() - radius));

        // Then apply rope constraints if needed
        constrainedX = applyRopeConstraints(constrainedX);

        // Calculate delta to move all circles
        double deltaX = constrainedX - getCenterX();
        double deltaY = constrainedY - getCenterY();

        // Update all circles in the group
        for (Node node : circleGroup.getChildren()) {
            Circle circle = (Circle) node;
            circle.setCenterX(circle.getCenterX() + deltaX);
            circle.setCenterY(circle.getCenterY() + deltaY);
        }

        updateConnectedRopes();
    }

    private double applyRopeConstraints(double newX) {
        if (connectedRopes.size() > 1) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            // Find the leftmost and rightmost connection points
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();

                double ropeX;
                if (isStartConnected) {
                    ropeX = rope.getLine().getEndX();
                } else {
                    ropeX = rope.getLine().getStartX();
                }

                // The pulley can't move past the connection point
                if (ropeX < minX) {
                    minX = ropeX;
                }
                if (ropeX > maxX) {
                    maxX = ropeX;
                }
            }

            // Constrain the pulley position based on the rope connections
            // Accounting for the radius in constraints
            return Math.max(minX, Math.min(newX, maxX));
        }
        return newX;
    }

    public void enforceConstraints() {
        if (connectedRopes.size() > 1) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            // Find boundary constraints from connected ropes
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();

                // Get the non-connected end position
                double ropeX = isStartConnected ?
                        rope.getLine().getEndX() :
                        rope.getLine().getStartX();

                if (ropeX < minX) minX = ropeX;
                if (ropeX > maxX) maxX = ropeX;
            }

            // Apply constraints
            double constrainedX = Math.max(minX, Math.min(getCenterX(), maxX));
            if (constrainedX != getCenterX()) {
                setPosition(constrainedX, getCenterY());
            }
        }
    }

    public void updateBoxList() {
        //check if the list is not empty
        if (!connectedRopes.isEmpty()) {
            connectedBoxes.clear();
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                if (rope.getStartConnection() instanceof Box){
                    Box ropeBox = (Box) rope.getStartConnection();
                    connectedBoxes.add(ropeBox);
                }
                else if (rope.getEndConnection() instanceof Box){
                    Box ropeBox = (Box) rope.getEndConnection();
                    connectedBoxes.add(ropeBox);
                }

            }

        }
        System.out.println("Number of boxes is " + connectedBoxes.size());
    }
}