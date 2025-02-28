package com.example.free_body_problem;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;

public class Snapping {

    // Snapping threshold in pixels
    private static final double SNAP_THRESHOLD = 20;

    public static void snapBoxToPlane(Rectangle box, Line plane) {
        // Get the box's bottom center position
        double boxCenterX = box.getX() + box.getWidth() / 2;
        double boxBottomY = box.getY() + box.getHeight();

        // Find the closest point on the plane to the box's bottom center
        double[] closestPoint = findClosestPointOnLine(boxCenterX, boxBottomY, plane);

        // Calculate the distance between the box's bottom center and the closest point
        double distance = Math.hypot(boxCenterX - closestPoint[0], boxBottomY - closestPoint[1]);

        // If the box is close enough to the plane, snap it
        if (distance <= SNAP_THRESHOLD) {
            // Calculate the angle of the plane
            double angle = Math.toDegrees(Math.atan2(plane.getEndY() - plane.getStartY(), plane.getEndX() - plane.getStartX()));

            // Position the box so its bottom center aligns with the closest point on the plane
            box.setX(closestPoint[0] - box.getWidth() / 2);
            box.setY(closestPoint[1] - box.getHeight());

            // Rotate the box to match the plane's angle
            box.setRotate(angle);
        }
    }

    // Helper method to find the closest point on a line segment to a given point
    private static double[] findClosestPointOnLine(double px, double py, Line line) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len2 = dx * dx + dy * dy; // Squared length of the line segment

        // If the line segment is just a point, return that point
        if (len2 == 0) return new double[] {x1, y1};

        // Calculate the projection of the point onto the line
        double t = ((px - x1) * dx + (py - y1) * dy) / len2;

        // Constrain t to lie within the line segment
        t = Math.max(0, Math.min(1, t));

        // Calculate the closest point
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        return new double[] {closestX, closestY};
    }
}