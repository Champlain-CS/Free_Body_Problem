package com.example.free_body_problem;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

public class Box extends Group{
    public Rectangle rectangle;
    private Circle resizeHandle;
    private Circle rotateHandle;
    private TextField textField;
    private Pane parentContainer;
    public Boolean hasRopeStartSnapped = false;
    public Boolean hasRopeEndSnapped = false;
    public Rope snappedRope;
    public Boolean snappedToPlane = false;
    public Plane snappedPlane;


    protected VectorDisplay gravityVector;


    public Box(double x, double y, double width, double height, Color color, Pane parentContainer) {
        this.parentContainer = parentContainer;

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
        textField.setPrefWidth(50); // Set preferred width
        textField.setLayoutX(x + width / 2 - textField.getPrefWidth() / 2);
        textField.setLayoutY(y + height / 2 - textField.getPrefHeight() / 2 - 0.08*height);

        parentContainer.getChildren().addAll(rectangle, textField, resizeHandle);

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

            updateHandlePositions();
            resizeHandle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });


        rectangle.xProperty().addListener((obs, oldVal, newVal) -> updateHandlePositions());
        rectangle.yProperty().addListener((obs, oldVal, newVal) -> updateHandlePositions());
        rectangle.widthProperty().addListener((obs, oldVal, newVal) -> updateHandlePositions());
        rectangle.heightProperty().addListener((obs, oldVal, newVal) -> updateHandlePositions());
        rectangle.rotateProperty().addListener((obs, oldVal, newVal) -> updateHandlePositions());
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
        textField.setLayoutX(centerX - textField.getPrefWidth() / 2);
        textField.setLayoutY(centerY - textField.getPrefHeight() / 2);

        if (hasRopeStartSnapped){
            snappedRope.getLine().setStartX(centerX);
            snappedRope.getLine().setStartY(centerY);
            hasRopeEndSnapped = false;
        }

        if (hasRopeEndSnapped){
            snappedRope.getLine().setEndX(centerX);
            snappedRope.getLine().setEndY(centerY);
            hasRopeStartSnapped = false;
        }
    }

}