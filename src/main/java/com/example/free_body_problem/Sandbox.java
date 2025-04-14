package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.List;

public class Sandbox extends Application {
    private final List<Plane> planes = new ArrayList<>();
    public ArrayList<PhysicsObject> physicsObjectList = new ArrayList<>();
    public static final double HANDLE_RADIUS = 5; // Handle radius for resizing
    private HBox helpBox;
    private boolean isDisplayingVectors = false;
    public static Roof sandBoxRoof;
    static TextField gravityField;
    static TextField coefficientField;
    public static Pane sandBoxPane;
    private VBox planeListBox; // New field to store the list of plane angles
    private boolean deletionMode = false;
    private Button deleteBT;
    private BorderPane sandBoxRoot;
    private ImageView vectorDisplayView;

    // Checkboxes for vector display control
    private CheckBox gravityVectorCB;
    private CheckBox normalVectorCB;
    private CheckBox frictionVectorCB;
    private CheckBox tension1VectorCB;
    private CheckBox tension2VectorCB;
    private CheckBox netForceVectorCB;

    private CheckBox normalComponentsCB;
    private CheckBox frictionComponentsCB;
    private CheckBox tension1ComponentsCB;
    private CheckBox tension2ComponentsCB;
    private CheckBox netForceComponentsCB;

    // Instantiate SoundPlayer
    private final SoundPlayer soundPlayer = new SoundPlayer();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMaximized(true);

        // Root pane
        sandBoxRoot = new BorderPane();
        sandBoxRoot.setStyle("-fx-border-color: darkgray");

        // Pane for draggable shapes
        sandBoxPane = new Pane();
        sandBoxPane.setPrefSize(600, 350);
        sandBoxRoot.setCenter(sandBoxPane);
        sandBoxRoot.setStyle("-fx-background-color: white");



        // Bottom bar for shape buttons
        HBox bottomBar = new HBox();
        bottomBar.getStyleClass().add("bottom-bar");

        sandBoxRoot.setBottom(bottomBar);
        deleteBT = new Button("DELETE");
        deleteBT.getStyleClass().add("delete-button"); // Apply CSS class
        deleteBT.setOnMouseClicked(e -> {
            toggleDeleteMode();
        });
        addRoofToSandbox();
        // Back to Menu Button
        Button menuBT = new Button("MENU");
        menuBT.getStyleClass().add("menu-button"); // Apply CSS class
        // Instantiate SoundPlayer for menu button
        SoundPlayer menuSoundPlayer = new SoundPlayer();

        menuBT.setOnMouseClicked(e -> {
            showMenu();
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
            Pulley newPulley = new Pulley(100, 50, 25, 10, Color.GRAY, Color.BLACK, sandBoxPane);
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

            // Add to physics object list to enable deletion
            physicsObjectList.add(newRope);
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

        ImageView axesDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/Axes.png")));
        axesDisplayView.setPreserveRatio(true);
        axesDisplayView.setFitHeight(85);
        axesDisplayView.setTranslateX(310);
        axesDisplayView.setTranslateY(sandBoxRoof.getHeight() + 5);
        axesDisplayView.setViewOrder(500);
        sandBoxRoot.getChildren().add(axesDisplayView);

        ImageView infoDisplayView = new ImageView(new Image(getClass().getResourceAsStream("/images/infoDisplay.png")));
        infoDisplayView.setPreserveRatio(true);
        infoDisplayView.setFitHeight(50);
        infoDisplayView.setPickOnBounds(true);


        // Little mute button in-app
        ImageView muteButton = new ImageView();
        muteButton.setPreserveRatio(true);
        muteButton.setFitHeight(50);
        muteButton.setPickOnBounds(true);

        Image muteInactive = new Image(getClass().getResourceAsStream("/images/speaker.png"));
        Image muteActive = new Image(getClass().getResourceAsStream("/images/speakerCrossed.png"));
        muteButton.setImage(muteInactive);

        if(GUI.backgroundMusicPlayer != null) {
            double originalVolume = GUI.backgroundMusicPlayer.getVolume();

            ContextMenu volumeMenu = new ContextMenu();
            MenuItem sliderItem = new MenuItem();
            Slider volumeSlider = new Slider(0, 1, GUI.backgroundMusicPlayer.getVolume());
            sliderItem.setGraphic(volumeSlider);
            sliderItem.setStyle("-fx-padding: 5px;");
            volumeMenu.getItems().add(sliderItem);

            volumeMenu.getStyleClass().add("volume-popup");
            sliderItem.setStyle("-fx-background-color: #f8f8f8");
            volumeSlider.getStyleClass().add("volume-slider");
            volumeSlider.setFocusTraversable(false);

            volumeSlider.setValue(originalVolume);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                SoundPlayer.setVolume(newValue.doubleValue()); // Update volume
                if (GUI.backgroundMusicPlayer != null) {
                    GUI.backgroundMusicPlayer.setVolume(newValue.doubleValue());
                }
            });

            muteButton.setOnMouseEntered(e -> {
                volumeMenu.show(muteButton, Side.TOP, 0, 0);
            });


            muteButton.setOnMouseClicked(e -> {
                if(muteButton.getImage() == muteInactive) {
                    muteButton.setImage(muteActive);
                    GUI.backgroundMusicPlayer.setVolume(0);
                }
                else if(muteButton.getImage() == muteActive) {
                    muteButton.setImage(muteInactive);
                    GUI.backgroundMusicPlayer.setVolume(volumeSlider.getValue());
                }
            });
        }





