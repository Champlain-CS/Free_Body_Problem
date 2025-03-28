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
    private Line line;
    private Circle startHandle;
    private Circle endHandle;
    public Boolean startSnapped = false;
    public Boolean endSnapped = false;
    private List<PhysicsObject> physicsObjectList;
    private boolean isOnHandle = false;

    // Add these new fields to track connections
    private PhysicsObject startConnection;
    private PhysicsObject endConnection;

    public Rope(double startX, double startY, double endX, double endY, Color color, Boolean startSnapped, Boolean endSnapped, List<PhysicsObject> physicsObjectList) {
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
    }

    //Getter and setter for start and end snapped
    public Boolean getStartSnapped() {
        return startSnapped;
    }

    public void setStartSnapped(Boolean startSnapped) {
        this.startSnapped = startSnapped;
    }

    public Boolean getEndSnapped() {
        return endSnapped;
    }

    public void setEndSnapped(Boolean endSnapped) {
        this.endSnapped = endSnapped;
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
        startHandle.setOnMousePressed(event -> {
            isOnHandle = true;
        });

        startHandle.setOnMouseDragged(event -> {
            // Only update positions if not snapped
            if (startConnection == null) {
                line.setStartX(event.getX());
                line.setStartY(event.getY());
                startHandle.setCenterX(event.getX());
                startHandle.setCenterY(event.getY());
            }

            for (PhysicsObject physicsObject : physicsObjectList) {
                Snapping.snapRopeStart(physicsObject, this);
            }
        });

        startHandle.setOnMouseReleased(event -> {
            isOnHandle = false;
        });

        endHandle.setOnMousePressed(event -> {
            isOnHandle = true;
        });

        endHandle.setOnMouseDragged(event -> {
            // Only update positions if not snapped
            if (endConnection == null) {
                line.setEndX(event.getX());
                line.setEndY(event.getY());
                endHandle.setCenterX(event.getX());
                endHandle.setCenterY(event.getY());
            }

            for (PhysicsObject physicsObject : physicsObjectList) {
                Snapping.snapRopeEnd(physicsObject, this);
            }
        });

        endHandle.setOnMouseReleased(event -> {
            isOnHandle = false;
        });

        // Keep your property listeners
        line.startXProperty().addListener((obs, oldVal, newVal) -> {
            startHandle.setCenterX(newVal.doubleValue());
        });

        line.startYProperty().addListener((obs, oldVal, newVal) -> {
            startHandle.setCenterY(newVal.doubleValue());
        });

        line.endXProperty().addListener((obs, oldVal, newVal) -> {
            endHandle.setCenterX(newVal.doubleValue());
        });

        line.endYProperty().addListener((obs, oldVal, newVal) -> {
            endHandle.setCenterY(newVal.doubleValue());
        });
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
                    startConnection = null;
                }
                if (endConnection != null) {
                    endConnection.connectedRopes.remove(this);
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
    }
    @Override
    public double getCenterX() {
        return (line.getStartX() + line.getEndX()) / 2;
    }

    @Override
    public double getCenterY() {
        return (line.getStartY() + line.getEndY()) / 2;
    }
}