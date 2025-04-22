package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Pulley extends PhysicsObject {
    public Group circleGroup;
    private Pane parentContainer;
    private double radius;
    private double grooveRadius; // Radius where the rope sits
    public List<Box> connectedBoxes = new ArrayList<>();

    public Pulley() {}

    public Pulley(double x, double y, double outerRadius, double innerRadius, Color wheelColor, Color hubColor, Pane parentContainer) {
        this.parentContainer = parentContainer;
        this.radius = outerRadius;
        this.grooveRadius = outerRadius - 2; // Groove is slightly inset from the edge

        // Create the group that will hold all visual elements
        circleGroup = new Group();
        circleGroup.setUserData(this);

        // Create the main wheel with gradient for 3D effect
        RadialGradient wheelGradient = new RadialGradient(
                0, 0,
                0.5, 0.5, 1,
                true, CycleMethod.NO_CYCLE,
                new Stop(0, wheelColor.brighter()),
                new Stop(0.7, wheelColor),
                new Stop(1, wheelColor.darker())
        );

        Circle wheelBody = new Circle(x, y, outerRadius, wheelGradient);
        wheelBody.setUserData(this);

        // Add lighting effect to the wheel
        Lighting lighting = new Lighting();
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);
        light.setElevation(30.0);
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        wheelBody.setEffect(lighting);

        // Create the groove/channel where the rope sits
        Circle groove = new Circle(x, y, grooveRadius, Color.TRANSPARENT);
        groove.setStroke(Color.gray(0.2));
        groove.setStrokeWidth(3);
        groove.setUserData(this);

        // Create central hub/axle
        RadialGradient hubGradient = new RadialGradient(
                0, 0,
                0.5, 0.5, 1,
                true, CycleMethod.NO_CYCLE,
                new Stop(0, hubColor.brighter()),
                new Stop(0.7, hubColor),
                new Stop(1, hubColor.darker().darker())
        );

        Circle hub = new Circle(x, y, innerRadius, hubGradient);
        hub.setUserData(this);

        // Add spokes (typically 6-8 for a pulley)
        int numberOfSpokes = 6;
        for (int i = 0; i < numberOfSpokes; i++) {
            double angle = Math.PI * 2 * i / numberOfSpokes;
            double innerX = x + innerRadius * Math.cos(angle);
            double innerY = y + innerRadius * Math.sin(angle);
            double outerX = x + (grooveRadius - 1) * Math.cos(angle);
            double outerY = y + (grooveRadius - 1) * Math.sin(angle);

            Line spoke = new Line(innerX, innerY, outerX, outerY);
            spoke.setStroke(Color.gray(0.4));
            spoke.setStrokeWidth(2.5);
            spoke.setStrokeLineCap(StrokeLineCap.ROUND);
            spoke.setUserData(this);

            circleGroup.getChildren().add(spoke);
        }

        // Adding components to the group in order (background to foreground)
        circleGroup.getChildren().addAll(wheelBody, groove, hub);

        // Add central dot/bolt
        Circle centerBolt = new Circle(x, y, innerRadius/3, Color.gray(0.2));
        circleGroup.getChildren().add(centerBolt);

        addDragListener();
    }

    // Add getter for radius
    public double getRadius() {
        return radius;
    }

    public double getCenterX() {
        // Find the center of the first circle (wheel body)
        for (Node node : circleGroup.getChildren()) {
            if (node instanceof Circle circle) {
                return circle.getCenterX();
            }
        }
        return circleGroup.getBoundsInParent().getCenterX();
    }

    public double getCenterY() {
        // Find the center of the first circle (wheel body)
        for (Node node : circleGroup.getChildren()) {
            if (node instanceof Circle circle) {
                return circle.getCenterY();
            }
        }
        return circleGroup.getBoundsInParent().getCenterY();
    }

    public Group getCircleGroup() {
        return circleGroup;
    }

    @Override
    public void updateConnectedRopes() {
        super.updateConnectedRopes();

        // After updating connected ropes, make sure they attach to the correct sides
        if (!connectedRopes.isEmpty()) {
            int ropeIndex = 0;
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();

                // Calculate the angle based on index (first rope left, second rope right)
                double snapAngle = (ropeIndex == 0) ? Math.PI : 0; // 180° or 0°

                // Calculate position on groove perimeter (where rope would actually sit)
                double snapX = getCenterX() + grooveRadius * Math.cos(snapAngle);
                double snapY = getCenterY() + grooveRadius * Math.sin(snapAngle);

                // Update rope position based on which end is connected
                if (isStartConnected) {
                    rope.getLine().setStartX(snapX);
                    rope.getLine().setStartY(snapY);
                } else {
                    rope.getLine().setEndX(snapX);
                    rope.getLine().setEndY(snapY);
                }

                ropeIndex++;
            }
        }
    }

    public void addDragListener() {
        circleGroup.setOnMousePressed(event -> circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()}));

        circleGroup.setOnMouseDragged(event -> {
            double[] initialPress = (double[]) circleGroup.getUserData();
            double offsetX = event.getSceneX() - initialPress[0];
            double offsetY = event.getSceneY() - initialPress[1];

            double centerX = getCenterX();
            double centerY = getCenterY();

            // Calculate proposed new position
            double newX = centerX + offsetX;
            double newY = centerY + offsetY;

            // Constrain to parent bounds
            newX = Math.max(radius, Math.min(newX, parentContainer.getWidth() - radius));
            newY = Math.max(radius, Math.min(newY, parentContainer.getHeight() - radius));

            // Apply rope constraints
            newX = applyRopeConstraints(newX);

            // Only update if position has changed
            if (newX != centerX || newY != centerY) {
                double deltaX = newX - centerX;
                double deltaY = newY - centerY;

                // Update the position of all elements in the group
                for (Node node : circleGroup.getChildren()) {
                    if (node instanceof Circle circle) {
                        circle.setCenterX(circle.getCenterX() + deltaX);
                        circle.setCenterY(circle.getCenterY() + deltaY);
                    } else if (node instanceof Line line) {
                        line.setStartX(line.getStartX() + deltaX);
                        line.setStartY(line.getStartY() + deltaY);
                        line.setEndX(line.getEndX() + deltaX);
                        line.setEndY(line.getEndY() + deltaY);
                    }
                }

                initialPress[0] = event.getSceneX();
                initialPress[1] = event.getSceneY();
                circleGroup.setUserData(initialPress);

                updateConnectedRopes();
            }
        });

        circleGroup.setOnMouseReleased(_ -> {
            for (Box box: connectedBoxes){
                box.setBoxUnderRope();
            }
        });
    }

    private double applyRopeConstraints(double newX) {
        if (connectedRopes.size() > 1) {
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;

            // Find the leftmost and rightmost connection points
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                boolean isStartConnected = entry.getValue();

                double ropeX;
                if (isStartConnected) {
                    ropeX = rope.getLine().getEndX();
                } else {
                    ropeX = rope.getLine().getStartX();
                }

                // The pulley can't move past the connection point
                if (ropeX < minX) {
                    minX = ropeX;
                }
                if (ropeX > maxX) {
                    maxX = ropeX;
                }
            }

            // Constrain the pulley position based on the rope connections
            return Math.max(minX, Math.min(newX, maxX));
        }
        return newX;
    }

    public void updateBoxList() {
        //check if the list is not empty
        if (!connectedRopes.isEmpty()) {
            connectedBoxes.clear();
            for (Map.Entry<Rope, Boolean> entry : connectedRopes.entrySet()) {
                Rope rope = entry.getKey();
                if (rope.getStartConnection() instanceof Box ropeBox){
                    connectedBoxes.add(ropeBox);
                }
                else if (rope.getEndConnection() instanceof Box ropeBox){
                    connectedBoxes.add(ropeBox);
                }
            }
        }
    }
}