        // Add elements to the bottom bar
        bottomBar.getChildren().addAll(infoDisplayView, muteButton, leftSpacer, shapeButtons, rightSpacer, menuBT, resetBT, deleteBT);

        // Pane for world settings
        VBox editorPane = new VBox();
        editorPane.getStyleClass().add("editor-pane");
        sandBoxRoot.setLeft(editorPane);

        Label editorLabel = new Label("Sandbox Editor");
        editorLabel.getStyleClass().add("editor-label");

        // Create a scroll pane for plane list
        ScrollPane planeScrollPane = new ScrollPane();
        planeScrollPane.setFitToWidth(true);
        planeScrollPane.setMinHeight(50);
        planeScrollPane.setPrefHeight(50);
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
        restrictTextFieldToNumbers(gravityField);

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
        restrictTextFieldToNumbers(coefficientField);
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

        sandBoxPane.setOnMouseClicked(event -> {
            // Handle deletion if in deletion mode
            if (deletionMode) {
                // Pick the node at exactly the clicked position
                Node clickedNode = event.getPickResult().getIntersectedNode();
                if (clickedNode != null && clickedNode != sandBoxPane) {
                    deleteElement(clickedNode);
                }
            }

            // Always request focus on the pane to handle any TextField unfocus
            sandBoxPane.requestFocus();
        });

