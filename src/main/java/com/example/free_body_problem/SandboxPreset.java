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

        Box box = new Box(boxX, boxY, boxWidth, boxHeight, Color.WHITE, sandboxPane, planes);
        physicsObjectList.add(box);
        box.addDragListener();

        box.isSnapped = true;
        box.snappedToPlane = true;
        box.snappedPlane = plane;
        plane.connectedBoxes.add(box);

        box.getRectangle().setRotate(-45-180);

        sandboxPane.getChildren().add(box.getRectangle());

        sandbox.updatePlaneList();

        if (sandbox.isDisplayingVectors) {
            sandbox.updateAllVectors();
        }

        soundPlayer.playSound("src/main/resources/sounds/Place.wav");
    }

    private void createSimplePulleyScenario() {

    }

    private void createAtwoodMachineScenario() {

    }

    private void hangingBoxScenario() {

    }
}