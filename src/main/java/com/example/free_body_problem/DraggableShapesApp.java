package com.example.free_body_problem;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DraggableShapesApp extends Application {

    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
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

// Back to Menu Button
        Button menuBT = new Button("MENU");
        menuBT.getStyleClass().add("menu-button"); // Apply CSS class
        menuBT.setOnMouseClicked(e -> {
            GUI app = new GUI();
            app.start(new Stage());
            primaryStage.close();
        });

// Load CSS into the Scene





        // Create buttons for each shape
        Rectangle rectangleButton = createButtonRectangle(100, 30, Color.WHITE);
        Circle circleButton = createButtonCircle(15, Color.GRAY);
        Line lineButton = createButtonLine(50, 5, Color.BLACK);

        // Add click handlers for each button
        rectangleButton.setOnMouseClicked(event -> {
            Box newBox = new Box(100, 50, 150, 100, Color.TRANSPARENT);
            mainPane.getChildren().add(newBox.getRectangle());
            newBox.addDragListener();
            newBox.addRotateListener();
            mainPane.getChildren().addAll(newBox.getResizeHandle(), newBox.getRotateHandle());
        });

        circleButton.setOnMouseClicked(event -> {
            Pulley newPulley = new Pulley(100, 50, 25, 10, Color.GRAY, Color.BLACK);
            mainPane.getChildren().add(newPulley.getCircleGroup());
        });

        lineButton.setOnMouseClicked(event -> {
            Plane newPlane = new Plane(100, 50, 200, 50, Color.BLACK);
            mainPane.getChildren().add(newPlane.getLine());
            newPlane.addLineResizeListener();
            mainPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
        });

        // Create a spacer to push the MENU button to the right
        //!!!!!!!!!This is a space so the menu button can be pushed to the right, its just an empty plane
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        // Add buttons to the bottom bar
        bottomBar.getChildren().addAll(rectangleButton, circleButton, lineButton, spacer, menuBT);

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
                            // Handle gravity and collisions for groups
                        }
                    }
                }
            }
        };
        //gravityTimer.start();

        // Enable dragging and removal for shapes

        // Set up the stage
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("MainMenuStyleSheet.css");
        primaryStage.setTitle("Draggable Shapes with Creation and Removal");
        primaryStage.setScene(scene);
        primaryStage.show();


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

    public static void main(String[] args) {
        launch(args);
    }
}