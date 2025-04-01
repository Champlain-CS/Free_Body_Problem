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

public class Box extends PhysicsObject {
    public Rectangle rectangle;
    private Circle resizeHandle;
    private Circle rotateHandle;
    private TextField textField;
    private HBox massField;
    private Pane parentContainer;
    private Double lastDragDelta = null;


    public double gravityForce;
    public double normalForce;
    public double frictionForce;
    public boolean snappedToPlane = false;
    public Plane snappedPlane;
    boolean isSnapped = false;

    protected VectorDisplay gravityVector;
    protected VectorDisplay normalVector;
    protected VectorDisplay frictionVector;
    protected VectorDisplay tensionVector; //May need multiple, or we do max 1 rope per box
    protected VectorDisplay netVector;
    public List<Plane> planeList;

    public double totalXForce, totalYForce;
    public double angle; //used for vector calculations


    public Box(double x, double y, double width, double height, Color color, Pane parentContainer, List<Plane> planeList) {
        this.parentContainer = parentContainer;
        parentContainer.getStylesheets().add("BoxStyleSheet.css");

        this.planeList = planeList;
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
        restrictTextFieldToNumbers(textField);



        parentContainer.getChildren().addAll(rectangle, massField);


        addDragListener();
        addResizeListener();
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                parentContainer.requestFocus(); // Unfocus the TextField by requesting focus on the parent container
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

    public Double getLastDragDelta() {
        return lastDragDelta;
    }

    public void setLastDragDelta(Double delta) {
        this.lastDragDelta = delta;
    }

    private Circle createHandle(double x, double y) {
        Circle handle = new Circle(x, y, Sandbox.HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    public double getCenterX() {
        return rectangle.getX() + rectangle.getWidth() / 2;
    }

    public double getCenterY() {
        return rectangle.getY() + rectangle.getHeight() / 2;
    }


    public void addDragListener() {
        rectangle.setOnMousePressed(event -> {
            // Store initial press position
            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            lastDragDelta = null; // Reset on new drag
        });

        rectangle.setOnMouseDragged(event -> {
            double[] initialPress = (double[]) rectangle.getUserData();

            // Calculate total distance from initial press position
            double totalDragDistance = Math.sqrt(
                    Math.pow(event.getSceneX() - initialPress[0], 2) +
                            Math.pow(event.getSceneY() - initialPress[1], 2)
            );

            // If box is snapped and we've dragged far enough, unsnap it
            if (isSnapped && totalDragDistance >= 50) {
                // Unsnap the box
                isSnapped = false;
                snappedToPlane = false;
                snappedPlane = null;
                rectangle.setRotate(0); // Reset rotation
                resizeHandle.setFill(Color.RED); // Reset handle color

                // Calculate the offset from center to where the mouse was initially pressed
                double mouseOffsetX = initialPress[0] - (rectangle.getX() + rectangle.getWidth() / 2);
                double mouseOffsetY = initialPress[1] - (rectangle.getY() + rectangle.getHeight() / 2);

                // Set the box position maintaining the same relative mouse position
                rectangle.setX(event.getSceneX() - mouseOffsetX - rectangle.getWidth() / 2);
                rectangle.setY(event.getSceneY() - mouseOffsetY - rectangle.getHeight() / 2);

                // Update the handle positions
                updateHandlePositions();

                // Store new reference point for future drag calculations
                rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
                return; // Skip the rest of the method for this drag event
            }

            // Only continue with drag if not snapped
            if (!isSnapped) {
                double offsetX = event.getSceneX() - initialPress[0];
                double offsetY = event.getSceneY() - initialPress[1];

                // Update reference for next drag event
                initialPress[0] = event.getSceneX();
                initialPress[1] = event.getSceneY();
                rectangle.setUserData(initialPress);

                lastDragDelta = Math.sqrt(offsetX*offsetX + offsetY*offsetY);

                // Move the box
                rectangle.setX(rectangle.getX() + offsetX);
                rectangle.setY(rectangle.getY() + offsetY);

                // Update handle positions
                updateHandlePositions();

                // Update all connected ropes
                updateConnectedRopes();

                // Check for snapping to planes
                Snapping.snapBoxToPlane(this, planeList);

                // Update handle positions again after potential snapping
                updateHandlePositions();
            }
        });

        rectangle.setOnMouseReleased(event -> {
            lastDragDelta = null; // Reset when done dragging
        });
    }

    private static final double MIN_WIDTH = 50;
    private static final double MIN_HEIGHT = 50;

    public void addResizeListener() {
        resizeHandle.setOnMouseDragged(event -> {
            // Only allow resizing if not snapped to a plane
            if (!isSnapped) {
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
            }
        });

        // Update cursor based on snap status
        resizeHandle.setOnMouseEntered(event -> {
            if (!isSnapped) {
                resizeHandle.setCursor(Cursor.SE_RESIZE);
            } else {
                resizeHandle.setCursor(Cursor.DEFAULT);
            }
        });
    }

    private void updateHandlePositions() {
        // Update the resize handle position
        if (isSnapped) {
            double angle = Math.toRadians(rectangle.getRotate());
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

            // Update the resize handle's position
            resizeHandle.setCenterX(rotatedCornerX);
            resizeHandle.setCenterY(rotatedCornerY);

            // Calculate the top-right corner of the box
            cornerY = -rectangle.getHeight() / 2;
            double rotatedTopCornerX = centerX + cornerX * cos - cornerY * sin;
            double rotatedTopCornerY = centerY + cornerX * sin + cornerY * cos;

            // Update the rotate handle's position
            rotateHandle.setCenterX(rotatedTopCornerX);
            rotateHandle.setCenterY(rotatedTopCornerY);
        } else {
            // Simpler positioning for unsnapped boxes
            resizeHandle.setCenterX(rectangle.getX() + rectangle.getWidth());
            resizeHandle.setCenterY(rectangle.getY() + rectangle.getHeight());
            rotateHandle.setCenterX(rectangle.getX() + rectangle.getWidth());
            rotateHandle.setCenterY(rectangle.getY() + rectangle.getHeight() / 2);
        }

        // Update the mass field position
        massField.setLayoutX(getCenterX() - massField.getWidth() / 2);
        massField.setLayoutY(getCenterY() - massField.getHeight() / 2);
    }

    private void restrictTextFieldToNumbers(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }
}