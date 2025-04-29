package com.example.free_body_problem;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

/**
 * This class lists the different presets a user can choose to quickly display a classic setup <p>
 * The selection comboBox is also implemented in this class for coherence
 */

public class SandboxPreset {
    private final Sandbox sandbox;
    private final Pane sandboxPane;
    private final List<PhysicsObject> physicsObjectList;
    private final List<Plane> planes;
    private final SoundPlayer soundPlayer;


    public SandboxPreset(Sandbox sandbox, Pane sandboxPane, List<PhysicsObject> physicsObjectList,
                         List<Plane> planes, SoundPlayer soundPlayer) {
        this.sandbox = sandbox;
        this.sandboxPane = sandboxPane;
        this.physicsObjectList = physicsObjectList;
        this.planes = planes;
        this.soundPlayer = soundPlayer;
    }

    public HBox createPresetControls() {
        HBox presetControls = new HBox(10);
        presetControls.setAlignment(Pos.CENTER);
        presetControls.setPadding(new Insets(5));

        ComboBox<String> presetSelector = new ComboBox<>();
        presetSelector.getItems().addAll(
                "Select a preset...",
                "Inclined Plane",
                "Atwood Machine",
                "Hanging Box",
                "Two Rope Hanging Box"
        );
        presetSelector.setValue("Select a preset...");

        Button applyButton = new Button("Apply Preset");
        Tooltip tooltip = new Tooltip("Create a predefined physics scenario");
        tooltip.setShowDelay(Duration.millis(300));
        applyButton.setTooltip(tooltip);

        applyButton.setOnAction(_ -> {
            String selectedPreset = presetSelector.getValue();
            if (!selectedPreset.equals("Select a preset...")) {
                clearSandbox();
                switch (selectedPreset) {
                    case "Inclined Plane":
                        createInclinedPlaneScenario();
                        break;
                    case "Atwood Machine":
                        createAtwoodMachineScenario();
                        break;
                    case "Hanging Box":
                        hangingBoxScenario();
                        break;
                    case "Two Rope Hanging Box":
                        twoRopeHangingBoxScenario();
                        break;
                }
                soundPlayer.playSound("src/main/resources/sounds/Place.wav");
                presetSelector.setValue("Select a preset...");
            }
        });

        presetControls.getChildren().addAll(presetSelector, applyButton);
        return presetControls;
    }

    private void clearSandbox() {
        sandbox.resetSimulation();
    }

    private void createInclinedPlaneScenario() {
        sandboxPane.getChildren().removeIf(node -> node instanceof VectorDisplay);

        double planeLength = 250; // Slightly shorter for better visibility
        double startX = sandboxPane.getWidth() / 3;
        double startY = sandboxPane.getHeight() * 2/3; // Start lower on the screen
        double endX = startX + planeLength * Math.cos(Math.toRadians(45));
        double endY = startY - planeLength * Math.sin(Math.toRadians(45)); // 45 degrees upward

        Plane plane = new Plane(startX, startY, endX, endY, Color.BLACK, sandbox);
        planes.add(plane);
        physicsObjectList.add(plane);

        sandboxPane.getChildren().add(plane.getLine());
        plane.addLineResizeListener();
        plane.addDragListener();
        sandboxPane.getChildren().addAll(
                plane.getStartHandle(),
                plane.getEndHandle()
        );

        double boxWidth = 100;
        double boxHeight = 80;

        double boxPosOnPlane = 0.33; // 33% along the plane
        double boxCenterX = startX + (endX - startX) * boxPosOnPlane;
        double boxCenterY = startY + (endY - startY) * boxPosOnPlane;

        double boxX = boxCenterX - boxWidth / 2;
        double boxY = boxCenterY - boxHeight / 2;

        // box height needs adjustment after rotation
        Box box = new Box(boxX, boxY - boxHeight/2 - 15, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);
        physicsObjectList.add(box);
        box.addDragListener();

        box.isSnapped = true;
        box.snappedToPlane = true;
        box.snappedPlane = plane;
        plane.connectedBoxes.add(box);

        box.getRectangle().setRotate(315);

        sandbox.updatePlaneList();

        if (sandbox.isDisplayingVectors) {
            sandbox.updateAllVectors();
        }

        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
    }

