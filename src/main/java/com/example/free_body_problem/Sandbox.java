package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.*;

public class Sandbox extends Application {
    private List<Plane> planes = new ArrayList<>();
    public ArrayList<PhysicsObject> physicsObjectList = new ArrayList<PhysicsObject>();
    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private boolean dragging = false; // Flag to indicate if a shape is being dragged
    private HBox helpBox;
    private boolean isDisplayingVectors = false;

    static TextField gravityField;
    static TextField coefficientField;
    public static Pane sandBoxPane;
    private VBox planeListBox; // New field to store the list of plane angles

    // Instantiate SoundPlayer
    private SoundPlayer soundPlayer = new SoundPlayer();

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
        sandBoxRoot.setStyle("-fx-background-color: white");

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
        // Instantiate SoundPlayer for menu button
        SoundPlayer menuSoundPlayer = new SoundPlayer();

        menuBT.setOnMouseClicked(e -> {
            // Play menu button sound
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu.wav");

            GUI app = new GUI();
            app.start(new Stage());
            primaryStage.close();
        });

        // Add RESET button
        Button resetBT = new Button("RESET");
        resetBT.getStyleClass().add("menu-button"); // Apply CSS class

        // Create buttons for each shape
        Rectangle rectangleButton = createButtonRectangle(50, 50, Color.WHITE);
        Circle circleButton = createButtonCircle(15, Color.BLACK);
        circleButton.setStroke(Color.GRAY);
        circleButton.setStrokeWidth(10); // Adjust the stroke width to create the inner radius effect
        Line lineButton = createButtonLine(50, 10, Color.BLACK);
        Line ropeButton = createButtonLine(50, 10, Color.BROWN);
        LinearGradient ropeGradient = new LinearGradient(0, 0, 1, 1, true,
                javafx.scene.paint.CycleMethod.REFLECT,
                new Stop(0, Color.web("#A0522D")),
                new Stop(0.5, Color.web("#D2691E")),
                new Stop(1, Color.web("#8B4513")));
        ropeButton.setStroke(ropeGradient);
        ropeButton.setStrokeLineCap(StrokeLineCap.ROUND);

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
            createPlane(100, 50, 200, 50, Color.BLACK);
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
            double ropeLength = 100;
            Rope newRope = new Rope(100, 50, 200, 50, Color.BROWN, false, false, physicsObjectList);
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

        // labels for the shapes
        Label boxLbl = new Label("Box");
        boxLbl.getStyleClass().add("shape-label");
        Label pulleyLbl = new Label("Pulley");
        pulleyLbl.getStyleClass().add("shape-label");
        Label planeLbl = new Label("Plane");
        planeLbl.getStyleClass().add("shape-label");
        Label ropeLbl = new Label("Rope");
        ropeLbl.getStyleClass().add("shape-label");

        // Grouping the shape buttons
        VBox boxBTBox = new VBox(boxLbl, rectangleButton);
        boxBTBox.setSpacing(10);
        boxBTBox.setAlignment(Pos.CENTER);

        VBox pulleyBTBox = new VBox(pulleyLbl, circleButton);
        pulleyBTBox.setSpacing(15);
        pulleyBTBox.setAlignment(Pos.CENTER);
        pulleyBTBox.setTranslateY(-3);

        VBox planeBTBox = new VBox(planeLbl, lineButton);
        planeBTBox.setSpacing(30);
        planeBTBox.setAlignment(Pos.CENTER);
        planeBTBox.setTranslateY(-10);

        VBox ropeBTBox = new VBox(ropeLbl, ropeButton);
        ropeBTBox.setSpacing(30);
        ropeBTBox.setAlignment(Pos.CENTER);
        ropeBTBox.setTranslateY(-10);

        HBox shapeButtons = new HBox(30, boxBTBox, pulleyBTBox, planeBTBox, ropeBTBox);
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

        // Create a scroll pane for plane list
        ScrollPane planeScrollPane = new ScrollPane();
        planeScrollPane.setFitToWidth(true);
        planeScrollPane.setPrefHeight(200);
        planeScrollPane.getStyleClass().add("plane-scroll-pane");

        // Create a VBox to hold the list of planes
        planeListBox = new VBox(10);
        planeListBox.setPadding(new Insets(10));
        planeListBox.getStyleClass().add("plane-list-box");

        planeScrollPane.setContent(planeListBox);

        HBox gravityBox = new HBox();
        gravityBox.getStyleClass().add("editor-attribute-box");
        Label gravityLabel = new Label("Gravity:");
        gravityLabel.getStyleClass().add("editor-attribute-label");
        gravityField = new TextField();
        gravityField.getStyleClass().add("editor-attribute-field");
        gravityField.setText("9.8");

        HBox gravityWithUnits = new HBox();
        gravityWithUnits.setSpacing(5);
        Label gravityUnits = new Label("m/s²");
        gravityUnits.setTranslateY(3);
        gravityUnits.getStyleClass().add("editor-attribute-label");
        gravityWithUnits.getChildren().addAll(gravityField, gravityUnits);
        gravityBox.getChildren().addAll(gravityLabel, gravityWithUnits);

