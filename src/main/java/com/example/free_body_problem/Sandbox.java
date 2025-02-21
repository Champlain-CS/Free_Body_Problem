package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Sandbox extends Application {

    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private boolean dragging = false; // Flag to indicate if a shape is being dragged

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);

        // Root pane
        BorderPane sandBoxRoot = new BorderPane();
        sandBoxRoot.setStyle("-fx-border-color: darkgray");

        // Pane for draggable shapes
        Pane sandBoxPane = new Pane();
        sandBoxPane.setPrefSize(600, 350);
        sandBoxRoot.setCenter(sandBoxPane);

        // Bottom bar for shape buttons
        HBox bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");

        sandBoxRoot.setBottom(bottomBar);


        // Back to Menu Button
        Button menuBT = new Button("MENU");
        menuBT.getStyleClass().add("menu-button"); // Apply CSS class
        menuBT.setOnMouseClicked(e -> {
            GUI app = new GUI();
            app.start(new Stage());
            primaryStage.close();
        });

        // Add RESET button
        Button resetBT = new Button("RESET");
        resetBT.getStyleClass().add("menu-button"); // Apply CSS class
        resetBT.setOnMouseClicked(event -> sandBoxPane.getChildren().clear());

        // Create buttons for each shape
        Rectangle rectangleButton = createButtonRectangle(100, 30, Color.WHITE);
        Circle circleButton = createButtonCircle(15, Color.GRAY);
        Line lineButton = createButtonLine(50, 10, Color.BLACK);
        Line ropeButton = createButtonLine(50, 10, Color.BROWN);

        // Add click handlers for each button
        rectangleButton.setOnMouseClicked(event -> {
            Box newBox = new Box(100, 50, 150, 100, Color.TRANSPARENT);
            sandBoxPane.getChildren().add(newBox.getRectangle());
            newBox.addDragListener();
            newBox.addRotateListener();
            sandBoxPane.getChildren().addAll(newBox.getResizeHandle(), newBox.getRotateHandle());
        });

        circleButton.setOnMouseClicked(event -> {
            Pulley newPulley = new Pulley(100, 50, 25, 10, Color.GRAY, Color.BLACK);
            sandBoxPane.getChildren().add(newPulley.getCircleGroup());
            newPulley.addDragListener();
        });

        lineButton.setOnMouseClicked(event -> {
            Plane newPlane = new Plane(100, 50, 200, 50, Color.BLACK);
            sandBoxPane.getChildren().add(newPlane.getLine());
            newPlane.addLineResizeListener();
            newPlane.addDragListener();
            sandBoxPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
        });

        ropeButton.setOnMouseClicked(event -> {
            Rope newRope = new Rope(100, 50, 200, 50, Color.BROWN);
            sandBoxPane.getChildren().add(newRope.getLine());
            newRope.addLineResizeListener();
            newRope.addDragListener();
            sandBoxPane.getChildren().addAll(newRope.getStartHandle(), newRope.getEndHandle());
        });

        // Create a spacer to push the MENU button to the right
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        // Add buttons to the bottom bar
        bottomBar.getChildren().addAll(rectangleButton, circleButton, lineButton, ropeButton, spacer, menuBT, resetBT);

        // Root layout with main pane and bottom bar
//        VBox root = new VBox();
//        VBox.setVgrow(sandBoxPane, Priority.ALWAYS);
//        root.getChildren().addAll(sandBoxPane, bottomBar);

        // Pane for world settings
        VBox editorPane = new VBox();
        editorPane.getStyleClass().add("editor-pane");
        sandBoxRoot.setLeft(editorPane);

        Label editorLabel = new Label("Sandbox Editor");
        editorLabel.getStyleClass().add("editor-label");

        HBox gravityBox = new HBox();
        gravityBox.getStyleClass().add("editor-attribute-box");
        Label gravityLabel = new Label("Gravity:");
        gravityLabel.getStyleClass().add("editor-attribute-label");
        TextField gravityField = new TextField();
        gravityField.getStyleClass().add("editor-attribute-field");
        gravityField.setText("9.8");
        gravityBox.getChildren().addAll(gravityLabel, gravityField);

        HBox coefficientBox = new HBox();
        coefficientBox.getStyleClass().add("editor-attribute-box");
        Label coefficientLabel = new Label("Coefficient of Friction:");
        coefficientLabel.getStyleClass().add("editor-attribute-label");
        TextField coefficientField = new TextField();
        coefficientField.getStyleClass().add("editor-attribute-field");
        coefficientField.setText("0.4");
        coefficientBox.getChildren().addAll(coefficientLabel, coefficientField);

        editorPane.getChildren().addAll(editorLabel, gravityBox, coefficientBox);



        // Set up the stage
        Scene scene = new Scene(sandBoxRoot, 600, 400);
        scene.getStylesheets().add("SandboxStyleSheet.css");
        primaryStage.setTitle("Sandbox");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to enable dragging and removal for shapes
    private void enableDraggingAndRemoval(Node shape) {
        shape.setOnMousePressed(event -> {
            dragging = true;
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        shape.setOnMouseDragged(event -> {
            double[] offset = (double[]) shape.getUserData();
            if (shape instanceof Group) {
                // Dragging logic for Group
            } else if (shape instanceof Rectangle) {
                // Dragging logic for Rectangle
            }
            shape.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        shape.setOnMouseReleased(event -> dragging = false);
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
}