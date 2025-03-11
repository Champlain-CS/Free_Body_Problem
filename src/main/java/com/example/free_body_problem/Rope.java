package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Rope {
    private Line line;
    private Circle startHandle;
    private Circle endHandle;

    public Rope(double startX, double startY, double endX, double endY, Color color) {
        line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);

        startHandle = createHandle(startX, startY);
        endHandle = createHandle(endX, endY);
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
        startHandle.setOnMouseDragged(event -> {
            line.setStartX(event.getX());
            line.setStartY(event.getY());
            startHandle.setCenterX(event.getX());
            startHandle.setCenterY(event.getY());

            for (Node node : line.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Rectangle) {
                    Rectangle box = (Rectangle) node;
                    Snapping.snapRopeStart(box, line);
                    // Update handle positions after snapping
                } else if (node instanceof Group) {
                    Group pivot = (Group) node;
                    Snapping.snapRopeStart(pivot, line);
                    // Update handle positions after snapping
                }
            }
        });

        endHandle.setOnMouseDragged(event -> {
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            endHandle.setCenterX(event.getX());
            endHandle.setCenterY(event.getY());

            for (Node node : line.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Rectangle) {
                    Rectangle box = (Rectangle) node;
                    Snapping.snapRopeEnd(box, line);
                    // Update handle positions after snapping
                } else if (node instanceof Group) {
                    Group pivot = (Group) node;
                    Snapping.snapRopeEnd(pivot, line);
                    // Update handle positions after snapping
                }
            }
        });

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
            line.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        line.setOnMouseDragged(event -> {
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
        });
    }
}