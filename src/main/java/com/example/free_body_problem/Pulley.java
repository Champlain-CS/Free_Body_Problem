package com.example.free_body_problem;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pulley {
    private Group circleGroup;

    public Pulley(double x, double y, double outerRadius, double innerRadius, Color outerColor, Color innerColor) {
        Circle outerCircle = new Circle(x, y, outerRadius, outerColor);
        outerCircle.setStroke(Color.BLACK);

        Circle innerCircle = new Circle(x, y, innerRadius, innerColor);
        innerCircle.setStroke(Color.BLACK);

        circleGroup = new Group(outerCircle, innerCircle);
    }

    public Group getCircleGroup() {
        return circleGroup;
    }
}