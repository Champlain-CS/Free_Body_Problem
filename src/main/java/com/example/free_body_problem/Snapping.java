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
        if (distance <= SNAP_THRESHOLD && plane.getUserData() instanceof Plane) {
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
        boolean snapped = false;

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
                updateBoxSnappedStatus(rect, rope, true, false);

                snapped = true;
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
                        snapped = true;
                        updatePulleySnappedStatus(circle, rope, true, false);
                    }

                    break; // Only need to check the first circle in the group
                }
            }
        }

        // Update the Rope object's snapped status if snapping occurred
        if (snapped) {
            updateRopeSnappedStatus(rope, true, false);
        }
    }

    public static void snapRopeEnd(Node target, Line rope) {
        double px = rope.getEndX();
        double py = rope.getEndY();
        boolean snapped = false;

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
                updateBoxSnappedStatus(rect, rope, false, true);

                snapped = true;
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
                        snapped = true;
                        updatePulleySnappedStatus(circle, rope, false, true);
                    }

                    break; // Only need to check the first circle in the group
                }
            }
        }

        // Update the Rope object's snapped status if snapping occurred
        if (snapped) {
            updateRopeSnappedStatus(rope, false, true);
        }
    }

    // Helper method to find and update the Rope object
    private static void updateRopeSnappedStatus(Line line, boolean startSnapped, boolean endSnapped) {
        // Check different ways to find the associated Rope object

        // Option 1: Check if the line itself has the Rope object as userData
        if (line.getUserData() instanceof Rope) {
            Rope rope = (Rope) line.getUserData();
            if (startSnapped) rope.setStartSnapped(true);
            if (endSnapped) rope.setEndSnapped(true);
        }
    }

    //Same method but for Box object through rectangle and will set end or start snapped and also associate the rope object to the box
    private static void updateBoxSnappedStatus(Rectangle rectangle, Line line, boolean startSnapped, boolean endSnapped) {
        if (line.getUserData() instanceof Rope && rectangle.getUserData() instanceof Box) {
            Box box = (Box) rectangle.getUserData();
            Rope rope = (Rope) line.getUserData();

            // Check if the rope is already connected to the box
            int existingIndex = box.connectedRopes.indexOf(rope);

            if (existingIndex != -1) {
                // Update existing connection
                if (startSnapped) {
                    box.ropeStartSnapped.set(existingIndex, true);
                    box.ropeEndSnapped.set(existingIndex, false);
                }
                if (endSnapped) {
                    box.ropeStartSnapped.set(existingIndex, false);
                    box.ropeEndSnapped.set(existingIndex, true);
                }
            } else {
                // Add new rope connection
                box.connectedRopes.add(rope);
                if (startSnapped) {
                    box.ropeStartSnapped.add(true);
                    box.ropeEndSnapped.add(false);
                } else {
                    box.ropeStartSnapped.add(false);
                    box.ropeEndSnapped.add(true);
                }
            }
        }
    }

    // Updated to handle multiple ropes in a Pulley
    private static void updatePulleySnappedStatus(Node circle, Line line, boolean startSnapped, boolean endSnapped) {
        if (line.getUserData() instanceof Rope && circle.getUserData() instanceof Pulley) {
            Pulley pulley = (Pulley) circle.getUserData();
            Rope rope = (Rope) line.getUserData();

            // Check if rope is already connected to this pulley
            int existingIndex = pulley.connectedRopes.indexOf(rope);

            if (existingIndex != -1) {
                // Update existing connection
                if (startSnapped) {
                    pulley.ropeStartSnapped.set(existingIndex, true);
                    pulley.ropeEndSnapped.set(existingIndex, false);
                }
                if (endSnapped) {
                    pulley.ropeStartSnapped.set(existingIndex, false);
                    pulley.ropeEndSnapped.set(existingIndex, true);
                }
            } else {
                // Add new rope connection
                pulley.connectedRopes.add(rope);
                if (startSnapped) {
                    pulley.ropeStartSnapped.add(true);
                    pulley.ropeEndSnapped.add(false);
                } else {
                    pulley.ropeStartSnapped.add(false);
                    pulley.ropeEndSnapped.add(true);
                }
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