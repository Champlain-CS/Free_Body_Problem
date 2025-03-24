package com.example.free_body_problem;

import javafx.scene.paint.Color;

public final class VectorMath {
    private Sandbox sandbox;
    private static double normalMag;

    public VectorMath(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    public static void calculateGravityVector(Box box) {
        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double massValue = Double.parseDouble(box.getTextField().getText());
        double positionX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionY = box.getRectangle().getY() + box.getRectangle().getHeight();

        double magnitude = massValue*gravityValue;

        VectorDisplay gravityVector = new VectorDisplay(positionX, positionY,
                magnitude, 90, "Gravity", Color.BLUE);
        box.gravityVector = gravityVector;
        Sandbox.sandBoxPane.getChildren().add(gravityVector);
        System.out.println("Gravity Vector updated for " + box);
    }

    public static void calculateNormalVector(Box box) {
        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double massValue = Double.parseDouble(box.getTextField().getText());
        double positionX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionY = box.getRectangle().getY();
        double angle = box.rectangle.getRotate();
        double magnitude;

        if(angle == 0) {
            magnitude = massValue * gravityValue;
        }
        else {
            magnitude = (massValue * gravityValue) * Math.cos(Math.toRadians(angle));
        }
        normalMag = magnitude;

        VectorDisplay gravityVector = new VectorDisplay(positionX, positionY,
                magnitude, angle-90, "Normal", Color.RED);
        box.gravityVector = gravityVector;
        Sandbox.sandBoxPane.getChildren().add(gravityVector);
        System.out.println("Normal Vector updated for " + box);
    }

    public static void calculateFrictionVector(Box box) {
        double normal = normalMag;
        double coefficient = Double.parseDouble(Sandbox.coefficientField.getText());

        double positionX = box.getRectangle().getX();
        double positionY = box.getRectangle().getY() + box.getRectangle().getHeight()/2;

        double magnitude  = coefficient * normal;
        double angle = box.rectangle.getRotate() + 180;

        VectorDisplay frictionVector = new VectorDisplay(
                positionX, positionY, magnitude, angle, "Friction", Color.GREEN);
        box.frictionVector = frictionVector;
        Sandbox.sandBoxPane.getChildren().add(frictionVector);
        System.out.println("Friction Vector updated for " + box);
    }
}
