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
                "Simple Pulley",
                "Atwood Machine",
                "Hanging Box"
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
                    case "Simple Pulley":
                        createSimplePulleyScenario();
                        break;
                    case "Atwood Machine":
                        createAtwoodMachineScenario();
                        break;
                    case "Hanging Box":
                        hangingBoxScenario();
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

        Box box = new Box(boxX, boxY - boxHeight/2 - 5, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);
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

    private void createSimplePulleyScenario() {

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


    }
}