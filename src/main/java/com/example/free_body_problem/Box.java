package com.example.free_body_problem;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

public class Box extends PhysicsObject{
    public Rectangle rectangle;
    private Circle resizeHandle;
    private Circle rotateHandle;
    private TextField textField;
    private HBox massField;
    private Pane parentContainer;


    public boolean snappedToPlane = false;
    public Plane snappedPlane;


    protected VectorDisplay gravityVector;
    protected VectorDisplay normalVector;
    protected VectorDisplay frictionVector;
    protected VectorDisplay tensionVector; //May need multiple, or we do max 1 rope per box
    protected VectorDisplay netVector;

    protected double totalXForce, totalYForce;
    protected double angle; //used for vector calculations


    public Box(double x, double y, double width, double height, Color color, Pane parentContainer) {
        this.parentContainer = parentContainer;
        parentContainer.getStylesheets().add("BoxStyleSheet.css");

        rectangle = new Rectangle(x, y, width, height);
        rectangle.setUserData(this);
        Group group = new Group(rectangle);
        group.setUserData(this);
        getChildren().add(rectangle);

        rectangle.setFill(color);
        rectangle.setStroke(Color.BLACK);

        resizeHandle = createHandle(x + width, y + height);

        // Position the rotate handle in the top-right corner of the box
        rotateHandle = createHandle(x + width, y + height / 2);

        textField = new TextField();
        textField.setText("5");
        textField.setAlignment(Pos.CENTER);
        textField.getStyleClass().add("textField");


        Label kgLabel = new Label("kg");
        kgLabel.getStyleClass().add("label");

        massField = new HBox();
        //Shifting by hard values because hbox doesn't get created with a width
        massField.setLayoutX(this.getCenterX() - 40);
        massField.setLayoutY(this.getCenterY() - 14);
        massField.getChildren().addAll(textField, kgLabel);
        massField.getStyleClass().add("massField");



        parentContainer.getChildren().addAll(rectangle, massField, resizeHandle);



        addDragListener();
        addResizeListener();
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                parentContainer.requestFocus(); // Unfocus the TextField by requesting focus on the parent container
            }
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                parentContainer.requestFocus(); // Unfocus the TextField by requesting focus on the parent container
            }
        });

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

    public double getCenterX(){
        return rectangle.getX() + rectangle.getWidth() / 2;
    }

    public double getCenterY(){
        return rectangle.getY() + rectangle.getHeight() / 2;
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

            updateHandlePositions();

            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});

            // Update all connected ropes
            updateConnectedRopes();

            // Check for snapping to planes
            for (Node node : rectangle.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Line) {
                    Line plane = (Line) node;
                    Snapping.snapBoxToPlane(rectangle, plane);
                    // Update handle positions after snapping
                    updateHandlePositions();

                }
            }
        });
    }
    private static final double MIN_WIDTH = 50;
    private static final double MIN_HEIGHT = 50;

    public void addResizeListener() {
        resizeHandle.setOnMouseDragged(event -> {
            double newWidth = event.getX() - rectangle.getX();
            double newHeight = event.getY() - rectangle.getY();

            if (newWidth >= MIN_WIDTH) {
                rectangle.setWidth(newWidth);
                resizeHandle.setCenterX(event.getX());
                rotateHandle.setCenterX(event.getX());
            }

            if (newHeight >= MIN_HEIGHT) {
                rectangle.setHeight(newHeight);
                resizeHandle.setCenterY(event.getY());
                rotateHandle.setCenterY(event.getY() - rectangle.getHeight() / 2);
            }


            massField.setLayoutX(this.getCenterX() - massField.getWidth() / 2);
            massField.setLayoutY(this.getCenterY() - massField.getHeight() / 2);



        });
    }
    private void updateHandlePositions() {
        double angle = Math.toRadians(rectangle.getRotate()); // Get the rotation angle in radians
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Calculate the bottom-right corner of the box relative to its center
        double centerX = rectangle.getX() + rectangle.getWidth() / 2;
        double centerY = rectangle.getY() + rectangle.getHeight() / 2;
        double cornerX = rectangle.getWidth() / 2;
        double cornerY = rectangle.getHeight() / 2;

        // Rotate the corner point around the center
        double rotatedCornerX = centerX + cornerX * cos - cornerY * sin;
        double rotatedCornerY = centerY + cornerX * sin + cornerY * cos;

        // Update the resize handle's position to stay at the rotated bottom-right corner
        resizeHandle.setCenterX(rotatedCornerX);
        resizeHandle.setCenterY(rotatedCornerY);

        // Calculate the top-right corner of the box relative to its center
        cornerY = -rectangle.getHeight() / 2;

        // Rotate the corner point around the center
        double rotatedTopCornerX = centerX + cornerX * cos - cornerY * sin;
        double rotatedTopCornerY = centerY + cornerX * sin + cornerY * cos;

        // Update the rotate handle's position to stay at the rotated top-right corner
        rotateHandle.setCenterX(rotatedTopCornerX);
        rotateHandle.setCenterY(rotatedTopCornerY);

        // Update the text field's position to stay centered
        massField.setLayoutX(this.getCenterX() - massField.getWidth() / 2);
        massField.setLayoutY(this.getCenterY() - massField.getHeight() / 2);


    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}