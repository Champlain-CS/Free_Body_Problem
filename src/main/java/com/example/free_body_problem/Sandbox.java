package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sandbox extends Application {
    private List<Plane> planes = new ArrayList<>();
    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private boolean dragging = false; // Flag to indicate if a shape is being dragged
    private HBox helpBox;
    private boolean isDisplayingVectors = false;
    private Pane sandBoxPane; // Add this line

    static TextField gravityField;
    public static Pane sandBoxPane;

    public ArrayList<Box> boxList = new ArrayList<Box>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMaximized(true);

        // Root pane
        BorderPane sandBoxRoot = new BorderPane();
        sandBoxRoot.setStyle("-fx-border-color: darkgray");

        // Pane for draggable shapes
        sandBoxPane = new Pane();
        sandBoxPane.setPrefSize(600, 350);
        sandBoxRoot.setCenter(sandBoxPane);

        ImageView infoDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/infoDisplay.png")));
        infoDisplayView.setPreserveRatio(true);
        infoDisplayView.setFitHeight(50);
        infoDisplayView.setPickOnBounds(true);
        sandBoxRoot.setRight(infoDisplayView);

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
            boxList.clear();
            isDisplayingVectors = false;
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

        // Add mouse event handlers for lineButton
        lineButton.setOnMouseClicked(event -> {
            Plane newPlane = new Plane(100, 50, 200, 50, Color.BLACK, this);
            sandBoxPane.getChildren().add(newPlane.getLine());
            newPlane.addLineResizeListener();
            newPlane.addDragListener();
            sandBoxPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
            newPlane.addKeyListener(); // Ensure this line is present
            sandBoxPane.requestFocus(); // Ensure the pane is focused
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
        ImageView vectorDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));
        vectorDisplayView.setPreserveRatio(true);
        vectorDisplayView.setFitHeight(50);
        vectorDisplayView.setPickOnBounds(true);
        vectorDisplayBox.getChildren().addAll(vectorDisplayLabel, vectorDisplayView);

        editorPane.getChildren().addAll(editorLabel, gravityBox, coefficientBox, vectorDisplayBox);
        editorPane.toFront();


        // Set up the stage
        Scene scene = new Scene(sandBoxRoot, 600, 400);
        scene.getStylesheets().add("SandboxStyleSheet.css");
        primaryStage.setTitle("Sandbox");
        primaryStage.setScene(scene);
        primaryStage.show();

        infoDisplayView.setOnMouseClicked(event -> {
            if (helpBox == null) {
                helpBox = createHelpDialogue(); // Create the help box
                sandBoxPane.getChildren().add(helpBox); // Add it to the pane
            } else {
                sandBoxPane.getChildren().remove(helpBox); // Remove it from the pane
                helpBox = null; // Reset the helpBox variable to null
            }
        });


        vectorDisplayView.setOnMouseClicked(event -> {
            if(isDisplayingVectors) {
                isDisplayingVectors = false;
                for(Node node: sandBoxPane.getChildren()) {
                    if(node instanceof VectorDisplay) {
                        sandBoxPane.getChildren().remove(node);
                    }
                }
            }
            for(Box box: boxList) {
                updateVectors(box);
                isDisplayingVectors = true;
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
                        boxList.add(newBox);
                        sandBoxPane.getChildren().add(newBox.getRectangle());
                        newBox.addDragListener();
                        sandBoxPane.getChildren().add(newBox.getResizeHandle());
                        break;
                    case "circle":
                        Pulley newPulley = new Pulley(event.getX(), event.getY(), 25, 10, Color.GRAY, Color.BLACK);
                        sandBoxPane.getChildren().add(newPulley.getCircleGroup());
                        newPulley.addDragListener();
                        break;
                    case "line":
                        double lineLength = 100;
                        createPlane(event.getX() - lineLength / 2, event.getY(), event.getX() + lineLength / 2, event.getY(), Color.BLACK);
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

    public List<Plane> getPlanes() {
        return planes;
    }

    private void createPlane(double startX, double startY, double endX, double endY, Color color) {
        Plane newPlane = new Plane(startX, startY, endX, endY, color, this);
        planes.add(newPlane);
        sandBoxPane.getChildren().add(newPlane.getLine());
        newPlane.addLineResizeListener();
        newPlane.addDragListener();
        sandBoxPane.getChildren().addAll(newPlane.getStartHandle(), newPlane.getEndHandle());
        newPlane.addKeyListener();
    }

    public boolean areAllPlanesInSameMode() {
        if (planes.isEmpty()) return true;
        boolean firstPlaneMode = planes.get(0).isTransformMode();
        for (Plane plane : planes) {
            if (plane.isTransformMode() != firstPlaneMode) {
                return false;
            }
        }
        return true;
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

    public static HBox createHelpDialogue() {
        HBox helpBox = new HBox();
        Rectangle background = new Rectangle();
        background.setWidth(900);
        background.setHeight(400);
        background.setFill(Color.LIGHTBLUE);
        background.setStroke(Color.BLACK);
        background.setTranslateX(50);
        background.setTranslateY(50);

        Label title = new Label("Help Information");
        title.setFont(Font.font("Arial", 25));
        title.setTranslateX(-820);
        title.setTranslateY(70);
        title.setMinWidth(300);

        TextArea textArea = new TextArea();
        textArea.setTranslateX(-1125);
        textArea.setTranslateY(110);
        textArea.getStyleClass().add("help-text-area");
        textArea.setEditable(false);

        InputStream inputStream = Sandbox.class.getClassLoader().getResourceAsStream("helpText.txt");
        if (inputStream != null) {
            try {
                // Read the file content
                Scanner scanner = new Scanner(inputStream);
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                scanner.close();

                // Set the content to the TextArea
                textArea.setText(content.toString());
            } catch (Exception e) {
                textArea.setText("File not found!");
            }
        }

        helpBox.getChildren().addAll(background, title, textArea);
        return helpBox;
    }

    public void updateVectors(Box box) {
        System.out.println("Gravity Vector updated for " + box);
        VectorMath.calculateGravityVector(box);

    }
}