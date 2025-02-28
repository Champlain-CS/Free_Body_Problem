package com.example.free_body_problem;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

public class Box {
    private Rectangle rectangle;
    private Circle resizeHandle;
    private TextField textField;
    private Pane parentContainer;

    public Box(double x, double y, double width, double height, Color color, Pane parentContainer) {
        this.parentContainer = parentContainer;

        rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        rectangle.setStroke(Color.BLACK);

        resizeHandle = createHandle(x + width, y + height);

        textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(50); // Set preferred width
        textField.setLayoutX(x + width / 2 - textField.getPrefWidth() / 2);
        textField.setLayoutY(y + height / 2 - textField.getPrefHeight() / 2);

        parentContainer.getChildren().addAll(rectangle, textField, resizeHandle);

        addDragListener();
        addResizeListener();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Circle getResizeHandle() {
        return resizeHandle;
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

            textField.setLayoutX(rectangle.getX() + rectangle.getWidth() / 2 - textField.getPrefWidth() / 2);
            textField.setLayoutY(rectangle.getY() + rectangle.getHeight() / 2 - textField.getPrefHeight() / 2);

            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
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
}