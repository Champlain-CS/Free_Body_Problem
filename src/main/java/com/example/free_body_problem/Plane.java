package com.example.free_body_problem;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class Plane {
    private Line line;
    private Circle startHandle;
    private Circle endHandle;
    private static final double SNAP_ANGLE_THRESHOLD = 5;
    private boolean isTransformMode = true;
    private double[] dragOffset;

    private Sandbox sandbox;

    // New angle display elements
    private Text angleText;
    private TextField angleInputField;
    private VBox angleBox;

    public Plane(double startX, double startY, double endX, double endY, Color color, Sandbox sandbox) {
        this.sandbox = sandbox;
        line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);
        line.setUserData(this);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        startHandle = createHandle(startX, startY, Color.RED);
        endHandle = createHandle(endX, endY, Color.RED);

        // Create angle display
        createAngleDisplay();

        addLineResizeListener();
        addDragListener();
    }

    public void createAngleDisplay() {
        // Create angle display text
        angleText = new Text();
        angleText.setFont(Font.font(14));

        // Create angle input field
        angleInputField = new TextField();
        angleInputField.setPrefWidth(50);
        angleInputField.setPromptText("Angle");

        // Create horizontal box for input and label
        HBox inputBox = new HBox(5, angleInputField, new Text("°"));
        inputBox.setAlignment(Pos.CENTER);

        // Vertical box to contain both text display and input
        angleBox = new VBox(5, angleText, inputBox);
        angleBox.setAlignment(Pos.CENTER);
        angleBox.setTranslateX(200);
        angleBox.setTranslateY(50);

        // Update angle display initially
        updateAngleDisplay();

        // Add listener to update angle when input is entered
        angleInputField.setOnAction(event -> {
            try {
                double inputAngle = Double.parseDouble(angleInputField.getText());
                setPlaneAngle(inputAngle);
            } catch (NumberFormatException e) {
                // Optional: add error handling or visual feedback
                angleInputField.setText("");
            }
        });

        // Add listeners to update angle display dynamically
        line.startXProperty().addListener((obs, oldVal, newVal) -> updateAngleDisplay());
        line.startYProperty().addListener((obs, oldVal, newVal) -> updateAngleDisplay());
        line.endXProperty().addListener((obs, oldVal, newVal) -> updateAngleDisplay());
        line.endYProperty().addListener((obs, oldVal, newVal) -> updateAngleDisplay());
    }

    public void updateAngleDisplay() {
        double angle = calculatePlaneAngle();
        angleText.setText(String.format("Plane Angle: %.1f°", angle));
    }

    public double calculatePlaneAngle() {
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();

        // Calculate angle in degrees from horizontal
        double angleRad = Math.atan2(dy, dx);
        double angleDeg = Math.toDegrees(angleRad);

        // Normalize angle to be between 0 and 360
        return (angleDeg + 360) % 360;
    }

    public void setPlaneAngle(double targetAngle) {
        // Calculate current line length
        double length = Math.hypot(
                line.getEndX() - line.getStartX(),
                line.getEndY() - line.getStartY()
        );

        // Calculate new end point based on target angle
        double startX = line.getStartX();
        double startY = line.getStartY();
        double endX = startX + length * Math.cos(Math.toRadians(targetAngle));
        double endY = startY + length * Math.sin(Math.toRadians(targetAngle));

        // Update line and handles
        line.setEndX(endX);
        line.setEndY(endY);
        endHandle.setCenterX(endX);
        endHandle.setCenterY(endY);
    }

    public VBox getAngleBox() {
        return angleBox;
    }

    // Existing methods remain the same...

    public Line getLine() {
        return line;
    }

    public Circle getStartHandle() {
        return startHandle;
    }

    public Circle getEndHandle() {
        return endHandle;
    }

    public void setDragOffset(double[] offset) {
        this.dragOffset = offset;
    }

    public double[] getDragOffset() {
        return dragOffset;
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

            // Snap to other planes
            for (Plane otherPlane : sandbox.getPlanes()) {
                if (otherPlane != this) {
                    Snapping.snapPlaneEnds(line, otherPlane.getLine());
                }
            }
            sandbox.updatePlaneList();
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

            // Snap to other planes
            for (Plane otherPlane : sandbox.getPlanes()) {
                if (otherPlane != this) {
                    Snapping.snapPlaneEnds(line, otherPlane.getLine());
                }
            }
            sandbox.updatePlaneList();

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
            this.setDragOffset(new double[]{event.getSceneX(), event.getSceneY()});
        });

        line.setOnMouseDragged(event -> {
            double[] offset = this.getDragOffset();
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

            // Update the drag offset
            this.setDragOffset(new double[]{event.getSceneX(), event.getSceneY()});
        });
    }

    public boolean isTransformMode() {
        return isTransformMode;
    }

    public void setTransformMode(boolean mode) {
        isTransformMode = mode;
        updateHandleColors();
    }

    public void toggleTransformMode() {
        isTransformMode = !isTransformMode;
        updateHandleColors();
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
        double[] snapAngles = {0, 45, 90, 135, 180, 225, 270, 315, 360};
        for (double snapAngle : snapAngles) {
            if (Math.abs(angle - snapAngle) <= SNAP_ANGLE_THRESHOLD) {
                return snapAngle;
            }
        }
        return angle;
    }


}

