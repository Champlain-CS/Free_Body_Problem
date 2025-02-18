package com.example.free_body_problem;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
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
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class DraggableShapesApp extends Application {

    private static final double GRAVITY = 9.8; // Gravity acceleration


    @Override
    public void start(Stage primaryStage) {
        // Pane for draggable shapes
        Pane mainPane = new Pane();
        mainPane.setPrefSize(600, 350);

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
            Rectangle newRectangle = createDraggableRectangle(100, 50, 150, 100, Color.WHITE);
            mainPane.getChildren().add(newRectangle);
        });

        circleButton.setOnMouseClicked(event -> {
            Circle newCircle = createDraggableCircle(100, 50, 25, Color.GRAY);
            mainPane.getChildren().add(newCircle);
        });

        lineButton.setOnMouseClicked(event -> {
            Line newLine = createDraggableLine(100, 50, 200, 50, Color.BLACK);
            mainPane.getChildren().add(newLine);
        });

        // Add buttons to the bottom bar
        bottomBar.getChildren().addAll(rectangleButton, circleButton, lineButton);

        // Root layout with main pane and bottom bar
        VBox root = new VBox(mainPane, bottomBar);

        // Animation timer to simulate gravity and collisions
        AnimationTimer gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Node shape : mainPane.getChildren()) {
                    if (shape.getClass().equals(Rectangle.class)) {
                        Rectangle rectangle = (Rectangle) shape;
                        rectangle.setY(rectangle.getY() + 3); // Move rectangle down
                    } else if (shape.getClass().equals(Circle.class)) {
                        Circle circle = (Circle) shape;
                        circle.setCenterY(circle.getCenterY() + 3); // Move circle down
                    }
                }
            }
        };
        gravityTimer.start();

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

    // Method to create a draggable circle
    private Circle createDraggableCircle(double x, double y, double radius, Color color) {
        Circle circle = new Circle(x, y, radius, color);
        circle.setStroke(Color.BLACK);

        enableDraggingAndRemoval(circle);

        return circle;
    }

    // Method to create a draggable line
    private Line createDraggableLine(double startX, double startY, double endX, double endY, Color color) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(8);

        // Enable dragging for a line
        line.setOnMousePressed(event -> {
            line.setUserData(new double[]{
                    event.getSceneX() - line.getStartX(),
                    event.getSceneY() - line.getStartY(),
                    event.getSceneX() - line.getEndX(),
                    event.getSceneY() - line.getEndY()
            });
        });

        line.setOnMouseDragged(event -> {
            double[] offset = (double[]) line.getUserData();
            line.setStartX(event.getSceneX() - offset[0]);
            line.setStartY(event.getSceneY() - offset[1]);
            line.setEndX(event.getSceneX() - offset[2]);
            line.setEndY(event.getSceneY() - offset[3]);
        });

        // Enable right-click removal
        line.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right-click
                Pane parent = (Pane) line.getParent();
                parent.getChildren().remove(line);
            }
        });

        return line;
    }

    // Helper method to enable dragging and removal for shapes
    private void enableDraggingAndRemoval(javafx.scene.Node shape) {
        shape.setOnMousePressed(event -> {
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        shape.setOnMouseDragged(event -> {
            double[] offset = (double[]) shape.getUserData();
            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                rect.setX(rect.getX() + (event.getSceneX() - offset[0]));
                rect.setY(rect.getY() + (event.getSceneY() - offset[1]));
            } else if (shape instanceof Circle) {
                Circle circle = (Circle) shape;
                circle.setCenterX(circle.getCenterX() + (event.getSceneX() - offset[0]));
                circle.setCenterY(circle.getCenterY() + (event.getSceneY() - offset[1]));
            }
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

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



    public static void main(String[] args) {
        launch(args);
    }
}