        HBox vectorDisplayBox = new HBox();
        vectorDisplayBox.getStyleClass().add("larger-editor-attribute-box");
        Label vectorDisplayLabel = new Label("Display Vectors: ");
        vectorDisplayLabel.getStyleClass().add("editor-attribute-label");
        vectorDisplayView = new ImageView(
                new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));
        vectorDisplayView.setPreserveRatio(true);
        vectorDisplayView.setFitHeight(50);
        vectorDisplayView.setPickOnBounds(true);
        vectorDisplayBox.getChildren().addAll(vectorDisplayLabel, vectorDisplayView);

        // Create checkboxes for different vector types
        gravityVectorCB = new CheckBox("Gravity");
        gravityVectorCB.getStyleClass().add("vector-checkbox");
        gravityVectorCB.setSelected(true);

        normalVectorCB = new CheckBox("Normal Force");
        normalVectorCB.getStyleClass().add("vector-checkbox");
        normalVectorCB.setSelected(true);

        normalComponentsCB = new CheckBox("Normal Components");
        normalComponentsCB.getStyleClass().add("vector-checkbox");

        frictionVectorCB = new CheckBox("Friction");
        frictionVectorCB.getStyleClass().add("vector-checkbox");
        frictionVectorCB.setSelected(true);

        frictionComponentsCB = new CheckBox("Friction Components");
        frictionComponentsCB.getStyleClass().add("vector-checkbox");

        tension1VectorCB = new CheckBox("Tension Force (1)");
        tension1VectorCB.getStyleClass().add("vector-checkbox");
        tension1VectorCB.setSelected(true);

        tension1ComponentsCB = new CheckBox("Tension Components (1)");
        tension1ComponentsCB.getStyleClass().add("vector-checkbox");

        tension2VectorCB = new CheckBox("Tension Force (2)");
        tension2VectorCB.getStyleClass().add("vector-checkbox");
        tension2VectorCB.setSelected(true);

        tension2ComponentsCB = new CheckBox("Tension Components (2)");
        tension2ComponentsCB.getStyleClass().add("vector-checkbox");

        netForceVectorCB = new CheckBox("Net Force");
        netForceVectorCB.getStyleClass().add("vector-checkbox");

        netForceComponentsCB = new CheckBox("Net Force Components");
        netForceComponentsCB.getStyleClass().add("vector-checkbox");



        // Pack checkboxes in a gridPane with appropriate padding
        GridPane vectorCheckboxes = new GridPane();
        vectorCheckboxes.setPadding(new Insets(5, 10, 10, 20));
        vectorCheckboxes.setHgap(10);
        vectorCheckboxes.setVgap(5);

        vectorCheckboxes.add(gravityVectorCB, 0, 0);

        vectorCheckboxes.add(normalVectorCB, 0, 1);
        vectorCheckboxes.add(normalComponentsCB, 1, 1);

        vectorCheckboxes.add(frictionVectorCB, 0, 2);
        vectorCheckboxes.add(frictionComponentsCB,1, 2);

        vectorCheckboxes.add(tension1VectorCB, 0, 3);
        vectorCheckboxes.add(tension1ComponentsCB, 1, 3);

        vectorCheckboxes.add(tension2VectorCB, 0, 4);
        vectorCheckboxes.add(tension2ComponentsCB, 1, 4);

        vectorCheckboxes.add(netForceVectorCB, 0, 5);
        vectorCheckboxes.add(netForceComponentsCB, 1, 5);


        // Add event handlers to checkboxes to update vector display
        gravityVectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });

        normalVectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });
        normalComponentsCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });

        frictionVectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });
        frictionComponentsCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });

        tension1VectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });
        tension1ComponentsCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(isDisplayingVectors)
                updateAllVectors();
        });

        tension2VectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });
        tension2ComponentsCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if(isDisplayingVectors)
                updateAllVectors();
        });

        netForceVectorCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });
        netForceComponentsCB.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (isDisplayingVectors)
                updateAllVectors();
        });


        // Add elements to the editor pane
        editorPane.getChildren().addAll(
                editorLabel,
                planeScrollPane,  // Add the scroll pane
                gravityBox,
                coefficientBox,
                vectorDisplayBox,// Add the vector controls section
                vectorCheckboxes      // Add the vector checkboxes
        );

        // Instantiate SoundPlayer for reset button
        SoundPlayer resetSoundPlayer = new SoundPlayer();

        resetBT.setOnMouseClicked(event -> {
            Alert warngAlert = new Alert(Alert.AlertType.CONFIRMATION);
            warngAlert.setTitle("Warning");
            warngAlert.setHeaderText(null);
            warngAlert.setContentText("Are you sure you want to reset your progress?");

            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            warngAlert.getButtonTypes().setAll(yesButton, cancelButton);

            Optional<ButtonType> result = warngAlert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                resetSimulation();
                resetSoundPlayer.playSound("src/main/resources/sounds/Reset.wav");
            }


        });

        // Set up the stage
        Scene scene = new Scene(sandBoxRoot, 600, 400);
        scene.getStylesheets().add("SandboxStyleSheet.css");
        primaryStage.setTitle("Sandbox");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Instantiate SoundPlayer for info display
        SoundPlayer infoSoundPlayer = new SoundPlayer();

        // Keep the existing click handler
        infoDisplayView.setOnMouseClicked(event -> {
            toggleHelpBox();

            // Play info display sound
            infoSoundPlayer.playSound("src/main/resources/sounds/Info.wav");
        });

        // Instantiate SoundPlayer for vector button
        SoundPlayer vectorSoundPlayer = new SoundPlayer();

        vectorDisplayView.setOnMouseClicked(event -> {
            toggleVectorsMode();

            // Play vector button sound
            vectorSoundPlayer.playSound("src/main/resources/sounds/Vectors.wav");
        });

        // Add comprehensive key handler for all shortcuts
        sandBoxPane.getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case I:
                    toggleHelpBox();
                    infoSoundPlayer.playSound("src/main/resources/sounds/Info.wav");
                    break;
                case D:
                    toggleDeleteMode();
                    break;
                case R:
                    resetSimulation();
                    resetSoundPlayer.playSound("src/main/resources/sounds/Reset.wav");
                    break;
                case M:
                    showMenu();
                    menuSoundPlayer.playSound("src/main/resources/sounds/Menu.wav");
                    break;
                case V:
                    toggleVectorsMode();
                    vectorSoundPlayer.playSound("src/main/resources/sounds/Vectors.wav");
                    break;
            }
        });

        // Remove the old key handler to avoid duplication
        sandBoxPane.setOnKeyPressed(null);

        // Make the pane focusable
        sandBoxPane.setFocusTraversable(true);
    }

    private void toggleDeleteMode() {
        deletionMode = !deletionMode;

        if (deletionMode) {
            // Visual indication that delete mode is active
            deleteBT.getStyleClass().remove("delete-button");
            deleteBT.getStyleClass().add("delete-button-active");
            sandBoxPane.setCursor(Cursor.CROSSHAIR);
        } else {
            // Return to normal mode
            deleteBT.getStyleClass().remove("delete-button-active");
            deleteBT.getStyleClass().add("delete-button");
            sandBoxPane.setCursor(Cursor.DEFAULT);
        }
    }

    private void resetSimulation() {
        sandBoxPane.getChildren().clear();
        gravityField.setText("9.8");
        physicsObjectList.clear();
        planes.clear();
        planeListBox.getChildren().clear();  // Clear the plane list
        isDisplayingVectors = false;

        // Reset checkboxes to selected state
        gravityVectorCB.setSelected(true);
        normalVectorCB.setSelected(true);
        frictionVectorCB.setSelected(true);
        netForceVectorCB.setSelected(true);

        // Removing the lock
        Iterator<Node> rootIterator = sandBoxRoot.getChildren().iterator();
        while (rootIterator.hasNext()) {
            Node node = rootIterator.next();
            if (node instanceof LockPane) {
                rootIterator.remove();
            }
        }
        addRoofToSandbox();
        sandBoxRoot.setStyle("-fx-background-color: white");
        vectorDisplayView.setImage(
                new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));
    }

    private void showMenu() {
        // Play menu button sound - sound is played where method is called
        GUI app = new GUI();
        app.start(new Stage());
        ((Stage) sandBoxPane.getScene().getWindow()).close();
    }

    private void toggleVectorsMode() {
        if (isDisplayingVectors) { // Remove vectors
            isDisplayingVectors = false;

                sandBoxPane.getChildren().removeIf(node -> node instanceof VectorDisplay);
                sandBoxRoot.getChildren().removeIf(node -> node instanceof LockPane);

                for (PhysicsObject obj : physicsObjectList) {
                    if(obj instanceof Box)
                        ((Box) obj).resetNetVectorComponents();
                }

            sandBoxRoot.setStyle("-fx-background-color: white");
            vectorDisplayView.setImage(
                    new Image(getClass().getResourceAsStream("/images/vectorDisplay.png")));

        } else { // Add vectors
            isDisplayingVectors = true;
            updateAllVectors();

            LockPane locker = new LockPane(sandBoxPane.getWidth(), sandBoxRoot.getHeight());
            locker.setTranslateX(((VBox)sandBoxRoot.getLeft()).getWidth());
            sandBoxRoot.getChildren().add(locker);
            sandBoxRoot.setStyle("-fx-background-color: #8d9393");
            vectorDisplayView.setImage(
                    new Image(getClass().getResourceAsStream("/images/vectorDisplayCrossed.png")));
        }
    }

    private void updateAllVectors() {
        // Clear all existing vectors
        Iterator<Node> iterator = sandBoxPane.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof VectorDisplay) {
                iterator.remove();
            }
        }

        // Recalculate vectors for all boxes
        for (PhysicsObject physObj : physicsObjectList) {
            if (physObj instanceof Box) {
                updateVectors((Box) physObj);
            }
        }
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
                        Pulley newPulley = new Pulley(event.getX(), event.getY(), 25, 10, Color.GRAY, Color.BLACK, sandBoxPane);
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
                        physicsObjectList.add(newRope);
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
        physicsObjectList.add(newPlane);

        sandBoxPane.getChildren().add(newPlane.getLine());
        newPlane.addLineResizeListener();
        newPlane.addDragListener();
        sandBoxPane.getChildren().addAll(
                newPlane.getStartHandle(),
                newPlane.getEndHandle()
        );

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
            restrictTextFieldToNumbers(angleField);

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

    public static Roof getRoof(){
        return sandBoxRoof;

    }

    // Helper method to create a line button
    private Line createButtonLine(double length, double strokeWidth, Color color) {
        Line line = new Line(0, 0, length, 0);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);
        return line;
    }

    public void updateVectors(Box box) {
        // Remove existing vectors for this box
        Iterator<Node> iterator = sandBoxPane.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof VectorDisplay &&
                    ((VectorDisplay)node).getUserData() == box) {
                iterator.remove();
            }
        }

        // Reset force totals before recalculating
        box.totalXForce = 0;
        box.totalYForce = 0;

        // Calculate ALL forces regardless of checkbox selections
        // This ensures physics remains consistent

        // Always calculated gravity
        VectorMath.calculateGravityVector(box);

        //Calculate normal if on a plane
        if (box.isSnapped) {
            VectorMath.calculateNormalVector(box);
        }

        //Calculate friction if box has an angle
        if (box.isSnapped && box.rectangle.getRotate() != 0) {
            VectorMath.calculateFrictionVector(box);
        }

        //Tension case 1: 1 rope, 1 box
        if(box.connectedRopes.size() == 1) {
            Map.Entry<Rope, Boolean> hashMap = box.connectedRopes.entrySet().iterator().next();
            Rope rope = hashMap.getKey();
            // Avoids calculating tension on unconnected ropes
            if(rope.getEndConnection() instanceof Roof || rope.getStartConnection() instanceof Roof) {
                VectorMath.calculateTension1Rope(box, rope);
            }
        }

        //Tension case 2: 2 ropes, 1 box
        if(box.connectedRopes.size() == 2) {
            Iterator<Map.Entry<Rope, Boolean>> ropeIterator = box.connectedRopes.entrySet().iterator();

            if (ropeIterator.hasNext()) {
                Rope rope1 = ropeIterator.next().getKey();
                Rope rope2 = ropeIterator.next().getKey();
                Rope[] ropesInOrder = determineRopePositions(rope1, rope2);

                // Making sure there are no pulley connections
                if (!(rope1.getEndConnection() instanceof Pulley || rope1.getStartConnection() instanceof Pulley) &&
                        !(rope2.getEndConnection() instanceof Pulley || rope2.getStartConnection() instanceof Pulley)) {
                    VectorMath.calculateTension2Ropes(box, ropesInOrder[0], ropesInOrder[1]);
                }
            }
        }

        VectorMath.calculateNetVector(box);


        System.out.println();

        iterator = sandBoxPane.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof VectorDisplay) {
                VectorDisplay vectorDisplay = (VectorDisplay) node;
                String vectorName = vectorDisplay.getForceName().getText();

                // Remove vectors if their main checkbox is unchecked
                if ((!gravityVectorCB.isSelected() && vectorName.equals("Gravity")) ||
                        (!normalVectorCB.isSelected() && vectorName.equals("Normal")) ||
                        (!frictionVectorCB.isSelected() && vectorName.equals("Friction")) ||
                        (!tension1VectorCB.isSelected() && vectorName.equals("Tension")) ||
                        (!tension1VectorCB.isSelected() && vectorName.equals("T1")) ||
                        (!tension2VectorCB.isSelected() && vectorName.equals("T2")) ||
                        (!netForceVectorCB.isSelected() && vectorName.equals("Net"))) {
                    iterator.remove();
                }

                // Remove components if their component checkbox is unchecked
                if ((!normalComponentsCB.isSelected() && (vectorName.equals("Nx") || vectorName.equals("Ny"))) ||
                        (!frictionComponentsCB.isSelected() && (vectorName.equals("Fx") || vectorName.equals("Fy"))) ||
                        (!tension1ComponentsCB.isSelected() && (vectorName.equals("Tx") || vectorName.equals("Ty"))) ||
                        (!tension1ComponentsCB.isSelected() && (vectorName.equals("T1x") || vectorName.equals("T1y"))) ||
                        (!tension2ComponentsCB.isSelected() && (vectorName.equals("T2x") || vectorName.equals("T2y"))) ||
                        (!netForceComponentsCB.isSelected() && (vectorName.equals("NetX") || vectorName.equals("NetY")))) {
                    iterator.remove();
                }
            }
        }

    }

    // Extract the toggle functionality to a reusable method
    private void toggleHelpBox() {
        if (helpBox == null) {
            helpBox = createHelpDialogue(); // Create the help box
            sandBoxPane.getChildren().add(helpBox); // Add it to the pane
        } else {
            sandBoxPane.getChildren().remove(helpBox); // Remove it from the pane
            helpBox = null; // Reset the helpBox variable to null
        }
    }

    private void restrictTextFieldToNumbers(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }

    private void deleteElement(Node clickedNode) {
        // Check if the clicked node is directly a node we recognize
        for (Object obj : new ArrayList<>(physicsObjectList)) {
            if (obj instanceof Box &&
                    (((Box)obj).getRectangle() == clickedNode ||
                            ((Box)obj).getResizeHandle() == clickedNode ||
                            ((Box)obj).getRotateHandle() == clickedNode)) {
                deleteBoxObject((Box)obj);
                return;
            } else if (obj instanceof Pulley &&
                    (((Pulley)obj).getCircleGroup() == clickedNode ||
                            clickedNode.getParent() == ((Pulley)obj).getCircleGroup())) {
                deletePulleyObject((Pulley)obj);
                return;
            } else if (obj instanceof Plane &&
                    (((Plane)obj).getLine() == clickedNode ||
                            ((Plane)obj).getStartHandle() == clickedNode ||
                            ((Plane)obj).getEndHandle() == clickedNode)) {
                deletePlaneObject((Plane)obj);
                return;
            } else if (obj instanceof Rope) {
                Rope rope = (Rope)obj;
                if (rope.getLine() == clickedNode ||
                        rope.getStartHandle() == clickedNode ||
                        rope.getEndHandle() == clickedNode) {
                    deleteRopeObject(rope);
                    return;
                }
            }
        }

        // If not found directly, traverse userData
        Node nodeToCheck = clickedNode;
        while (nodeToCheck != null) {
            Object userData = nodeToCheck.getUserData();

            if (userData instanceof Box) {
                deleteBoxObject((Box)userData);
                return;
            } else if (userData instanceof Pulley) {
                deletePulleyObject((Pulley)userData);
                return;
            } else if (userData instanceof Plane) {
                deletePlaneObject((Plane)userData);
                return;
            } else if (userData instanceof Rope) {
                deleteRopeObject((Rope)userData);
                return;
            } else if (userData instanceof Group) {
                // Check if this group contains our objects
                Group group = (Group)userData;
                for (Node child : group.getChildren()) {
                    if (child == clickedNode) {
                        // Check if this group is part of our objects
                        for (Object obj : physicsObjectList) {
                            if (obj instanceof Pulley &&
                                    ((Pulley)obj).getCircleGroup() == group) {
                                deletePulleyObject((Pulley)obj);
                                return;
                            }
                        }
                    }
                }
            }

            // Move up to parent node
            nodeToCheck = nodeToCheck.getParent();
        }

        // Special case: check if the clicked node is part of a Rope but not directly matching
        if (clickedNode instanceof Line) {
            for (Object obj : physicsObjectList) {
                if (obj instanceof Rope && ((Rope)obj).getLine() == clickedNode) {
                    deleteRopeObject((Rope)obj);
                    return;
                }
            }
        }
    }

    private void deleteRopeObject(Rope rope) {
        // First disconnect any connections the rope has
        if (rope.getStartConnection() != null) {
            rope.getStartConnection().connectedRopes.remove(rope);
        }
        if (rope.getEndConnection() != null) {
            rope.getEndConnection().connectedRopes.remove(rope);
        }

        // Remove visual elements with null checks
        if (rope.getLine() != null) {
            sandBoxPane.getChildren().remove(rope.getLine());
        }
        if (rope.getStartHandle() != null) {
            sandBoxPane.getChildren().remove(rope.getStartHandle());
        }
        if (rope.getEndHandle() != null) {
            sandBoxPane.getChildren().remove(rope.getEndHandle());
        }

        // Remove from physics object list and play sound
        physicsObjectList.remove(rope);
        soundPlayer.playSound("src/main/resources/sounds/Delete.wav");
    }

    private void deleteBoxObject(Box box) {
        sandBoxPane.getChildren().remove(box.getRectangle());
        if (box.getResizeHandle() != null) sandBoxPane.getChildren().remove(box.getResizeHandle());
        if (box.getRotateHandle() != null) sandBoxPane.getChildren().remove(box.getRotateHandle());

        // Remove the HBox containing the mass field - improved method for finding it
        for (Node node : new ArrayList<>(sandBoxPane.getChildren())) {
            if (node instanceof HBox && ((HBox)node).getChildren().size() > 0) {
                double boxCenterX = box.getCenterX();
                double boxCenterY = box.getCenterY();
                double nodeX = node.getLayoutX();
                double nodeY = node.getLayoutY();
                double nodeWidth = node.getBoundsInLocal().getWidth();
                double nodeHeight = node.getBoundsInLocal().getHeight();

                // Check if this HBox is within the expected area for this box's mass field
                if (Math.abs(nodeX + nodeWidth/2 - boxCenterX) < 50 &&
                        Math.abs(nodeY + nodeHeight/2 - boxCenterY) < 50) {
                    sandBoxPane.getChildren().remove(node);
                    break;
                }
            }
        }

        physicsObjectList.remove(box);
        soundPlayer.playSound("src/main/resources/sounds/Delete.wav");
    }

    private void deletePulleyObject(Pulley pulley) {
        sandBoxPane.getChildren().remove(pulley.getCircleGroup());
        physicsObjectList.remove(pulley);
        soundPlayer.playSound("src/main/resources/sounds/Delete.wav");
    }

    private void deletePlaneObject(Plane plane) {
        sandBoxPane.getChildren().remove(plane.getLine());
        sandBoxPane.getChildren().remove(plane.getStartHandle());
        sandBoxPane.getChildren().remove(plane.getEndHandle());
        planes.remove(plane);
        physicsObjectList.remove(plane);
        updatePlaneList();
        soundPlayer.playSound("src/main/resources/sounds/Delete.wav");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static HBox createHelpDialogue() {
        HBox helpBox = new HBox();
        helpBox.setAlignment(Pos.CENTER);

        // Set high z-order to ensure help stays on top
        helpBox.setViewOrder(-1000);  // Lower values appear on top

        // Define dimensions
        double boxWidth = 900;
        double boxHeight = 400;

        // Create background rectangle
        Rectangle background = new Rectangle(boxWidth, boxHeight);
        background.setFill(Color.LIGHTBLUE);
        background.setStroke(Color.BLACK);

        // Create container for text content
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));
        contentBox.setMaxWidth(boxWidth - 40);
        contentBox.setMaxHeight(boxHeight - 40);
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Create title
        Text title = new Text("Help Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        title.setFill(Color.BLACK);
        // Add drop shadow to make title more visible
        title.setEffect(new javafx.scene.effect.DropShadow(5, Color.WHITE));

        // Create text area for help content
        TextArea textArea = new TextArea();
        textArea.getStyleClass().add("help-text-area");
        textArea.setEditable(false);
        textArea.setPrefWidth(boxWidth - 60);
        textArea.setPrefHeight(boxHeight - 100);
        // Ensure text area is on top
        textArea.setViewOrder(-1001);

        // Load content from file
        InputStream inputStream = Sandbox.class.getClassLoader().getResourceAsStream("helpText.txt");
        if (inputStream != null) {
            try {
                Scanner scanner = new Scanner(inputStream);
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                scanner.close();
                textArea.setText(content.toString());
            } catch (Exception e) {
                textArea.setText("Help content not found!");
            }
        }

        // Add elements to containers
        contentBox.getChildren().addAll(title, textArea);
        StackPane centerPane = new StackPane(background, contentBox);
        helpBox.getChildren().add(centerPane);

        // Add a semi-transparent overlay to block interaction with elements beneath
        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(sandBoxPane.widthProperty());
        overlay.heightProperty().bind(sandBoxPane.heightProperty());
        overlay.setFill(Color.rgb(0, 0, 0, 0.3));
        overlay.setViewOrder(-999);  // Just behind the help box

        // Center the help box in the sandbox pane
        helpBox.layoutXProperty().bind(sandBoxPane.widthProperty().subtract(boxWidth).divide(2));
        helpBox.layoutYProperty().bind(sandBoxPane.heightProperty().subtract(boxHeight).divide(2));

        // Create container that includes both overlay and help box
        Group helpGroup = new Group();
        helpGroup.getChildren().addAll(overlay, helpBox);

        // Removed the close button code that was here

        return helpBox;
    }

    private void addRoofToSandbox() {
        // Create a roof that spans the width of the sandbox
        double roofHeight = 25; // Height of the roof
        double roofY = 0; // Position at the top of the sandbox

        Roof roof = new Roof(0, roofY, sandBoxPane.getWidth(), roofHeight, Color.DARKGRAY);

        // Add the ENTIRE Roof object (which is a PhysicsObject) to the sandbox
        sandBoxPane.getChildren().add(roof);  // Changed from roof.getRoof() to just roof

        // Add to the physics object list to enable interactions
        physicsObjectList.add(roof);
        sandBoxRoof = roof;

        // Make the roof adapt to sandbox width changes
        sandBoxPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            roof.adjustToSandboxWidth(newVal.doubleValue());
        });
    }

    private Rope[] determineRopePositions(Rope rope1, Rope rope2) {
        //Checking what point is higher (start or end) on both
        boolean startHigherFor1 = false;
        if(rope1.getLine().getStartY() > rope1.getLine().getEndY()) {
            startHigherFor1 = true;
        }
        boolean startHigherFor2 = false;
        if(rope2.getLine().getStartY() > rope2.getLine().getEndY()) {
            startHigherFor2 = true;
        }

        //Checking which rope is to the right, which is to the left
        boolean rope1ToTheRight = false;
        if(startHigherFor1 && startHigherFor2 && rope1.getLine().getStartX() > rope2.getLine().getStartX()) {
            rope1ToTheRight = true;
        }
        else if(!startHigherFor1 && startHigherFor2 && rope2.getLine().getEndX() > rope1.getLine().getStartX()) {
            rope1ToTheRight = true;
        }
        else if(startHigherFor1 && !startHigherFor2 && rope1.getLine().getStartX() > rope2.getLine().getEndX()) {
            rope1ToTheRight = true;
        }
        else if(!startHigherFor1 && !startHigherFor2 && rope1.getLine().getEndX() > rope2.getLine().getEndX()) {
            rope1ToTheRight = true;
        }

        Rope[] ropesInOrder = new Rope[2];
        if(rope1ToTheRight) {
            ropesInOrder[0] = rope2;
            ropesInOrder[1] = rope1;
        }
        else {
            ropesInOrder[1] = rope1;
            ropesInOrder[0] = rope2;
        }

        return ropesInOrder;
    }

    private void saveStateToFile(File file) {
        SaveState saveState = new SaveState();
        saveState.gravitySetting = Double.parseDouble(gravityField.getText());
        saveState.frictionCoefficientSetting = Double.parseDouble(coefficientField.getText());
        saveState.physicsObjects = physicsObjectList;


        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(saveState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SaveState loadSaveState(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (SaveState)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}