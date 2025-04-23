package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

public class Rope extends PhysicsObject {
    private final Line line;
    private final Circle startHandle;
    private final Circle endHandle;
    public Boolean startSnapped = false;
    public Boolean endSnapped;
    private final List<PhysicsObject> physicsObjectList;
    private boolean isOnHandle = false;
    private double orientation;

    // Add these new fields to track connections
    private PhysicsObject startConnection;
    private PhysicsObject endConnection;

    public Rope(double startX, double startY, double endX, double endY, Boolean endSnapped, List<PhysicsObject> physicsObjectList) {
        this.endSnapped = endSnapped;
        line = new Line(startX, startY, endX, endY);
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true,
                javafx.scene.paint.CycleMethod.REFLECT,
                new Stop(0, Color.web("#A0522D")),
                new Stop(0.5, Color.web("#D2691E")),
                new Stop(1, Color.web("#8B4513")));
        line.setStroke(gradient);
        line.setStrokeWidth(8);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        startHandle = createHandle(startX, startY);
        this.physicsObjectList = physicsObjectList;

        //DEBUG
        startHandle.setFill(Color.RED);
        endHandle = createHandle(endX, endY);

        line.setUserData(this);
        Group group = new Group(line);
        group.setUserData(this);
        getChildren().add(line);

        // Initialize connections as null
        this.startConnection = null;
        this.endConnection = null;

        // Listeners for line points change to update orientation
        line.startXProperty().addListener((_, _, _) -> updateOrientationAttribute());
        line.startYProperty().addListener((_, _, _) -> updateOrientationAttribute());
        line.endXProperty().addListener((_, _, _) -> updateOrientationAttribute());
        line.endYProperty().addListener((_, _, _) -> updateOrientationAttribute());

