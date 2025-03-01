package com.example.free_body_problem;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Plane {
    private Line line;
    private Circle startHandle;
    private Circle endHandle;
    private static final double SNAP_ANGLE_THRESHOLD = 5;
    private boolean isTransformMode = true;

    public Plane(double startX, double startY, double endX, double endY, Color color) {
        line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);

        startHandle = createHandle(startX, startY, Color.RED); // Start handle is red (transform)
        endHandle = createHandle(endX, endY, Color.RED); // End handle is red (transform)

        addLineResizeListener();
        addDragListener();
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

    private Circle createHandle(double x, double y, Color color) {
        Circle handle = new Circle(x, y, Sandbox.HANDLE_RADIUS);
        handle.setFill(color);
        return handle;
    }

    public void addLineResizeListener() {
        startHandle.setOnMouseDragged(event -> {
            if (isTransformMode) {
                line.setStartX(event.getX());
                line.setStartY(event.getY());
                startHandle.setCenterX(event.getX());
                startHandle.setCenterY(event.getY());
            } else {
                double angle = Math.toDegrees(Math.atan2(event.getY() - line.getEndY(), event.getX() - line.getEndX()));
                angle = (angle % 360 + 360) % 360; // Normalize the angle to be between 0 and 360

                // Snap the angle to 0, 90, 180, or 360 if within the threshold
                angle = snapAngleToClosest(angle);

                double length = Math.hypot(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY());
                double startX = line.getEndX() - length * Math.cos(Math.toRadians(angle));
                double startY = line.getEndY() - length * Math.sin(Math.toRadians(angle));

                line.setStartX(startX);
                line.setStartY(startY);
                startHandle.setCenterX(startX);
                startHandle.setCenterY(startY);
            }
        });

        endHandle.setOnMouseDragged(event -> {
            if (isTransformMode) {
                line.setEndX(event.getX());
                line.setEndY(event.getY());
                endHandle.setCenterX(event.getX());
                endHandle.setCenterY(event.getY());
            } else {
                double angle = Math.toDegrees(Math.atan2(event.getY() - line.getStartY(), event.getX() - line.getStartX()));
                angle = (angle % 360 + 360) % 360; // Normalize the angle to be between 0 and 360

                // Snap the angle to 0, 90, 180, or 360 if within the threshold
                angle = snapAngleToClosest(angle);

                double length = Math.hypot(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY());
                double endX = line.getStartX() + length * Math.cos(Math.toRadians(angle));
                double endY = line.getStartY() + length * Math.sin(Math.toRadians(angle));

                line.setEndX(endX);
                line.setEndY(endY);
                endHandle.setCenterX(endX);
                endHandle.setCenterY(endY);
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

    public void addKeyListener() {
        System.out.println("addKeyListener called");
        line.getScene().setOnKeyPressed(event -> {
            System.out.println("Key pressed: " + event.getCode());
            if (event.getCode() == KeyCode.R) {
                isTransformMode = !isTransformMode;
                updateHandleColors();
                System.out.println("Transform mode toggled: " + isTransformMode);
            }
        });
    }

    private void updateHandleColors() {
        if (isTransformMode) {
            startHandle.setFill(Color.RED);
            endHandle.setFill(Color.RED);
        } else {
            startHandle.setFill(Color.BLUE);
            endHandle.setFill(Color.BLUE);
        }
    }

    private double snapAngleToClosest(double angle) {
        double[] snapAngles = {0, 90, 180, 360};
        for (double snapAngle : snapAngles) {
            if (Math.abs(angle - snapAngle) <= SNAP_ANGLE_THRESHOLD) {
                return snapAngle;
            }
        }
        return angle;
    }
}