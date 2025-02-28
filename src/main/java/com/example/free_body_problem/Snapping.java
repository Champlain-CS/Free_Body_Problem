package com.example.free_body_problem;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;

public class Snapping {

    // Snapping threshold in pixels
    private static final double SNAP_THRESHOLD = 20;

    public static void snapBoxToPlane(Rectangle box, Line plane) {
        // Get the box's current position and dimensions
        double boxWidth = box.getWidth();
        double boxHeight = box.getHeight();
        double boxCenterX = box.getX() + boxWidth / 2;
        double boxBottomY = box.getY() + boxHeight;

        // Get the plane's start and end coordinates
        double planeStartX = plane.getStartX();
        double planeStartY = plane.getStartY();
        double planeEndX = plane.getEndX();
        double planeEndY = plane.getEndY();

        // Find the closest point on the plane to the box's bottom center
        double[] closestPoint = findClosestPointOnLine(
                boxCenterX, boxBottomY,
                planeStartX, planeStartY,
                planeEndX, planeEndY
        );

        // Calculate the distance between the box's bottom center and the closest point
        double distance = Math.hypot(boxCenterX - closestPoint[0], boxBottomY - closestPoint[1]);

        // Check if the box is within the snapping threshold
        if (distance <= SNAP_THRESHOLD) {
            // Calculate plane angle
            double deltaX = planeEndX - planeStartX;
            double deltaY = planeEndY - planeStartY;
            double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

            // Normalize angle to be between -180 and 180 degrees
            while (angle > 180) angle -= 360;
            while (angle < -180) angle += 360;

            // Calculate the normal vector to determine the "top" side
            double normalX = -deltaY;
            double normalY = deltaX;

            // Normalize the normal vector
            double normalLength = Math.hypot(normalX, normalY);
            normalX /= normalLength;
            normalY /= normalLength;

            // Ensure the normal points "upward" (in JavaFX, negative Y is up)
            if (normalY > 0) {
                normalX = -normalX;
                normalY = -normalY;
                // Flip the angle accordingly
                angle = (angle > 0) ? angle - 180 : angle + 180;
            }

            // Calculate exact offset for flush contact at any angle
            double halfWidth = boxWidth / 2;
            double angleRadians = Math.toRadians(angle);
            double offsetY = halfWidth * Math.abs(Math.sin(angleRadians));

            // Position box so it's centered on the closest point and flush with the plane
            box.setX(closestPoint[0] - halfWidth);
            box.setY(closestPoint[1] - boxHeight - offsetY);

            // Apply rotation
            box.setRotate(angle);

            System.out.println("Closest point: " + closestPoint[0] + ", " + closestPoint[1]);
            System.out.println("Box position: " + box.getX() + ", " + box.getY());
            System.out.println("Applied angle: " + angle + ", Offset: " + offsetY);
        }
    }

    // Helper method to find the closest point on a line segment to a given point
    private static double[] findClosestPointOnLine(
            double px, double py,
            double x1, double y1,
            double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len2 = dx * dx + dy * dy;  // squared length of line segment

        // If line segment is just a point, return that point
        if (len2 == 0) return new double[] {x1, y1};

        // Calculate projection of point onto line
        double t = ((px - x1) * dx + (py - y1) * dy) / len2;

        // Constrain t to lie within the line segment
        t = Math.max(0, Math.min(1, t));

        // Calculate the closest point
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        return new double[] {closestX, closestY};
    }
}