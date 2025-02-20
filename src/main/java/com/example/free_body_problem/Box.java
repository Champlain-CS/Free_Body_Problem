package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Box {
    private Rectangle rectangle;
    private Circle resizeHandle;
    private Circle rotateHandle;

    public Box(double x, double y, double width, double height, Color color) {
        rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        rectangle.setStroke(Color.BLACK);

        resizeHandle = createHandle(x + width, y + height);
        rotateHandle = createHandle(x + width, y + height / 2);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Circle getResizeHandle() {
        return resizeHandle;
    }

    public Circle getRotateHandle() {
        return rotateHandle;
    }

    private Circle createHandle(double x, double y) {
        Circle handle = new Circle(x, y, DraggableShapesApp.HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    public void addDragListener() {
        rectangle.setOnMousePressed(event -> {
            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        rectangle.setOnMouseDragged(event -> {
            double[] offset = (double[]) rectangle.getUserData();
            rectangle.setX(rectangle.getX() + (event.getSceneX() - offset[0]));
            rectangle.setY(rectangle.getY() + (event.getSceneY() - offset[1]));
            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        rectangle.setOnMouseReleased(event -> {
            // Handle mouse release if needed
        });
    }

    public void addRotateListener() {
        rotateHandle.setFill(Color.BLUE);
        rotateHandle.setOnMouseDragged(event -> {
            double centerX = rectangle.getX() + rectangle.getWidth() / 2;
            double centerY = rectangle.getY() + rectangle.getHeight() / 2;
            double angle = Math.toDegrees(Math.atan2(event.getY() - centerY, event.getX() - centerX));
            rectangle.setRotate(angle);
            rotateHandle.setCenterX(event.getX());
            rotateHandle.setCenterY(event.getY());
        });

        rectangle.xProperty().addListener((obs, oldVal, newVal) -> {
            rotateHandle.setCenterX(newVal.doubleValue() + rectangle.getWidth());
        });

        rectangle.yProperty().addListener((obs, oldVal, newVal) -> {
            rotateHandle.setCenterY(newVal.doubleValue() + rectangle.getHeight() / 2);
        });

        rectangle.widthProperty().addListener((obs, oldVal, newVal) -> {
            rotateHandle.setCenterX(rectangle.getX() + newVal.doubleValue());
        });

        rectangle.heightProperty().addListener((obs, oldVal, newVal) -> {
            rotateHandle.setCenterY(rectangle.getY() + rectangle.getHeight() / 2);
        });

        rectangle.rotateProperty().addListener((obs, oldVal, newVal) -> {
            double centerX = rectangle.getX() + rectangle.getWidth() / 2;
            double centerY = rectangle.getY() + rectangle.getHeight() / 2;
            double angle = Math.toRadians(newVal.doubleValue());
            double handleX = centerX + (rectangle.getWidth() / 2) * Math.cos(angle);
            double handleY = centerY + (rectangle.getWidth() / 2) * Math.sin(angle);
            rotateHandle.setCenterX(handleX);
            rotateHandle.setCenterY(handleY);
        });
    }
}