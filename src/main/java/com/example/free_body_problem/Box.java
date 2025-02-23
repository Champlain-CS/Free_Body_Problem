package com.example.free_body_problem;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line; // Add this import
import javafx.scene.Node;
import javafx.geometry.Pos;

public class Box {
    private Rectangle rectangle;
    private Circle resizeHandle;
    private Circle rotateHandle;
    private TextField textField;

    public Box(double x, double y, double width, double height, Color color) {
        rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        rectangle.setStroke(Color.BLACK);

        resizeHandle = createHandle(x + width, y + height);
        rotateHandle = createHandle(x + width, y + height / 2);

        textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(50); // Set preferred width
        textField.setLayoutX(x + width / 2 - textField.getPrefWidth() / 2);
        textField.setLayoutY(y + height / 2 - textField.getPrefHeight() / 2);

        addDragListener();
        addResizeListener();
        addRotateListener();
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

    public TextField getTextField() {
        return textField;
    }

    private Circle createHandle(double x, double y) {
        Circle handle = new Circle(x, y, Sandbox.HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    public void addDragListener() {
        rectangle.setOnMousePressed(event -> {
            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        rectangle.setOnMouseDragged(event -> {
            double[] offset = (double[]) rectangle.getUserData();
            double offsetX = event.getSceneX() - offset[0];
            double offsetY = event.getSceneY() - offset[1];

            rectangle.setX(rectangle.getX() + offsetX);
            rectangle.setY(rectangle.getY() + offsetY);

            resizeHandle.setCenterX(resizeHandle.getCenterX() + offsetX);
            resizeHandle.setCenterY(resizeHandle.getCenterY() + offsetY);
            rotateHandle.setCenterX(rotateHandle.getCenterX() + offsetX);
            rotateHandle.setCenterY(rotateHandle.getCenterY() + offsetY);

            textField.setLayoutX(rectangle.getX() + rectangle.getWidth() / 2 - textField.getPrefWidth() / 2);
            textField.setLayoutY(rectangle.getY() + rectangle.getHeight() / 2 - textField.getPrefHeight() / 2);

            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});

            // Check for snapping to planes
            for (Node node : rectangle.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Line) {
                    Line plane = (Line) node;
                    Snapping.snapBoxToPlane(rectangle, plane);
                }
            }
        });
    }

    public void addResizeListener() {
        resizeHandle.setOnMousePressed(event -> {
            resizeHandle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        resizeHandle.setOnMouseDragged(event -> {
            double[] offset = (double[]) resizeHandle.getUserData();
            double offsetX = event.getSceneX() - offset[0];
            double offsetY = event.getSceneY() - offset[1];

            rectangle.setWidth(rectangle.getWidth() + offsetX);
            rectangle.setHeight(rectangle.getHeight() + offsetY);

            resizeHandle.setCenterX(rectangle.getX() + rectangle.getWidth());
            resizeHandle.setCenterY(rectangle.getY() + rectangle.getHeight());

            textField.setLayoutX(rectangle.getX() + rectangle.getWidth() / 2 - textField.getPrefWidth() / 2);
            textField.setLayoutY(rectangle.getY() + rectangle.getHeight() / 2 - textField.getPrefHeight() / 2);

            resizeHandle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        rectangle.xProperty().addListener((obs, oldVal, newVal) -> {
            resizeHandle.setCenterX(newVal.doubleValue() + rectangle.getWidth());
            textField.setLayoutX(newVal.doubleValue() + rectangle.getWidth() / 2 - textField.getPrefWidth() / 2);
        });

        rectangle.yProperty().addListener((obs, oldVal, newVal) -> {
            resizeHandle.setCenterY(newVal.doubleValue() + rectangle.getHeight());
            textField.setLayoutY(newVal.doubleValue() + rectangle.getHeight() / 2 - textField.getPrefHeight() / 2);
        });

        rectangle.widthProperty().addListener((obs, oldVal, newVal) -> {
            resizeHandle.setCenterX(rectangle.getX() + newVal.doubleValue());
            textField.setLayoutX(rectangle.getX() + newVal.doubleValue() / 2 - textField.getPrefWidth() / 2);
        });

        rectangle.heightProperty().addListener((obs, oldVal, newVal) -> {
            resizeHandle.setCenterY(rectangle.getY() + newVal.doubleValue());
            textField.setLayoutY(rectangle.getY() + newVal.doubleValue() / 2 - textField.getPrefHeight() / 2);
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
            rotateHandle.setCenterY(rectangle.getY() + newVal.doubleValue());
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