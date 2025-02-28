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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class Sandbox extends Application {

    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private boolean dragging = false; // Flag to indicate if a shape is being dragged
    private Rectangle helpBox; // Reference to the large rectangle

    TextField gravityField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMaximized(true);

        // Root pane
        BorderPane sandBoxRoot = new BorderPane();
        sandBoxRoot.setStyle("-fx-border-color: darkgray");

        // Pane for draggable shapes
        Pane sandBoxPane = new Pane();
        sandBoxPane.setPrefSize(600, 350);
        sandBoxRoot.setCenter(sandBoxPane);

        // Right bar for images
        VBox rightBar = new VBox();
        rightBar.setAlignment(Pos.TOP_RIGHT);
        rightBar.setPadding(new Insets(20));
        rightBar.setSpacing(40);

        // Add images to the right bar
        ImageView infoDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/infoDisplay.png")));
        ImageView vectorDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));
        infoDisplayView.setFitWidth(30);
        infoDisplayView.setPreserveRatio(true);
        infoDisplayView.setScaleX(2);
        infoDisplayView.setScaleY(2);
        infoDisplayView.setPickOnBounds(true);

        vectorDisplayView.setFitWidth(30);
        vectorDisplayView.setPreserveRatio(true);
        vectorDisplayView.setScaleX(2);
        vectorDisplayView.setScaleY(2);
        vectorDisplayView.setPickOnBounds(true);
        //rightBar.getChildren().addAll(infoDisplayView, vectorDisplayView);

        sandBoxRoot.setRight(rightBar);

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
        resetBT.setOnMouseClicked(event -> {
            sandBoxPane.getChildren().clear();
            gravityField.setText("9.8");
        });

        // Create buttons for each shape
        Rectangle rectangleButton = createButtonRectangle(50, 50, Color.WHITE);
        Circle circleButton = createButtonCircle(15, Color.BLACK);
        circleButton.setStroke(Color.GRAY);
        circleButton.setStrokeWidth(10); // Adjust the stroke width to create the inner radius effect
        Line lineButton = createButtonLine(50, 10, Color.BLACK);
        Line ropeButton = createButtonLine(50, 10, Color.BROWN);

        // Add drag-and-drop handlers for each button
        addDragHandlers(rectangleButton, sandBoxPane, "rectangle");
        addDragHandlers(circleButton, sandBoxPane, "circle");
        addDragHandlers(lineButton, sandBoxPane, "line");
        addDragHandlers(ropeButton, sandBoxPane, "rope");

        // Add mouse event handlers for rectangleButton
        rectangleButton.setOnMouseEntered(e -> {
            rectangleButton.setScaleX(1.2);
            rectangleButton.setScaleY(1.2);
        });
        rectangleButton.setOnMouseExited(e -> {
            rectangleButton.setScaleX(1.0);
            rectangleButton.setScaleY(1.0);
        });

        // Add mouse event handlers for circleButton
        circleButton.setOnMouseClicked(event -> {
            Pulley newPulley = new Pulley(100, 50, 25, 10, Color.GRAY, Color.BLACK);
            sandBoxPane.getChildren().add(newPulley.getCircleGroup());
            newPulley.addDragListener();
        });
        circleButton.setOnMouseEntered(e -> {
            circleButton.setScaleX(1.2);
            circleButton.setScaleY(1.2);
        });
        circleButton.setOnMouseExited(e -> {
            circleButton.setScaleX(1.0);
            circleButton.setScaleY(1.0);
        });

        // Add mouse event rs for lineButton
        lineButton.setOnMouseClicked(event -> {
            Plane newPlane = new Plane(100, 50, 200, 50, Color.BLACK);
            sandBoxPane.getChildren().add(newPlane.getLine());
            newPlane.addLineResizeListener();
            newPlane.addDragListener();
            sandBoxPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
        });
        lineButton.setOnMouseEntered(e -> {
            lineButton.setScaleX(1.2);
            lineButton.setScaleY(1.2);
        });
        lineButton.setOnMouseExited(e -> {
            lineButton.setScaleX(1.0);
            lineButton.setScaleY(1.0);
        });

        // Add mouse event handlers for ropeButton
        ropeButton.setOnMouseClicked(event -> {
            Rope newRope = new Rope(100, 50, 200, 50, Color.BROWN);
            sandBoxPane.getChildren().add(newRope.getLine());
            newRope.addLineResizeListener();
            newRope.addDragListener();
            sandBoxPane.getChildren().addAll(newRope.getStartHandle(), newRope.getEndHandle());
        });
        ropeButton.setOnMouseEntered(e -> {
            ropeButton.setScaleX(1.2);
            ropeButton.setScaleY(1.2);
        });
        ropeButton.setOnMouseExited(e -> {
            ropeButton.setScaleX(1.0);
            ropeButton.setScaleY(1.0);
        });

        // Create a spacer to push the MENU button to the right
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Grouping the shape buttons
        HBox shapeButtons = new HBox(20, rectangleButton, circleButton, lineButton, ropeButton);
        shapeButtons.setAlignment(Pos.CENTER);

        // Create spacers for centering
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        leftSpacer.setMinWidth(175);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Add elements to the bottom bar
        bottomBar.getChildren().addAll(leftSpacer, shapeButtons, rightSpacer, menuBT, resetBT);


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
        gravityField = new TextField();
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

        HBox vectorDisplayBox = new HBox();
        vectorDisplayBox.getStyleClass().add("larger-editor-attribute-box");
        Label vectorDisplayLabel = new Label("Display Vectors: ");
        vectorDisplayLabel.getStyleClass().add("editor-attribute-label");
        vectorDisplayBox.getChildren().addAll(vectorDisplayLabel, vectorDisplayView);

        editorPane.getChildren().addAll(editorLabel, gravityBox, coefficientBox, vectorDisplayBox);
        editorPane.toFront();

        // Set up the stage
        Scene scene = new Scene(sandBoxRoot, 600, 400);
        scene.getStylesheets().add("SandboxStyleSheet.css");
        primaryStage.setTitle("Sandbox");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add event handler to imageView1 to create a large rectangle
        infoDisplayView.setOnMouseClicked(event -> {
            if (helpBox == null) {
                helpBox = new Rectangle(900, 500, Color.LIGHTBLUE);
                helpBox.setStroke(Color.BLACK);
                helpBox.setX((sandBoxPane.getWidth() - helpBox.getWidth()) / 2);
                helpBox.setY((sandBoxPane.getHeight() - helpBox.getHeight()) / 2);
                sandBoxPane.getChildren().add(helpBox);

                // Create and add text inside the helpBox
                Text helpText = new Text("Help Information");
                helpText.setFill(Color.BLACK);
                helpText.setStyle("-fx-font-size: 24px;");
                helpText.setX(helpBox.getX() + 20);
                helpText.setY(helpBox.getY() + 40);
                sandBoxPane.getChildren().add(helpText);

                // Add event handler to remove the large rectangle and text when Escape is pressed
                scene.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ESCAPE && helpBox != null) {
                        sandBoxPane.getChildren().remove(helpBox);
                        sandBoxPane.getChildren().remove(helpText);
                        helpBox = null;
                    }
                });
            }
        });

        // Add event handler to remove the large rectangle when Escape is pressed
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && helpBox != null) {
                sandBoxPane.getChildren().remove(helpBox);
                helpBox = null;
            }
        });
    }

    private void addDragHandlers(Node button, Pane sandBoxPane, String shapeType) {
        button.setOnDragDetected(event -> {
            Dragboard db = button.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(shapeType);
            db.setContent(content);

            // Create a snapshot of the node and set it as the drag view
            if (button instanceof Rectangle) {
                Rectangle rect = (Rectangle) button;
                db.setDragView(rect.snapshot(null, null));
            } else if (button instanceof Circle) {
                Circle circle = (Circle) button;
                db.setDragView(circle.snapshot(null, null));
            } else if (button instanceof Line) {
                Line line = (Line) button;
                db.setDragView(line.snapshot(null, null));
            }

            event.consume();
        });

        sandBoxPane.setOnDragOver(event -> {
            if (event.getGestureSource() != sandBoxPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        sandBoxPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String shape = db.getString();
                switch (shape) {
                    case "rectangle":
                        double rectWidth = 150;
                        double rectHeight = 100;
                        Box newBox = new Box(event.getX() - rectWidth / 2, event.getY() - rectHeight / 2, rectWidth, rectHeight, Color.WHITE, sandBoxPane);
                        sandBoxPane.getChildren().add(newBox.getRectangle());
                        newBox.addDragListener();
                        sandBoxPane.getChildren().addAll(newBox.getResizeHandle());
                        break;
                    case "circle":
                        Pulley newPulley = new Pulley(event.getX(), event.getY(), 25, 10, Color.GRAY, Color.BLACK);
                        sandBoxPane.getChildren().add(newPulley.getCircleGroup());
                        newPulley.addDragListener();
                        break;
                    case "line":
                        double lineLength = 100;
                        Plane newPlane = new Plane(event.getX() - lineLength / 2, event.getY(), event.getX() + lineLength / 2, event.getY(), Color.BLACK);
                        sandBoxPane.getChildren().add(newPlane.getLine());
                        newPlane.addLineResizeListener();
                        newPlane.addDragListener();
                        sandBoxPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
                        break;
                    case "rope":
                        double ropeLength = 100;
                        Rope newRope = new Rope(event.getX() - ropeLength / 2, event.getY(), event.getX() + ropeLength / 2, event.getY(), Color.BROWN);
                        sandBoxPane.getChildren().add(newRope.getLine());
                        newRope.addLineResizeListener();
                        newRope.addDragListener();
                        sandBoxPane.getChildren().addAll(newRope.getStartHandle(), newRope.getEndHandle());
                        break;
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
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

    public static void main(String[] args) {
        launch(args);
    }
}

/* Justin To do
- Bring back ? image
- Image attributes to CSS
- Images grow on hover
- ? image click off
- vectorDisplay change color on enabled/disabled
 */