    private void twoRopeHangingBoxScenario() {

        // Set box dimensions as specified
        double boxWidth = 100;
        double boxHeight = 80;

        // Get the roof for reference
        Roof roof = Sandbox.getRoof();
        double roofBottomY = roof.getY() + roof.getHeight();

        // Calculate the box position - center horizontally, below center vertically
        double boxX = Sandbox.sandBoxPane.getWidth() / 2 - boxWidth / 2;
        double boxY = Sandbox.sandBoxPane.getHeight() / 2 + 100;

        // Create the box
        Box box = new Box(boxX, boxY, boxWidth, boxHeight, Color.WHITE, Sandbox.sandBoxPane, planes);
        box.getTextField().setText("10"); // 10kg box
        physicsObjectList.add(box);

        // Calculate rope positions - symmetrically placed
        double separation = 500; // Distance between rope attachment points
        double roofCenterX = Sandbox.sandBoxPane.getWidth() / 2;
        double leftRopeStartX = roofCenterX - separation/2 - 1;  // 1 pixel to the left
        double rightRopeStartX = roofCenterX + separation/2 + 1;  // 1 pixel to the right
        double boxCenterX = box.getCenterX();
        double boxCenterY = box.getCenterY();

        // Create left rope
        Rope leftRope = new Rope(leftRopeStartX, roofBottomY, boxCenterX, boxCenterY,
                false, physicsObjectList);
        leftRope.getStyleClass().add("rope-line");
        Sandbox.sandBoxPane.getChildren().add(leftRope);
        Sandbox.sandBoxPane.getChildren().addAll(leftRope.getStartHandle(), leftRope.getEndHandle());
        leftRope.addLineResizeListener();
        leftRope.addDragListener();
        physicsObjectList.add(leftRope);

        // Create right rope
        Rope rightRope = new Rope(rightRopeStartX, roofBottomY, boxCenterX, boxCenterY,
                false, physicsObjectList);
        rightRope.getStyleClass().add("rope-line");
        Sandbox.sandBoxPane.getChildren().add(rightRope);
        Sandbox.sandBoxPane.getChildren().addAll(rightRope.getStartHandle(), rightRope.getEndHandle());
        rightRope.addLineResizeListener();
        rightRope.addDragListener();
        physicsObjectList.add(rightRope);

        // Connect ropes to box and roof
        box.connectedRopes.put(leftRope, false);
        leftRope.setEndConnection(box);
        box.connectedRopes.put(rightRope, false);
        rightRope.setEndConnection(box);

        roof.connectedRopes.put(leftRope, true);
        leftRope.setStartConnection(roof);
        roof.connectedRopes.put(rightRope, true);
        rightRope.setStartConnection(roof);

        // Position the box under the ropes
        box.setBoxUnderRope();

        // Display the angle between the ropes
        box.displayRopeAngle();

        // Update vectors if enabled
        if (sandbox.isDisplayingVectors) {
            sandbox.updateAllVectors();
        }

        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
    }

