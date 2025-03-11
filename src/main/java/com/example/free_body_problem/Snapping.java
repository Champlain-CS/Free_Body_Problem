package com.example.free_body_problem;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;

public class Snapping {

    // Snapping threshold in pixels
    private static final double SNAP_THRESHOLD = 20;

    public static void snapBoxToPlane(Rectangle box, Line plane) {
        // Calculate the angle of the plane
        double planeAngle = Math.toDegrees(Math.atan2(plane.getEndY() - plane.getStartY(),
                plane.getEndX() - plane.getStartX()));

        // Normalize the angle to be between 0 and 360
        planeAngle = (planeAngle % 360 + 360) % 360;

        // Determine which side of the box to connect to the plane
        boolean useBottomSide = (planeAngle <= 90 || planeAngle >= 270);

        // Get the current angle of the box in radians
        double currentAngleRad = Math.toRadians(box.getRotate());

        // Calculate the box's center position
        double boxCenterX = box.getX() + box.getWidth() / 2;
        double boxCenterY = box.getY() + box.getHeight() / 2;

        // Calculate the vector from center to the connecting side (before rotation)
        double vectorY = useBottomSide ? box.getHeight() / 2 : -box.getHeight() / 2;

        // Apply rotation to this vector
        double rotatedVectorX = -Math.sin(currentAngleRad) * vectorY;
        double rotatedVectorY = Math.cos(currentAngleRad) * vectorY;

        // Calculate the actual connecting point position (bottom or top center)
        double connectingPointX = boxCenterX + rotatedVectorX;
        double connectingPointY = boxCenterY + rotatedVectorY;

        // Find the closest point on the plane to the box's connecting point
        double[] closestPoint = findClosestPointOnLine(connectingPointX, connectingPointY, plane);

        // Calculate the distance between the connecting point and the closest point
        double distance = Math.hypot(connectingPointX - closestPoint[0], connectingPointY - closestPoint[1]);

        // If the box is close enough to the plane, snap it
        if (distance <= SNAP_THRESHOLD) {
            // Position the box so its connecting point aligns with the closest point on the plane
            // We need to reverse the rotation calculation to find the new center
            double newCenterX = closestPoint[0] - rotatedVectorX;
            double newCenterY = closestPoint[1] - rotatedVectorY;

            // Adjust to find the top-left corner of the box
            box.setX(newCenterX - box.getWidth() / 2);
            box.setY(newCenterY - box.getHeight() / 2);

            // Rotate the box to match the plane's angle
            box.setRotate(planeAngle);
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

    public static void snapRopeStart(Node target, Line rope) {
        double px = rope.getStartX();
        double py = rope.getStartY();

        if (target instanceof Rectangle) {
            // Handle Rectangle nodes
            Rectangle rect = (Rectangle) target;
            double centerX = rect.getX() + rect.getWidth() / 2;
            double centerY = rect.getY() + rect.getHeight() / 2;

            // Calculate distance to rectangle bounds
            double minX = rect.getX();
            double minY = rect.getY();
            double maxX = minX + rect.getWidth();
            double maxY = minY + rect.getHeight();

            double dx = Math.max(minX - px, Math.max(0, px - maxX));
            double dy = Math.max(minY - py, Math.max(0, py - maxY));
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= SNAP_THRESHOLD) {
                rope.setStartX(centerX);
                rope.setStartY(centerY);
            }
        } else if (target instanceof Group) {
            // Handle Group nodes (Pulley)
            Group group = (Group) target;

            // Find first Circle in the group to get center coordinates
            for (Node node : group.getChildren()) {
                if (node instanceof Circle) {
                    Circle circle = (Circle) node;
                    double centerX = circle.getCenterX();
                    double centerY = circle.getCenterY();

                    // Calculate direct distance to center
                    double dx = px - centerX;
                    double dy = py - centerY;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance <= SNAP_THRESHOLD) {
                        rope.setStartX(centerX);
                        rope.setStartY(centerY);
                    }

                    break; // Only need to check the first circle in the group
                }
            }
        }
    }

    public static void snapRopeEnd(Node target, Line rope) {
        double px = rope.getEndX();
        double py = rope.getEndY();

        if (target instanceof Rectangle) {
            // Handle Rectangle nodes
            Rectangle rect = (Rectangle) target;
            double centerX = rect.getX() + rect.getWidth() / 2;
            double centerY = rect.getY() + rect.getHeight() / 2;

            // Calculate distance to rectangle bounds
            double minX = rect.getX();
            double minY = rect.getY();
            double maxX = minX + rect.getWidth();
            double maxY = minY + rect.getHeight();

            double dx = Math.max(minX - px, Math.max(0, px - maxX));
            double dy = Math.max(minY - py, Math.max(0, py - maxY));
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= SNAP_THRESHOLD) {
                rope.setEndX(centerX);
                rope.setEndY(centerY);
            }
        } else if (target instanceof Group) {
            // Handle Group nodes (Pulley)
            Group group = (Group) target;

            // Find first Circle in the group to get center coordinates
            for (Node node : group.getChildren()) {
                if (node instanceof Circle) {
                    Circle circle = (Circle) node;
                    double centerX = circle.getCenterX();
                    double centerY = circle.getCenterY();

                    // Calculate direct distance to center
                    double dx = px - centerX;
                    double dy = py - centerY;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance <= SNAP_THRESHOLD) {
                        rope.setEndX(centerX);
                        rope.setEndY(centerY);
                    }

                    break; // Only need to check the first circle in the group
                }
            }

        }
    }
}