        updateOrientationAttribute();
    }

    //Getter and setter for start and end snapped
    public boolean getStartSnapped() {
        return startSnapped;
    }


    public boolean getEndSnapped() {
        return endSnapped;
    }

    // Add getters and setters for the new connection fields
    public PhysicsObject getStartConnection() {
        return startConnection;
    }

    public void setStartConnection(PhysicsObject object) {
        this.startConnection = object;
        this.startSnapped = (object != null);
    }

    public PhysicsObject getEndConnection() {
        return endConnection;
    }

    public void setEndConnection(PhysicsObject object) {
        this.endConnection = object;
        this.endSnapped = (object != null);
    }

    public Line getLine() {
        return line;
    }

    public Circle getStartHandle() {
        return startHandle;
    }

    public Circle getEndHandle() {
        return endHandle;
    }

    private Circle createHandle(double x, double y) {
        Circle handle = new Circle(x, y, Sandbox.HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    public void addLineResizeListener() {
        startHandle.setOnMousePressed(_ -> isOnHandle = true);

        startHandle.setOnMouseDragged(event -> {
            // Always update positions, but still check for snapping
            line.setStartX(event.getX());
            line.setStartY(event.getY());
            startHandle.setCenterX(event.getX());
            startHandle.setCenterY(event.getY());

            // If we were snapped, break the connection temporarily while dragging
            if (startConnection != null) {
                startConnection.connectedRopes.remove(this);
                startConnection = null;
                startSnapped = false;
            }

            // Check for new snapping opportunities
            for (PhysicsObject physicsObject : physicsObjectList) {
                Snapping.snapRopeStart(physicsObject, this);
            }
            updateOrientationAttribute();
        });

        startHandle.setOnMouseReleased(_ -> {
            isOnHandle = false;
            Roof roof = Sandbox.getRoof();
            Snapping.snapToRoofIfIntersecting(startHandle, true, roof, this);

            if (startConnection instanceof Box box) {
                box.setBoxUnderRope();
                box.enforceConstraints(); // Enforce after positioning
            }
            else if (endConnection instanceof Box box) {
                box.setBoxUnderRope();
                box.enforceConstraints(); // Enforce after positioning
            }

            updateOrientationAttribute();
        });

        endHandle.setOnMousePressed(_ -> isOnHandle = true);

        endHandle.setOnMouseDragged(event -> {
            // Always update positions, but still check for snapping
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            endHandle.setCenterX(event.getX());
            endHandle.setCenterY(event.getY());

            // If we were snapped, break the connection temporarily while dragging
            if (endConnection != null) {
                endConnection.connectedRopes.remove(this);
                endConnection = null;
                endSnapped = false;
            }

            // Check for new snapping opportunities
            for (PhysicsObject physicsObject : physicsObjectList) {
                Snapping.snapRopeEnd(physicsObject, this);
            }
            updateOrientationAttribute();
        });

        endHandle.setOnMouseReleased(_ -> {
            isOnHandle = false;
            Roof roof = Sandbox.getRoof();
            Snapping.snapToRoofIfIntersecting(endHandle, false, roof, this);

            if (endConnection instanceof Box box) {
                box.setBoxUnderRope();
                box.enforceConstraints(); // Enforce after positioning
            }
            else if (startConnection instanceof Box box) {
                box.setBoxUnderRope();
                box.enforceConstraints(); // Enforce after positioning
            }

            updateOrientationAttribute();
        });

        // Keep your property listeners
        line.startXProperty().addListener((_, _, newVal) -> startHandle.setCenterX(newVal.doubleValue()));

        line.startYProperty().addListener((_, _, newVal) -> startHandle.setCenterY(newVal.doubleValue()));

        line.endXProperty().addListener((_, _, newVal) -> endHandle.setCenterX(newVal.doubleValue()));

        line.endYProperty().addListener((_, _, newVal) -> endHandle.setCenterY(newVal.doubleValue()));
    }

    public void addDragListener() {
        line.setOnMousePressed(event -> {
            isOnHandle = false; // Make sure we know we're on the line, not a handle
            line.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        line.setOnMouseDragged(event -> {
            if (!isOnHandle) {
                // Modified to only disconnect if dragging the entire rope
                if (startConnection != null) {
                    startConnection.connectedRopes.remove(this);
                    if (startConnection instanceof Box box) {
                        box.setBoxUnderRope();
                    }
                    else if (startConnection instanceof Pulley pulley){
                        pulley.updateBoxList();
                    }
                    startConnection = null;
                }

                if (endConnection != null) {
                    endConnection.connectedRopes.remove(this);
                    if (endConnection instanceof Box box) {
                        box.setBoxUnderRope();
                    }
                    else if (endConnection instanceof Pulley pulley){
                        pulley.updateBoxList();
                    }
                    endConnection = null;
                }
                startSnapped = false;
                endSnapped = false;

                double[] offset = (double[]) line.getUserData();
                double offsetX = event.getSceneX() - offset[0];
                double offsetY = event.getSceneY() - offset[1];

                line.setStartX(line.getStartX() + offsetX);
                line.setStartY(line.getStartY() + offsetY);
                line.setEndX(line.getEndX() + offsetX);
                line.setEndY(line.getEndY() + offsetY);

                startHandle.setCenterX(line.getStartX());
                startHandle.setCenterY(line.getStartY());
                endHandle.setCenterX(line.getEndX());
                endHandle.setCenterY(line.getEndY());

                line.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            }
        });

        // Add property listeners to update angles when rope positions change
        this.getLine().startXProperty().addListener((_, _, _) -> updateConnectedBoxAngles());
        this.getLine().startYProperty().addListener((_, _, _) -> updateConnectedBoxAngles());
        this.getLine().endXProperty().addListener((_, _,
                                                   _) -> updateConnectedBoxAngles());
        this.getLine().endYProperty().addListener((_, _, _) -> updateConnectedBoxAngles());
    }

    // Move this method outside of addDragListener and to the class level
    private void updateConnectedBoxAngles() {
        if (startConnection instanceof Box) {
            ((Box) startConnection).displayRopeAngle();
        }
        if (endConnection instanceof Box) {
            ((Box) endConnection).displayRopeAngle();
        }
    }
    @Override
    public double getCenterX() {
        return (line.getStartX() + line.getEndX()) / 2;
    }

    @Override
    public double getCenterY() {
        return (line.getStartY() + line.getEndY()) / 2;
    }

    public void updateOrientationAttribute() {
        double deltaX = line.getEndX() - line.getStartX();
        double deltaY = line.getEndY() - line.getStartY();
        double angleInRadians = Math.atan2(deltaY, deltaX);
        double angleInDegrees = Math.toDegrees(angleInRadians);

        // Normalize to 0-360 range
        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }

        // Calculating angle from the lower point to the higher point
        if (line.getStartY() > line.getEndY() ||
                (line.getStartY() == line.getEndY() && line.getStartX() > line.getEndX())) {
            // If start is below end or same height but to the right, flip the angle
            angleInDegrees = (angleInDegrees + 180) % 360;
        }

        orientation = angleInDegrees;
    }

    public double getOrientation() {
        return orientation;
    }


}