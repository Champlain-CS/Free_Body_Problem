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
import java.util.Map;

import static java.lang.Math.abs;

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
        textField.setText("10");
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
            lastDragDelta = null;
            // Store initial press position
            rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
            lastDragDelta = null; // Reset on new drag
            alignConnectedPulleys();
        });

        rectangle.setOnMouseDragged(event -> {
            double[] initialPress = (double[]) rectangle.getUserData();
            double totalDragDistance = Math.sqrt(
                    Math.pow(event.getSceneX() - initialPress[0], 2) +
                            Math.pow(event.getSceneY() - initialPress[1], 2)
            );

            // If box is snapped and we've dragged far enough, unsnap it
            if (isSnapped && totalDragDistance >= 50) {
                isSnapped = false;
                snappedToPlane = false;
                snappedPlane = null;
                rectangle.setRotate(0);
                resizeHandle.setFill(Color.RED);

                double mouseOffsetX = initialPress[0] - (rectangle.getX() + rectangle.getWidth() / 2);
                double mouseOffsetY = initialPress[1] - (rectangle.getY() + rectangle.getHeight() / 2);

                // Calculate new position with bounds checking
                double newX = event.getSceneX() - mouseOffsetX - rectangle.getWidth() / 2;
                double newY = event.getSceneY() - mouseOffsetY - rectangle.getHeight() / 2;

                // Constrain to parent bounds
                newX = Math.max(0, Math.min(newX, parentContainer.getWidth() - rectangle.getWidth()));
                newY = Math.max(0, Math.min(newY, parentContainer.getHeight() - rectangle.getHeight()));
                applyRopeConstraints(newX);
                rectangle.setX(newX);
                rectangle.setY(newY);

                updateHandlePositions();
                rectangle.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
                return;
            }

            if (!isSnapped) {
                double offsetX = event.getSceneX() - initialPress[0];
                double offsetY = event.getSceneY() - initialPress[1];

                // Calculate proposed new position
                double newX = rectangle.getX() + offsetX;
                double newY = rectangle.getY() + offsetY;

                // Constrain to parent bounds
                newX = Math.max(0, Math.min(newX, parentContainer.getWidth() - rectangle.getWidth()));
                newY = Math.max(0, Math.min(newY, parentContainer.getHeight() - rectangle.getHeight()));
                newX = applyRopeConstraints(newX);
                // Only update if position has changed
                if (newX != rectangle.getX() || newY != rectangle.getY()) {
                    rectangle.setX(newX);
                    rectangle.setY(newY);

                    initialPress[0] = event.getSceneX();
                    initialPress[1] = event.getSceneY();
                    rectangle.setUserData(initialPress);
                    lastDragDelta = Math.sqrt(offsetX*offsetX + offsetY*offsetY);

                    updateHandlePositions();
                    updateConnectedRopes();
                    Snapping.snapBoxToPlane(this, planeList);
                    updateHandlePositions();
                }
            }
        });

        rectangle.setOnMouseReleased(event -> {
            lastDragDelta = null;
            setBoxUnderRope();
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
    public void alignConnectedPulleys() {
        for (java.util.Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
            Rope rope = entry.getKey();
            PhysicsObject otherEnd;

            // Check if the other end is a pulley
            if (entry.getValue()) { // This box is at the start of the rope
                otherEnd = rope.getEndConnection();
            } else { // This box is at the end of the rope
                otherEnd = rope.getStartConnection();
            }

            if (otherEnd instanceof Pulley) {
                Snapping.alignPulleyWithBox((Pulley) otherEnd, this, rope);

                // Handle the case where the pulley has multiple ropes connected
                for (java.util.Map.Entry<Rope, Boolean> pulleyEntry : otherEnd.connectedRopes.entrySet()) {
                    Rope otherRope = pulleyEntry.getKey();

                    // Skip the rope we just processed
                    if (otherRope == rope) continue;

                    // Get the object at the other end of this rope
                    PhysicsObject thirdObj = null;
                    if (pulleyEntry.getValue()) { // Pulley is at the start of the rope
                        thirdObj = otherRope.getEndConnection();
                    } else { // Pulley is at the end of the rope
                        thirdObj = otherRope.getStartConnection();
                    }

                    // If that object is also a box, align it with the pulley's new position
                    if (thirdObj instanceof Box) {
                        ((Box) thirdObj).updateConnectedRopes();
                    }
                }
            }
        }
    }
    public void resetNetVectorComponents() {
        totalXForce =0;
        totalYForce =0;
    }

    public void setPosition(double x, double y) {
        // Apply basic container constraints first
        double constrainedX = Math.max(0, Math.min(x, parentContainer.getWidth() - rectangle.getWidth()));
        double constrainedY = Math.max(0, Math.min(y, parentContainer.getHeight() - rectangle.getHeight()));

        // Then apply rope constraints if needed
        if (connectedRopes.size() > 1) {
            enforceConstraints();
            return; // Constraints already applied in enforceConstraints()
        }

        rectangle.setX(constrainedX);
        rectangle.setY(constrainedY);
        updateHandlePositions();
        updateConnectedRopes();
    }

    public void setBoxUnderRope() {
        if (connectedRopes.size() == 1 && !isSnapped) {
            // Get the rope that's connected to the box
            Map.Entry<Rope, Boolean> hashMap = connectedRopes.entrySet().iterator().next();
            Rope rope = hashMap.getKey();
            Boolean start = hashMap.getValue();

            // Get both ends of the rope
            double startY = rope.getLine().getStartY();
            double endY = rope.getLine().getEndY();
            double distance = abs(startY - endY);

            // Determine which end is higher (lower y value)

            // If y values are equal, no change needed

            // Position the box under the connected end
            if (start && rope.getEndSnapped() == false) {
                if (startY < endY) {
                    startY += distance*2;
                }
                setPosition(rope.getLine().getEndX() - getRectangle().getWidth() / 2, startY);

            } else if (!start && rope.getStartSnapped() == false) {
                if (startY > endY) {
                    endY += distance*2;
                }
                setPosition(rope.getLine().getStartX() - getRectangle().getWidth() / 2, endY);
            }
            updateConnectedRopes();
        }
    }

    private double applyRopeConstraints(double newX) {

        if (connectedRopes.size() > 1 ) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            // Find the leftmost and rightmost connection points
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();
                System.out.println("Start" + rope.getStartSnapped());
                System.out.println("End" + rope.getEndSnapped());

                double ropeX;
                if (isStartConnected) {
                    ropeX = rope.getLine().getEndX();
                } else {
                    ropeX = rope.getLine().getStartX();
                }

                // The box can't move past the connection point
                if (ropeX < minX) {
                    minX = ropeX;
                }
                if (ropeX > maxX) {
                    maxX = ropeX;
                }
            }
            maxX -= rectangle.getWidth()/2;
            // Constrain the box position based on the rope connections
            return  Math.max(minX - rectangle.getWidth()/2, Math.min(newX, maxX));
        }
        return newX;
    }

    public void enforceConstraints() {
        if (connectedRopes.size() > 1) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            // Find boundary constraints from connected ropes
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();

                // Get the non-connected end position
                double ropeX = isStartConnected ?
                        rope.getLine().getEndX() :
                        rope.getLine().getStartX();

                if (ropeX < minX) minX = ropeX;
                if (ropeX > maxX) maxX = ropeX;
            }

            // Apply constraints
            double constrainedX = Math.max(minX, Math.min(rectangle.getX(), maxX));
            if (constrainedX != rectangle.getX()) {
                setPosition(constrainedX, rectangle.getY());
            }
        }
    }
}