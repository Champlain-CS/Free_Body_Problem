package com.example.free_body_problem;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;

public class Snapping {

    // Method to check if a box should snap to a plane and adjust its position accordingly
    public static void snapBoxToPlane(Rectangle box, Line plane) {
        // Get the y-coordinates of the box's bottom edge
        double boxBottomY = box.getY() + box.getHeight();

        // Get the y-coordinates of the plane's start and end points
        double planeStartY = plane.getStartY();
        double planeEndY = plane.getEndY();

        // Check if the box's bottom edge is within ±10 pixels of the plane's y-coordinates
        if (Math.abs(boxBottomY - planeStartY) <= 10 || Math.abs(boxBottomY - planeEndY) <= 10) {
            // Calculate the angle of the plane
            double deltaX = plane.getEndX() - plane.getStartX();
            double deltaY = plane.getEndY() - plane.getStartY();

            // Handle horizontal planes (deltaY == 0)
            if (Math.abs(deltaY) < 0.001) { // Use a small epsilon to account for floating-point inaccuracies
                // For horizontal planes, simply align the box's bottom edge with the plane's y-coordinate
                box.setY(planeStartY - box.getHeight()-4);
                box.setRotate(0); // No rotation needed for horizontal planes
                return;
            }

            // Calculate the angle of the plane
            double angle = Math.atan2(deltaY, deltaX);

            // Calculate the new position of the box to align with the plane
            double newX = plane.getStartX() + (boxBottomY - planeStartY) * (deltaX / deltaY);
            double newY = planeStartY - box.getHeight();

            // Set the new position of the box
            box.setX(newX);
            box.setY(newY);

            // Rotate the box to match the plane's angle
            box.setRotate(Math.toDegrees(angle));
        }
    }
}