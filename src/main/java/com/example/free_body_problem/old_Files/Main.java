package com.example.free_body_problem.old_Files;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final double GRAVITY = 9.8; // Gravity acceleration
    private static final double GROUND_LEVEL = 500; // Bottom boundary of the scene
    private static final double SQUARE_SIZE = 50; // Size of each square
    private List<Rectangle> squares = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Create a Pane for free placement of squares
        Pane pane = new Pane();

        // Add a button to spawn squares
        Button addSquareButton = new Button("Add Square");
        addSquareButton.setLayoutX(10);
        addSquareButton.setLayoutY(10);

        // Add button click action
        addSquareButton.setOnAction(e -> {
            // Create a square
            Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.BLUE);
            square.setStroke(Color.BLACK);
            square.setLayoutX(50 + Math.random() * 400); // Random X position
            square.setLayoutY(0); // Start at the top of the pane

            // Add the square to the pane and list
            pane.getChildren().add(square);
            squares.add(square);
        });

        // Add the button to the pane
        pane.getChildren().add(addSquareButton);

        // Create a scene
        Scene scene = new Scene(pane, 500, 500);

        // Animation timer to simulate gravity and collisions
        AnimationTimer gravityTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Rectangle square : squares) {
                    // If the square is already at the ground or stacked, skip it
                    if (isSquareAtRest(square)) {
                        continue;
                    }

                    // Update position
                    double newY = square.getLayoutY() + GRAVITY;

                    // Check for collisions with other squares
                    for (Rectangle other : squares) {
                        if (square != other && isColliding(square, other)) {
                            newY = other.getLayoutY() - SQUARE_SIZE; // Stack on top
                            break;
                        }
                    }

                    // Check for ground collision
                    if (newY + SQUARE_SIZE >= GROUND_LEVEL) {
                        newY = GROUND_LEVEL - SQUARE_SIZE; // Stop at the ground
                    }

                    // Apply new position
                    square.setLayoutY(newY);
                }
            }
        };

        gravityTimer.start(); // Start the gravity simulation

        // Set up the stage
        primaryStage.setTitle("Gravity with Collisions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Check if a square is at rest (on the ground or stacked on another square)
    private boolean isSquareAtRest(Rectangle square) {
        if (square.getLayoutY() + SQUARE_SIZE >= GROUND_LEVEL) {
            return true; // Square is on the ground
        }

        for (Rectangle other : squares) {
            if (square != other && isColliding(square, other)) {
                return true; // Square is stacked
            }
        }

        return false;
    }

    // Check if two squares are colliding
    private boolean isColliding(Rectangle square, Rectangle other) {
        return square.getLayoutX() < other.getLayoutX() + SQUARE_SIZE &&
                square.getLayoutX() + SQUARE_SIZE > other.getLayoutX() &&
                square.getLayoutY() + SQUARE_SIZE >= other.getLayoutY() &&
                square.getLayoutY() < other.getLayoutY() + SQUARE_SIZE;
    }

    public static void main(String[] args) {
        launch(args);
    }
}