        HBox coefficientBox = new HBox();
        coefficientBox.getStyleClass().add("editor-attribute-box");
        Label coefficientLabel = new Label("Coefficient of Friction:");
        coefficientLabel.getStyleClass().add("editor-attribute-label");
        coefficientField = new TextField();
        coefficientField.getStyleClass().add("editor-attribute-field");
        coefficientField.setText("0.4");
        coefficientBox.getChildren().addAll(coefficientLabel, coefficientField);

        gravityField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sandBoxPane.requestFocus(); // Unfocus the TextField by requesting focus on the parent container
            }
        });

        coefficientField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sandBoxPane.requestFocus(); // Unfocus the TextField by requesting focus on the parent container
            }
        });

        HBox vectorDisplayBox = new HBox();
        vectorDisplayBox.getStyleClass().add("larger-editor-attribute-box");
        Label vectorDisplayLabel = new Label("Display Vectors: ");
        vectorDisplayLabel.getStyleClass().add("editor-attribute-label");
        ImageView vectorDisplayView = new ImageView(
                new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));
        vectorDisplayView.setPreserveRatio(true);
        vectorDisplayView.setFitHeight(50);
        vectorDisplayView.setPickOnBounds(true);
        vectorDisplayBox.getChildren().addAll(vectorDisplayLabel, vectorDisplayView);

        // Add elements to the editor pane
        editorPane.getChildren().addAll(
                editorLabel,
                planeScrollPane,  // Add the scroll pane
                gravityBox,
                coefficientBox,
                vectorDisplayBox
        );

        // Instantiate SoundPlayer for reset button
        SoundPlayer resetSoundPlayer = new SoundPlayer();

        resetBT.setOnMouseClicked(event -> {
            sandBoxPane.getChildren().clear();
            gravityField.setText("9.8");
            physicsObjectList.clear();
            planes.clear();
            planeListBox.getChildren().clear();  // Clear the plane list
            isDisplayingVectors = false;

            // Removing the lock
            Iterator<Node> rootIterator = sandBoxRoot.getChildren().iterator();
            while (rootIterator.hasNext()) {
                Node node = rootIterator.next();
                if (node instanceof LockPane) {
                    rootIterator.remove();
                }
            }
            sandBoxRoot.setStyle("-fx-background-color: white");
            vectorDisplayView.setImage(
                    new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));

            // Play reset sound
            resetSoundPlayer.playSound("src/main/resources/sounds/Reset.wav");
        });

        // Set up the stage
        Scene scene = new Scene(sandBoxRoot, 600, 400);
        scene.getStylesheets().add("SandboxStyleSheet.css");
        primaryStage.setTitle("Sandbox");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Instantiate SoundPlayer for info display
        SoundPlayer infoSoundPlayer = new SoundPlayer();

        infoDisplayView.setOnMouseClicked(event -> {
            if (helpBox == null) {
                helpBox = createHelpDialogue(); // Create the help box
                sandBoxPane.getChildren().add(helpBox); // Add it to the pane
            } else {
                sandBoxPane.getChildren().remove(helpBox); // Remove it from the pane
                helpBox = null; // Reset the helpBox variable to null
            }

            // Play info display sound
            infoSoundPlayer.playSound("src/main/resources/sounds/Info.wav");
        });

        // Instantiate SoundPlayer for vector button
        SoundPlayer vectorSoundPlayer = new SoundPlayer();

        vectorDisplayView.setOnMouseClicked(event -> {
            if (isDisplayingVectors) { // Remove vectors
                isDisplayingVectors = false;

                Iterator<Node> paneIterator = sandBoxPane.getChildren().iterator();
                while (paneIterator.hasNext()) {
                    Node node = paneIterator.next();
                    if (node instanceof VectorDisplay) {
                        paneIterator.remove();
                    }
                }
                Iterator<Node> rootIterator = sandBoxRoot.getChildren().iterator();
                while (rootIterator.hasNext()) {
                    Node node = rootIterator.next();
                    if (node instanceof LockPane) {
                        rootIterator.remove();
                    }
                }
                sandBoxRoot.setStyle("-fx-background-color: white");
                vectorDisplayView.setImage(
                        new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));

                System.out.println("unlocked");

            } else { // Add vectors
                isDisplayingVectors = true;
                for (PhysicsObject physObj : physicsObjectList) {
                    if (physObj instanceof Box) {
                        // Cast the physObj to Box to access Box-specific methods
                        Box box = (Box) physObj;
                        updateVectors(box);
                    }
                }
                LockPane locker = new LockPane(sandBoxPane.getWidth(), sandBoxRoot.getHeight());
                locker.setTranslateX(editorPane.getWidth());
                sandBoxRoot.getChildren().add(locker);
                sandBoxRoot.setStyle("-fx-background-color: #8d9393");
                vectorDisplayView.setImage(
                        new Image(getClass().getResourceAsStream("/images/vectorDisplayCrossed.png")));
                System.out.println("Locked");
            }

            // Play vector button sound
            vectorSoundPlayer.playSound("src/main/resources/sounds/Vectors.wav");
        });

        // Add event handler to remove the large rectangle when Escape is pressed
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && helpBox != null) {
                sandBoxRoot.getChildren().remove(helpBox);
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
                        Box newBox = new Box(event.getX() - rectWidth / 2, event.getY() - rectHeight / 2, rectWidth, rectHeight, Color.WHITE, sandBoxPane, planes);
                        physicsObjectList.add(newBox);
                        newBox.addDragListener();
                        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
                        break;
                    case "circle":
                        Pulley newPulley = new Pulley(event.getX(), event.getY(), 25, 10, Color.GRAY, Color.BLACK);
                        sandBoxPane.getChildren().add(newPulley.getCircleGroup());
                        newPulley.addDragListener();
                        physicsObjectList.add(newPulley);
                        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
                        break;
                    case "line":
                        double lineLength = 100;
                        Plane newPlane = createPlane(event.getX() - lineLength / 2, event.getY(), event.getX() + lineLength / 2, event.getY(), Color.BLACK);
                        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
                        break;
                    case "rope":
                        double ropeLength = 100;
                        Rope newRope = new Rope(event.getX() - ropeLength / 2, event.getY(), event.getX() + ropeLength / 2, event.getY(), Color.BROWN, false, false, physicsObjectList);
                        sandBoxPane.getChildren().add(newRope.getLine());
                        newRope.getStyleClass().add("rope-line");
                        newRope.addLineResizeListener();
                        newRope.addDragListener();
                        sandBoxPane.getChildren().addAll(newRope.getStartHandle(), newRope.getEndHandle());
                        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
                        break;
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Plane createPlane(double startX, double startY, double endX, double endY, Color color) {
        Plane newPlane = new Plane(startX, startY, endX, endY, color, this);
        planes.add(newPlane);
        sandBoxPane.getChildren().add(newPlane.getLine());
        newPlane.addLineResizeListener();
        newPlane.addDragListener();
        sandBoxPane.getChildren().addAll(
                newPlane.getStartHandle(),
                newPlane.getEndHandle()
        );
        newPlane.addKeyListener();

        // Update the plane list
        updatePlaneList();

        return newPlane;
    }

    // New method to update the plane list
    public void updatePlaneList() {
        planeListBox.getChildren().clear();
        for (int i = 0; i < planes.size(); i++) {
            Plane plane = planes.get(i);
            HBox planeInfoBox = new HBox(10);
            planeInfoBox.setAlignment(Pos.CENTER_LEFT);

            Label planeLabel = new Label("Plane " + (i + 1));
            planeLabel.setFont(Font.font(14));

            // Create an editable text field for the angle
            TextField angleField = new TextField(String.format("%.1f", plane.calculatePlaneAngle()));
            angleField.setPrefWidth(60);
            angleField.getStyleClass().add("plane-angle-field");

            // Add a degree symbol label
            Label degreeLabel = new Label("°");

            // Add listener to update plane angle when text field is modified
            angleField.setOnAction(event -> {
                try {
                    double newAngle = Double.parseDouble(angleField.getText());
                    plane.setPlaneAngle(newAngle);
                    updatePlaneList(); // Refresh the list to ensure accuracy
                } catch (NumberFormatException e) {
                    // Revert to original angle if invalid input
                    angleField.setText(String.format("%.1f", plane.calculatePlaneAngle()));
                }
            });

            // Bind the angleField to update when plane is moved
            plane.getLine().startXProperty().addListener((obs, oldVal, newVal) -> {
                angleField.setText(String.format("%.1f", plane.calculatePlaneAngle()));
            });
            plane.getLine().startYProperty().addListener((obs, oldVal, newVal) -> {
                angleField.setText(String.format("%.1f", plane.calculatePlaneAngle()));
            });
            plane.getLine().endXProperty().addListener((obs, oldVal, newVal) -> {
                angleField.setText(String.format("%.1f", plane.calculatePlaneAngle()));
            });
            plane.getLine().endYProperty().addListener((obs, oldVal, newVal) -> {
                angleField.setText(String.format("%.1f", plane.calculatePlaneAngle()));
            });

            planeInfoBox.getChildren().addAll(planeLabel, angleField, degreeLabel);
            planeListBox.getChildren().add(planeInfoBox);
        }
    }

    public List<Plane> getPlanes() {
        return planes;
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

    public void updateVectors(Box box) {
        VectorMath.calculateGravityVector(box);

        if(box.isSnapped) {
            VectorMath.calculateNormalVector(box);
            if(box.rectangle.getRotate() != 0)
                VectorMath.calculateFrictionVector(box);
        }
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
}