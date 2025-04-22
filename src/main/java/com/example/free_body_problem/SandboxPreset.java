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
        // Create an inclined plane at 30 degrees
        double centerX = sandboxPane.getWidth() / 2;
        double centerY = sandboxPane.getHeight() / 2;

        double planeLength = 300;
        double angle = 30;
        double angleRad = Math.toRadians(angle);

        double startX = centerX - planeLength * Math.cos(angleRad) / 2;
        double startY = centerY + planeLength * Math.sin(angleRad) / 2;

        // Create a box on the plane
        double boxWidth = 80;
        double boxHeight = 60;
        Box box = new Box(startX + planeLength * 0.25 * Math.cos(angleRad),
                startY - planeLength * 0.25 * Math.sin(angleRad) - boxHeight,
                boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);

        physicsObjectList.add(box);
        sandboxPane.getChildren().add(box.getRectangle());
        box.addDragListener();

        // Snap box to plane
        Snapping.snapBoxToPlane(box, planes);
    }

    private void createSimplePulleyScenario() {
        double centerX = sandboxPane.getWidth() / 2;
        double centerY = sandboxPane.getHeight() / 3;

        // Create pulley
        Pulley pulley = new Pulley(centerX, centerY, 40, 30, Color.BLACK, Color.GRAY, sandboxPane);
        sandboxPane.getChildren().add(pulley.getCircleGroup());
        pulley.addDragListener();
        physicsObjectList.add(pulley);

        // Create boxes
        Box leftBox = new Box(centerX - 150, centerY + 150, 70, 70, Color.WHITE, sandboxPane, planes);
        Box rightBox = new Box(centerX + 150, centerY + 150, 70, 70, Color.WHITE, sandboxPane, planes);

        physicsObjectList.add(leftBox);
        physicsObjectList.add(rightBox);

        sandboxPane.getChildren().add(leftBox.getRectangle());
        sandboxPane.getChildren().add(rightBox.getRectangle());

        leftBox.addDragListener();
        rightBox.addDragListener();

        // Create ropes
        Rope leftRope = new Rope(centerX - 40, centerY, leftBox.getCenterX(), leftBox.getCenterY(),
                false, physicsObjectList);
        Rope rightRope = new Rope(centerX + 40, centerY, rightBox.getCenterX(), rightBox.getCenterY(),
                false, physicsObjectList);

        sandboxPane.getChildren().add(leftRope.getLine());
        sandboxPane.getChildren().add(rightRope.getLine());

        leftRope.addLineResizeListener();
        rightRope.addLineResizeListener();
        leftRope.addDragListener();
        rightRope.addDragListener();

        physicsObjectList.add(leftRope);
        physicsObjectList.add(rightRope);

        // Connect ropes to objects
        Snapping.snapRopeStart(pulley, leftRope);
        Snapping.snapRopeEnd(leftBox, leftRope);
        Snapping.snapRopeStart(pulley, rightRope);
        Snapping.snapRopeEnd(rightBox, rightRope);
    }

    private void createAtwoodMachineScenario() {
        double centerX = sandboxPane.getWidth() / 2;
        double centerY = sandboxPane.getHeight() * 0.25;

        // Create pulley
        Pulley pulley = new Pulley(centerX, centerY, 40, 30, Color.BLACK, Color.GRAY, sandboxPane);
        sandboxPane.getChildren().add(pulley.getCircleGroup());
        pulley.addDragListener();
        physicsObjectList.add(pulley);

        // Create boxes with different masses
        Box leftBox = new Box(centerX - 100, centerY + 150, 60, 60, Color.WHITE, sandboxPane, planes);
        // Set mass using property instead of setMass method
        leftBox.getProperties().put("mass", 1.0); // 1 kg

        Box rightBox = new Box(centerX + 100, centerY + 150, 70, 70, Color.WHITE, sandboxPane, planes);
        // Set mass using property instead of setMass method
        rightBox.getProperties().put("mass", 2.0); // 2 kg - heavier

        physicsObjectList.add(leftBox);
        physicsObjectList.add(rightBox);

        leftBox.addDragListener();
        rightBox.addDragListener();

        // Create ropes
        Rope leftRope = new Rope(centerX - 40, centerY, leftBox.getCenterX(), leftBox.getCenterY(),
                false, physicsObjectList);
        Rope rightRope = new Rope(centerX + 40, centerY, rightBox.getCenterX(), rightBox.getCenterY(),
                false, physicsObjectList);

        sandboxPane.getChildren().add(leftRope.getLine());
        sandboxPane.getChildren().add(rightRope.getLine());

        leftRope.addLineResizeListener();
        rightRope.addLineResizeListener();
        leftRope.addDragListener();
        rightRope.addDragListener();

        physicsObjectList.add(leftRope);
        physicsObjectList.add(rightRope);

        // Connect ropes to objects
        Snapping.snapRopeStart(pulley, leftRope);
        Snapping.snapRopeEnd(leftBox, leftRope);
        Snapping.snapRopeStart(pulley, rightRope);
        Snapping.snapRopeEnd(rightBox, rightRope);
    }

    private void hangingBoxScenario() {
        double centerX = sandboxPane.getWidth() / 2;
        double centerY = sandboxPane.getHeight() / 3;

        // Create pulley
        Pulley pulley = new Pulley(centerX, centerY, 40, 30, Color.BLACK, Color.GRAY, sandboxPane);
        sandboxPane.getChildren().add(pulley.getCircleGroup());
        pulley.addDragListener();
        physicsObjectList.add(pulley);

        // Create a box
        Box box = new Box(centerX - 100, centerY + 150, 70, 70, Color.WHITE, sandboxPane, planes);
        physicsObjectList.add(box);
        sandboxPane.getChildren().add(box.getRectangle());
        box.addDragListener();

        // Create a rope
        Rope rope = new Rope(centerX - 40, centerY, box.getCenterX(), box.getCenterY(),
                false, physicsObjectList);
        sandboxPane.getChildren().add(rope.getLine());
        rope.addLineResizeListener();
        rope.addDragListener();
        physicsObjectList.add(rope);

        // Connect rope to object
        Snapping.snapRopeStart(pulley, rope);
        Snapping.snapRopeEnd(box, rope);
    }
}