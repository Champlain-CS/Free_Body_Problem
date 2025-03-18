package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class Rope extends Group {
    private Line line;
    private Circle startHandle;
    private Circle endHandle;
    public Boolean startSnapped = false;
    public Boolean endSnapped = false;
    private List<PhysicsObject> physicsObjectList;
    private boolean isOnHandle = false;


    public Rope(double startX, double startY, double endX, double endY, Color color, Boolean startSnapped, Boolean endSnapped, List<PhysicsObject> physicsObjectList) {
        line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);
        startHandle = createHandle(startX, startY);
        this.physicsObjectList = physicsObjectList;

        //DEBUG
        startHandle.setFill(Color.GREEN);
        endHandle = createHandle(endX, endY);

        line.setUserData(this);
        Group group = new Group(line);
        group.setUserData(this);
        getChildren().add(line);


    }

    //Gettter and setter for start and end snapped
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
            line.setStartX(event.getX());
            line.setStartY(event.getY());
            startHandle.setCenterX(event.getX());
            startHandle.setCenterY(event.getY());

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
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            endHandle.setCenterX(event.getX());
            endHandle.setCenterY(event.getY());

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
                for (PhysicsObject physicsObject : physicsObjectList) {
                    physicsObject.connectedRopes.remove(this);
                }

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
}