    private void createAtwoodMachineScenario() {
        sandboxPane.getChildren().removeIf(node -> node instanceof VectorDisplay);

        double pulleyX = sandboxPane.getWidth() / 2;
        double pulleyY = sandboxPane.getHeight() * 0.2;
        double outerRadius = 40;
        double innerRadius = 37;

        Pulley pulley = new Pulley(pulleyX, pulleyY, outerRadius, innerRadius,
                Color.BLACK, Color.GRAY, sandboxPane);
        sandboxPane.getChildren().add(pulley.getCircleGroup());
        pulley.addDragListener();
        physicsObjectList.add(pulley);

        double boxWidth = 100;
        double boxHeight = 80;
        double leftBoxX = pulleyX - 150;
        double rightBoxX = pulleyX + 70;
        double leftBoxY = pulleyY + 200;
        double rightBoxY = pulleyY + 100;

        Box leftBox = new Box(leftBoxX, leftBoxY, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);
        Box rightBox = new Box(rightBoxX, rightBoxY, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);

        physicsObjectList.add(leftBox);
        physicsObjectList.add(rightBox);

        leftBox.getTextField().setText("8");
        rightBox.getTextField().setText("12");

        Rope leftRope = new Rope(pulleyX - outerRadius, pulleyY, leftBox.getCenterX(), leftBox.getCenterY(),
                false, physicsObjectList);
        leftRope.getStyleClass().add("rope-line");
        Sandbox.sandBoxPane.getChildren().add(leftRope);
        Sandbox.sandBoxPane.getChildren().addAll(leftRope.getStartHandle(), leftRope.getEndHandle());
        leftRope.addLineResizeListener();
        leftRope.addDragListener();
        physicsObjectList.add(leftRope);

        Rope rightRope = new Rope(pulleyX + outerRadius, pulleyY, rightBox.getCenterX(), rightBox.getCenterY(),
                false, physicsObjectList);
        rightRope.getStyleClass().add("rope-line");
        Sandbox.sandBoxPane.getChildren().add(rightRope);
        Sandbox.sandBoxPane.getChildren().addAll(rightRope.getStartHandle(), rightRope.getEndHandle());
        rightRope.addLineResizeListener();
        rightRope.addDragListener();
        physicsObjectList.add(rightRope);

        leftBox.connectedRopes.put(leftRope, false);
        leftRope.setEndConnection(leftBox);

        rightBox.connectedRopes.put(rightRope, false);
        rightRope.setEndConnection(rightBox);

        pulley.connectedRopes.put(leftRope, true);
        pulley.connectedRopes.put(rightRope, true);
        leftRope.setStartConnection(pulley);
        rightRope.setStartConnection(pulley);

        pulley.updateBoxList();

        leftBox.setBoxUnderRope();
        rightBox.setBoxUnderRope();

        // Use Platform.runLater to ensure text fields are properly positioned after the scene is laid out
        javafx.application.Platform.runLater(() -> {
            // Reposition boxes again to update all internal positions including text fields
            leftBox.setBoxUnderRope();
            rightBox.setBoxUnderRope();
        });

        if (sandbox.isDisplayingVectors) {
            sandbox.updateAllVectors();
        }

        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
    }

    private void hangingBoxScenario() {
        sandboxPane.getChildren().removeIf(node -> node instanceof VectorDisplay);

        // Creating the rope
        double startX = sandboxPane.getWidth() / 2;
        double startY = Sandbox.getRoof().getHeight();
        double endX = startX;
        double endY = 300; //arbitrary hanging length

        Rope rope = new Rope(startX, startY, endX, endY, true, physicsObjectList);
        sandboxPane.getChildren().add(rope.getLine());
        rope.getLine().getStyleClass().add("rope-line");
        rope.addLineResizeListener();
        rope.addDragListener();
        sandboxPane.getChildren().addAll(rope.getStartHandle(), rope.getEndHandle());
        physicsObjectList.add(rope);



        // Creating the box
        double boxWidth = 100;
        double boxHeight = 80;
        double boxX = endX - boxWidth / 2;
        double boxY = endY - boxHeight / 2;

        Box box = new Box(boxX, boxY, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);
        physicsObjectList.add(box);

        rope.setStartConnection(Sandbox.getRoof());
        rope.setEndConnection(box);
        box.connectedRopes.put(rope, false);
        javafx.application.Platform.runLater(() ->  box.setBoxUnderRope());
        box.updateConnectedRopes();


        if (sandbox.isDisplayingVectors) {
            sandbox.updateAllVectors();
        }

        soundPlayer.playSound("src/main/resources/sounds/Place.wav");

    }
}