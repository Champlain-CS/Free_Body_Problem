package com.example.free_body_problem;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class Box extends PhysicsObject {
    public Rectangle rectangle;
    private final Circle resizeHandle;
    private final Circle rotateHandle;
    private final TextField textField;
    private final HBox massField;
    private final Pane parentContainer;


    public boolean snappedToPlane = false;
    public boolean isNetSet = false;
    public boolean isPulled = false;
    boolean isSnapped = false;
    boolean isSliding;
    public Plane snappedPlane;


    protected VectorDisplay gravityVector;
    protected VectorDisplay normalVector;
    protected VectorDisplay frictionVector;
    protected VectorDisplay tensionVector1;
    protected VectorDisplay tensionVector2;
    protected VectorDisplay netVector;
    public List<Plane> planeList;

    public double totalXForce, totalYForce;


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

                    updateHandlePositions();
                    updateConnectedRopes();
                    Snapping.snapBoxToPlane(this, planeList);
                    updateHandlePositions();
                }
            }
        });

        rectangle.setOnMouseReleased(_ -> setBoxUnderRope());
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
        resizeHandle.setOnMouseEntered(_ -> {
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
        textField.textProperty().addListener((_, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
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
            // Instead of calling enforceConstraints() which would cause recursion,
            // apply the constraint logic directly here
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

            // Apply constraints directly
            constrainedX = Math.max(minX - rectangle.getWidth()/2, Math.min(constrainedX, maxX - rectangle.getWidth()/2));
        }

        // Set the position with all constraints applied
        rectangle.setX(constrainedX);
        rectangle.setY(constrainedY - 50);
        rectangle.setRotate(0);
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
            if (start && !rope.getEndSnapped() || (rope.getEndConnection() instanceof Roof) || (rope.getEndConnection() instanceof Pulley)) {
                if (startY < endY) {
                    startY += distance*2;
                }
                setPosition(rope.getLine().getEndX() - getRectangle().getWidth() / 2, startY);

            } else if (!start && !rope.getStartSnapped() || (rope.getStartConnection() instanceof Roof) || (rope.getStartConnection() instanceof Pulley)) {
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

            // Apply constraints directly to rectangle instead of calling setPosition
            double constrainedX = Math.max(minX - rectangle.getWidth()/2, Math.min(rectangle.getX(), maxX - rectangle.getWidth()/2));
            if (constrainedX != rectangle.getX()) {
                rectangle.setX(constrainedX);
                updateHandlePositions();
                updateConnectedRopes();
            }
        }
    }

    public void displayRopeAngle() {
        // Only proceed if exactly two ropes are connected
        if (connectedRopes.size() != 2) {
            removeAngleDisplay();
            return;
        }

        // Get the two ropes
        Rope[] ropes = connectedRopes.keySet().toArray(new Rope[2]);
        Rope rope1 = ropes[0];
        Rope rope2 = ropes[1];

        // Calculate angles from box center to rope endpoints
        double boxCenterX = getCenterX();
        double boxCenterY = getCenterY();

        // Get the other ends of each rope relative to the box
        double rope1EndX, rope1EndY, rope2EndX, rope2EndY;

        if (rope1.getStartConnection() == this) {
            rope1EndX = rope1.getLine().getEndX();
            rope1EndY = rope1.getLine().getEndY();
        } else {
            rope1EndX = rope1.getLine().getStartX();
            rope1EndY = rope1.getLine().getStartY();
        }

        if (rope2.getStartConnection() == this) {
            rope2EndX = rope2.getLine().getEndX();
            rope2EndY = rope2.getLine().getEndY();
        } else {
            rope2EndX = rope2.getLine().getStartX();
            rope2EndY = rope2.getLine().getStartY();
        }

        // Calculate vectors from box to rope endpoints
        double vector1X = rope2EndX - boxCenterX;
        double vector1Y = rope2EndY - boxCenterY;
        double vector2X = rope1EndX - boxCenterX;
        double vector2Y = rope1EndY - boxCenterY;

        // Calculate the angle between the vectors (in degrees)
        double dotProduct = vector1X * vector2X + vector1Y * vector2Y;
        double magnitude1 = Math.sqrt(vector1X * vector1X + vector1Y * vector1Y);
        double magnitude2 = Math.sqrt(vector2X * vector2X + vector2Y * vector2Y);

        double angleCos = dotProduct / (magnitude1 * magnitude2);
        // Clamp to valid range to avoid floating-point errors
        angleCos = Math.min(1.0, Math.max(-1.0, angleCos));

        double angleDegrees = Math.toDegrees(Math.acos(angleCos));

        // Create or update the angle display
        createAngleDisplay(boxCenterX, boxCenterY, vector1X, vector1Y, vector2X, vector2Y, angleDegrees);
    }


    // Create visual elements to display the angle
    private javafx.scene.shape.Arc angleArc;
    private javafx.scene.text.Text angleText;

    private void createAngleDisplay(double centerX, double centerY,
                                    double v1x, double v1y, double v2x, double v2y, double angleDegrees) {
        // Remove any existing angle display
        removeAngleDisplay();

        // Calculate arc parameters
        double radius = 30; // Size of arc
        double angle1 = Math.toDegrees(Math.atan2(-v1y, v1x)); // Convert to JavaFX angle system
        double angle2 = Math.toDegrees(Math.atan2(-v2y, v2x));
        // normalize angles to 0-360 degrees
        angle1 = (angle1 + 360) % 360;
        angle2 = (angle2 + 360) % 360;



        // Calculate cross product to determine the correct sweep direction
        double crossProduct = v1x * v2y - v1y * v2x;
        double startAngle, sweepAngle;

        // If cross product is positive, goes from angle1 to angle2
        // If negative, go from angle2 to angle1
        if (crossProduct >= 0) {
            startAngle = angle1;
            // Calculate sweep angle in counterclockwise direction
            sweepAngle = (angle2 - angle1 + 360) % 360;
            // If bigger than 180, take the complementary angle
            if (sweepAngle > 180) {
                sweepAngle = 360 - sweepAngle;
                startAngle = angle2;
            }
        } else {
            startAngle = angle2;
            // Calculate sweep angle in counterclockwise direction
            sweepAngle = (angle1 - angle2 + 360) % 360;
            // If bigger than 180, take the complementary angle
            if (sweepAngle > 180) {
                sweepAngle = 360 - sweepAngle;
                startAngle = angle1;
            }
        }

        // Create the arc
        angleArc = new javafx.scene.shape.Arc(
                centerX, centerY, radius, radius,
                startAngle, sweepAngle
        );
        angleArc.setFill(javafx.scene.paint.Color.TRANSPARENT);
        angleArc.setStroke(javafx.scene.paint.Color.ORANGE);
        angleArc.setStrokeWidth(2);
        angleArc.setType(javafx.scene.shape.ArcType.ROUND);

        // Create the text for displaying the angle value
        String formattedAngle = String.format("%.1f°", angleDegrees);

        // Modify this line in createAngleDisplay method
        angleText = new javafx.scene.text.Text(
                centerX + radius * 0.7 * Math.cos(Math.toRadians((angle1 + angle2) / 2)),
                centerY - radius * 0.7 * Math.sin(Math.toRadians((angle1 + angle2) / 2)) - 10, // Add offset of -10
                formattedAngle
        );
        angleText.setFill(javafx.scene.paint.Color.ORANGE);
        angleText.setFont(javafx.scene.text.Font.font("Arial", 12));

        // Add to sandbox
        Sandbox.sandBoxPane.getChildren().addAll(angleArc, angleText);
    }

    // Remove the angle display elements
    private void removeAngleDisplay() {
        if (angleArc != null) {
            Sandbox.sandBoxPane.getChildren().remove(angleArc);
            angleArc = null;
        }
        if (angleText != null) {
            Sandbox.sandBoxPane.getChildren().remove(angleText);
            angleText = null;
        }
    }


}