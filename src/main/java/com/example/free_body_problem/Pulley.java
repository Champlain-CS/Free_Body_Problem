package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pulley {
    private Group circleGroup;

    public Pulley(double x, double y, double outerRadius, double innerRadius, Color outerColor, Color innerColor) {
        Circle outerCircle = new Circle(x, y, outerRadius, outerColor);
        outerCircle.setStroke(null);

        Circle innerCircle = new Circle(x, y, innerRadius, innerColor);
        innerCircle.setStroke(null);

        circleGroup = new Group(outerCircle, innerCircle);
    }

    public Group getCircleGroup() {
        return circleGroup;
    }

    public void addDragListener() {
        circleGroup.setOnMousePressed(event -> {
            circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        circleGroup.setOnMouseDragged(event -> {
            double[] offset = (double[]) circleGroup.getUserData();
            for (Node node : circleGroup.getChildren()) {
                Circle circle = (Circle) node;
                circle.setCenterX(circle.getCenterX() + (event.getSceneX() - offset[0]));
                circle.setCenterY(circle.getCenterY() + (event.getSceneY() - offset[1]));
            }

            circleGroup.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        circleGroup.setOnMouseReleased(event -> {
            // Handle mouse release if needed
        });
    }
}