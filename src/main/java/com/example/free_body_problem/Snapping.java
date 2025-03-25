package com.example.free_body_problem;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Snapping {

    // Snapping threshold in pixels
    private static final double SNAP_THRESHOLD = 20;

    public static void snapBoxToPlane(Box boxObject, List<Plane> planeList) {
        boolean wasSnapped = boxObject.isSnapped;

        boxObject.isSnapped = false;

        Rectangle box = boxObject.getRectangle();

        // Loop through all planes to find snapping opportunities
        for (Plane obj : planeList) {
            Line planeLine = obj.getLine();

            // Calculate the angle of the plane
            double planeAngle = Math.toDegrees(Math.atan2(planeLine.getEndY() - planeLine.getStartY(),
                    planeLine.getEndX() - planeLine.getStartX()));

            // Normalize the angle to be between 0 and 360
            planeAngle = (planeAngle % 360 + 360) % 360;

            // Determine whether to use top or bottom of the box based on plane angle
            boolean useBottomEdge = !(planeAngle > 90 && planeAngle < 270);

            // Get the current angle of the box in radians
            double currentAngleRad = Math.toRadians(planeAngle);

            // Calculate the box's center position
            double boxCenterX = box.getX() + box.getWidth() / 2;
            double boxCenterY = box.getY() + box.getHeight() / 2;

            // Calculate the edge center point (bottom or top) of the box before rotation
            double edgeCenterX = boxCenterX;
            double edgeCenterY = useBottomEdge
                    ? (boxCenterY + box.getHeight() / 2)  // Bottom edge
                    : (boxCenterY - box.getHeight() / 2); // Top edge

            // Rotate the edge center point around the center of the box
            double rotatedEdgeX = boxCenterX +
                    (edgeCenterX - boxCenterX) * Math.cos(currentAngleRad) -
                    (edgeCenterY - boxCenterY) * Math.sin(currentAngleRad);
            double rotatedEdgeY = boxCenterY +
                    (edgeCenterX - boxCenterX) * Math.sin(currentAngleRad) +
                    (edgeCenterY - boxCenterY) * Math.cos(currentAngleRad);

            // Find the closest point on the plane to the rotated edge center
            double[] closestPoint = findClosestPointOnLine(rotatedEdgeX, rotatedEdgeY, planeLine);

            // Calculate the distance between the edge center and the closest point
            double distance = Math.hypot(rotatedEdgeX - closestPoint[0], rotatedEdgeY - closestPoint[1]);

            // If the box is close enough to the plane, snap it
            if (distance <= SNAP_THRESHOLD) {
                // Calculate the offset to move the box so its edge is exactly on the plane
                double offsetX = closestPoint[0] - rotatedEdgeX;
                double offsetY = closestPoint[1] - rotatedEdgeY;

                // Move the box
                box.setX(box.getX() + offsetX);
                box.setY(box.getY() + offsetY);

                // Adjust rotation to ensure correct orientation for normal vectors
                // If the plane angle is between 90 and 270, flip 180 degrees
                double finalRotation = planeAngle;
                if (planeAngle > 90 && planeAngle < 270) {
                    finalRotation += 180;
                }
                finalRotation %= 360;

                // Set the box rotation
                box.setRotate(finalRotation);

                boxObject.isSnapped = true;
                boxObject.snappedToPlane = true;
                boxObject.snappedPlane = obj;

                break; // Exit the loop once snapped to a plane
            }
        }

        // Handle visual feedback for snapping
        if (boxObject.isSnapped) {
            boxObject.getResizeHandle().setFill(Color.GRAY); // Gray out the handle when disabled
        } else {
            boxObject.getResizeHandle().setFill(Color.RED);  // Return to original color
            box.setRotate(0); // If the box is not snapped to any plane, reset its rotation to 0
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