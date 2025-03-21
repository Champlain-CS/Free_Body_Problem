package com.example.free_body_problem;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Snapping {

    // Snapping threshold in pixels
    private static final double SNAP_THRESHOLD = 20;

    public static void snapBoxToPlane(Box boxObject, List<Plane> planeList) {
         boxObject.isSnapped = false;

         Rectangle box = boxObject.getRectangle();
        // Loop through all physics objects to find planes
        for (Plane obj : planeList) {
                Line planeLine = obj.getLine();

                // Calculate the angle of the plane
                double planeAngle = Math.toDegrees(Math.atan2(planeLine.getEndY() - planeLine.getStartY(),
                        planeLine.getEndX() - planeLine.getStartX()));

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
                double[] closestPoint = findClosestPointOnLine(connectingPointX, connectingPointY, planeLine);

                // Calculate the distance between the connecting point and the closest point
                double distance = Math.hypot(connectingPointX - closestPoint[0], connectingPointY - closestPoint[1]);

                // If the box is close enough to the plane, snap it
                if (distance <= SNAP_THRESHOLD) {
                    // Position the box so its connecting point aligns with the closest point on the plane
                    double newCenterX = closestPoint[0] - rotatedVectorX;
                    double newCenterY = closestPoint[1] - rotatedVectorY;

                    // Adjust to find the top-left corner of the box
                    box.setX(newCenterX - box.getWidth() / 2);
                    box.setY(newCenterY - box.getHeight() / 2);

                    // Rotate the box to match the plane's angle
                    box.setRotate(planeAngle);

                    boxObject.isSnapped = true;
                    break; // Exit the loop once snapped to a plane
                }
            }

        // If the box is not snapped to any plane, reset its rotation to 0
        if (!boxObject.isSnapped) {
            box.setRotate(0);
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

    public static void snapRopeStart(PhysicsObject target, Rope rope) {
        double px = rope.getLine().getStartX();
        double py = rope.getLine().getStartY();

        // Store the current connection state before making any changes
        PhysicsObject currentStartConnection = rope.getStartConnection();

        // Check if the end of the rope is already connected to the target
        if (rope.getEndConnection() == target) {
            return; // Do not snap if the end is already connected to the target
        }

        if (target instanceof Box) {
            // Handle Rectangle nodes
            double centerX = target.getCenterX();
            double centerY = target.getCenterY();
            Box box = (Box) target;
            Rectangle rect = box.getRectangle();

            // Calculate distance to rectangle bounds
            double minX = rect.getX();
            double minY = rect.getY();
            double maxX = minX + rect.getWidth();
            double maxY = minY + rect.getHeight();

            double dx = Math.max(minX - px, Math.max(0, px - maxX));
            double dy = Math.max(minY - py, Math.max(0, py - maxY));
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= SNAP_THRESHOLD) {
                // If we're snapping to a new object, remove connection from the old one
                if (currentStartConnection != null && currentStartConnection != target) {
                    currentStartConnection.connectedRopes.remove(rope);
                }

                // Update rope position and connections
                rope.getLine().setStartX(centerX);
                rope.getLine().setStartY(centerY);
                target.connectedRopes.put(rope, true);
                rope.setStartConnection(target);
            }
        } else if (target instanceof Pulley) {
            // Handle Group nodes (Pulley)
            Pulley pulley = (Pulley) target;
            double centerX = pulley.getCenterX();
            double centerY = pulley.getCenterY();

            // Calculate direct distance to center
            double dx = px - centerX;
            double dy = py - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= SNAP_THRESHOLD) {
                // If we're snapping to a new object, remove connection from the old one
                if (currentStartConnection != null && currentStartConnection != target) {
                    currentStartConnection.connectedRopes.remove(rope);
                }

                // Update rope position and connections
                rope.getLine().setStartX(centerX);
                rope.getLine().setStartY(centerY);
                target.connectedRopes.put(rope, true);
                rope.setStartConnection(target);
            }
        }
    }

    public static void snapRopeEnd(PhysicsObject target, Rope rope) {
        double px = rope.getLine().getEndX();
        double py = rope.getLine().getEndY();

        // Store the current connection state before making any changes
        PhysicsObject currentEndConnection = rope.getEndConnection();

        // Check if the start of the rope is already connected to the target
        if (rope.getStartConnection() == target) {
            return; // Do not snap if the start is already connected to the target
        }

        if (target instanceof Box) {
            // Handle Rectangle nodes
            Box box = (Box) target;
            Rectangle rect = box.getRectangle();
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
                // If we're snapping to a new object, remove connection from the old one
                if (currentEndConnection != null && currentEndConnection != target) {
                    currentEndConnection.connectedRopes.remove(rope);
                }

                // Update rope position and connections
                rope.getLine().setEndX(centerX);
                rope.getLine().setEndY(centerY);
                target.connectedRopes.put(rope, false);
                rope.setEndConnection(target);
            }
        } else if (target instanceof Pulley) {
            // Handle Group nodes (Pulley)
            Pulley pulley = (Pulley) target;
            double centerX = pulley.getCenterX();
            double centerY = pulley.getCenterY();

            // Calculate direct distance to center
            double dx = px - centerX;
            double dy = py - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= SNAP_THRESHOLD) {
                // If we're snapping to a new object, remove connection from the old one
                if (currentEndConnection != null && currentEndConnection != target) {
                    currentEndConnection.connectedRopes.remove(rope);
                }

                // Update rope position and connections
                rope.getLine().setEndX(centerX);
                rope.getLine().setEndY(centerY);
                target.connectedRopes.put(rope, false);
                rope.setEndConnection(target);
            }
        }
    }

    public static void snapPlaneEnds(Line plane1, Line plane2) {
        double startX1 = plane1.getStartX();
        double startY1 = plane1.getStartY();
        double endX1 = plane1.getEndX();
        double endY1 = plane1.getEndY();

        double startX2 = plane2.getStartX();
        double startY2 = plane2.getStartY();
        double endX2 = plane2.getEndX();
        double endY2 = plane2.getEndY();

        if (distance(startX1, startY1, startX2, startY2) <= SNAP_THRESHOLD) {
            plane1.setStartX(startX2);
            plane1.setStartY(startY2);
        } else if (distance(startX1, startY1, endX2, endY2) <= SNAP_THRESHOLD) {
            plane1.setStartX(endX2);
            plane1.setStartY(endY2);
        } else if (distance(endX1, endY1, startX2, startY2) <= SNAP_THRESHOLD) {
            plane1.setEndX(startX2);
            plane1.setEndY(startY2);
        } else if (distance(endX1, endY1, endX2, endY2) <= SNAP_THRESHOLD) {
            plane1.setEndX(endX2);
            plane1.setEndY(endY2);
        }
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}