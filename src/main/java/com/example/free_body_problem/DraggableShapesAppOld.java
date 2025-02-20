package com.example.free_body_problem;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DraggableShapesAppOld extends Application {

    private static final double GRAVITY = 9.8; // Gravity acceleration
    private static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private boolean dragging = false; // Flag to indicate if a shape is being dragged
    private AnimationTimer gravityTimer;

    @Override
    public void start(Stage primaryStage) {
        // Pane for draggable shapes
        Pane mainPane = new Pane();
        mainPane.setPrefSize(600, 350);

        // Create a handle for resizing the Pane
        Circle paneHandle = createHandle(mainPane.getPrefWidth(), mainPane.getPrefHeight());
        addPaneResizeListener(mainPane, paneHandle);

        // Bottom bar for shape buttons
        HBox bottomBar = new HBox();
        bottomBar.setPrefHeight(50);
        bottomBar.setStyle("-fx-background-color: lightgray;");
        bottomBar.setSpacing(10);

        // Create buttons for each shape
        Rectangle rectangleButton = createButtonRectangle(100, 30, Color.WHITE);
        Circle circleButton = createButtonCircle(15, Color.GRAY);
        Line lineButton = createButtonLine(50, 5, Color.BLACK);

        // Add click handlers for each button
        rectangleButton.setOnMouseClicked(event -> {
            Rectangle newRectangle = createDraggableRectangle(100, 50, 150, 100, Color.TRANSPARENT);
            mainPane.getChildren().add(newRectangle);
            Circle resizeHandle = createHandle(newRectangle.getX() + newRectangle.getWidth(), newRectangle.getY() + newRectangle.getHeight());
            Circle rotateHandle = createHandle(newRectangle.getX() + newRectangle.getWidth(), newRectangle.getY() + newRectangle.getHeight() / 2);
            addDragListener(newRectangle, resizeHandle);
            addRotateListener(newRectangle, rotateHandle);
            mainPane.getChildren().addAll(resizeHandle, rotateHandle);
        });

        circleButton.setOnMouseClicked(event -> {
            Group newCircleGroup = createDraggableCircleGroup(100, 50, 25, 10, Color.GRAY, Color.BLACK);
            mainPane.getChildren().add(newCircleGroup);
        });

        lineButton.setOnMouseClicked(event -> {
            Line newLine = createDraggableLine(100, 50, 200, 50, Color.BLACK);
            mainPane.getChildren().add(newLine);
            Circle startHandle = createHandle(newLine.getStartX(), newLine.getStartY());
            Circle endHandle = createHandle(newLine.getEndX(), newLine.getEndY());
            addLineResizeListener(newLine, startHandle, endHandle);
            mainPane.getChildren().addAll(startHandle, endHandle);
        });

        // Add buttons to the bottom bar
        bottomBar.getChildren().addAll(rectangleButton, circleButton, lineButton);

        // Root layout with main pane and bottom bar
        VBox root = new VBox(mainPane, bottomBar);
        mainPane.getChildren().add(paneHandle);

        // Animation timer to simulate gravity and collisions
        gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!dragging) {
                    for (Node shape : mainPane.getChildren()) {
                        if (shape instanceof Group) {
                            // Handle gravity for circle groups
                        } else if (shape instanceof Rectangle) {
                            // Handle gravity for rectangles
                        }
                    }
                }
            }
        };
        //gravityTimer.start();

        // Enable dragging and removal for shapes

        // Set up the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Draggable Shapes with Creation and Removal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create a draggable rectangle
    private Rectangle createDraggableRectangle(double x, double y, double width, double height, Color color) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        rectangle.setStroke(Color.BLACK);

        enableDraggingAndRemoval(rectangle);

        return rectangle;
    }

    // Method to create a draggable circle group
    private Group createDraggableCircleGroup(double x, double y, double outerRadius, double innerRadius, Color outerColor, Color innerColor) {
        Circle outerCircle = new Circle(x, y, outerRadius, outerColor);
        outerCircle.setStroke(Color.BLACK);

        Circle innerCircle = new Circle(x, y, innerRadius, innerColor);
        innerCircle.setStroke(Color.BLACK);

        Group circleGroup = new Group(outerCircle, innerCircle);

        enableDraggingAndRemoval(circleGroup);

        return circleGroup;
    }

    // Method to create a draggable line
    private Line createDraggableLine(double startX, double startY, double endX, double endY, Color color) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);

        enableDraggingAndRemoval(line);

        return line;
    }

    // Helper method to enable dragging and removal for shapes
    private void enableDraggingAndRemoval(javafx.scene.Node shape) {
        shape.setOnMousePressed(event -> {
            dragging = true;
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        shape.setOnMouseDragged(event -> {
            double[] offset = (double[]) shape.getUserData();
            if (shape instanceof Group) {
                Group group = (Group) shape;
                for (Node node : group.getChildren()) {
                    if (node instanceof Circle) {
                        Circle circle = (Circle) node;
                        circle.setCenterX(circle.getCenterX() + (event.getSceneX() - offset[0]));
                        circle.setCenterY(circle.getCenterY() + (event.getSceneY() - offset[1]));
                    }
                }
            } else if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                rect.setX(rect.getX() + (event.getSceneX() - offset[0]));
                rect.setY(rect.getY() + (event.getSceneY() - offset[1]));
            }
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        shape.setOnMouseReleased(event -> dragging = false);

        shape.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right-click
                Pane parent = (Pane) shape.getParent();
                parent.getChildren().remove(shape);
            }
        });
    }

    // Helper method to create a rectangle button
    private Rectangle createButtonRectangle(double width, double height, Color color) {
        Rectangle rect = new Rectangle(width, height, color);
        rect.setStroke(Color.BLACK);
        return rect;
    }

    // Helper method to create a circle button
    private Circle createButtonCircle(double radius, Color color) {
        Circle circle = new Circle(radius, color);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    // Helper method to create a line button
    private Line createButtonLine(double length, double strokeWidth, Color color) {
        Line line = new Line(0, 0, length, 0);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);
        return line;
    }

    // Method to create a handle for resizing
    private Circle createHandle(double x, double y) {
        Circle handle = new Circle(x, y, HANDLE_RADIUS);
        handle.setFill(Color.RED);
        return handle;
    }

    // Method to add drag listener for resizing
    private void addDragListener(Rectangle rectangle, Circle handle) {
        handle.setOnMouseDragged(event -> {
            double offsetX = event.getX() - handle.getCenterX();
            double offsetY = event.getY() - handle.getCenterY();

            rectangle.setWidth(rectangle.getWidth() + offsetX);
            rectangle.setHeight(rectangle.getHeight() + offsetY);

            handle.setCenterX(event.getX());
            handle.setCenterY(event.getY());
        });

        rectangle.xProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterX(newVal.doubleValue() + rectangle.getWidth());
        });

        rectangle.yProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterY(newVal.doubleValue() + rectangle.getHeight());
        });

        rectangle.widthProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterX(rectangle.getX() + newVal.doubleValue());
        });

        rectangle.heightProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterY(rectangle.getY() + newVal.doubleValue());
        });
    }

    // Method to add resize listener to the Pane
    private void addPaneResizeListener(Pane pane, Circle handle) {
        handle.setOnMouseDragged(event -> {
            double offsetX = event.getX() - handle.getCenterX();
            double offsetY = event.getY() - handle.getCenterY();

            pane.setPrefWidth(pane.getPrefWidth() + offsetX);
            pane.setPrefHeight(pane.getPrefHeight() + offsetY);

            handle.setCenterX(event.getX());
            handle.setCenterY(event.getY());
        });
    }

    // Method to add resize listener to the Line
    private void addLineResizeListener(Line line, Circle startHandle, Circle endHandle) {
        startHandle.setOnMouseDragged(event -> {
            line.setStartX(event.getX());
            line.setStartY(event.getY());
            startHandle.setCenterX(event.getX());
            startHandle.setCenterY(event.getY());
        });

        endHandle.setOnMouseDragged(event -> {
            line.setEndX(event.getX());
            line.setEndY(event.getY());
            endHandle.setCenterX(event.getX());
            endHandle.setCenterY(event.getY());
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

    // Method to add rotate listener to the rectangle
    private void addRotateListener(Rectangle rectangle, Circle handle) {
        handle.setFill(Color.BLUE);
        handle.setOnMouseDragged(event -> {
            double centerX = rectangle.getX() + rectangle.getWidth() / 2;
            double centerY = rectangle.getY() + rectangle.getHeight() / 2;
            double angle = Math.toDegrees(Math.atan2(event.getY() - centerY, event.getX() - centerX));
            rectangle.setRotate(angle);
            handle.setCenterX(event.getX());
            handle.setCenterY(event.getY());
        });

        rectangle.xProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterX(newVal.doubleValue() + rectangle.getWidth());
        });

        rectangle.yProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterY(newVal.doubleValue() + rectangle.getHeight() / 2);
        });

        rectangle.widthProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterX(rectangle.getX() + newVal.doubleValue());
        });

        rectangle.heightProperty().addListener((obs, oldVal, newVal) -> {
            handle.setCenterY(rectangle.getY() + rectangle.getHeight() / 2);
        });

        rectangle.rotateProperty().addListener((obs, oldVal, newVal) -> {
            double centerX = rectangle.getX() + rectangle.getWidth() / 2;
            double centerY = rectangle.getY() + rectangle.getHeight() / 2;
            double angle = Math.toRadians(newVal.doubleValue());
            double handleX = centerX + (rectangle.getWidth() / 2) * Math.cos(angle);
            double handleY = centerY + (rectangle.getWidth() / 2) * Math.sin(angle);
            handle.setCenterX(handleX);
            handle.setCenterY(handleY);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}