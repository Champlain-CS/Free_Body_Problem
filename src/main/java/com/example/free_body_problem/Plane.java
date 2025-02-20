package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Plane {
    private Line line;
    private Circle startHandle;
    private Circle endHandle;

    public Plane(double startX, double startY, double endX, double endY, Color color) {
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
        Circle handle = new Circle(x, y, DraggableShapesApp.HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    public void addLineResizeListener() {
        startHandle.setOnMouseDragged(event -> {
            line.setStartX(event.getX());
            line.setStartY(event.getY());
            startHandle.setCenterX(event.getX());
            startHandle.setCenterY(event.getY());
        });

        endHandle.setOnMouseDragged(event -> {
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            endHandle.setCenterX(event.getX());
            endHandle.setCenterY(event.getY());
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
}