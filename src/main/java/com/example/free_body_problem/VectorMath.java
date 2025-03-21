package com.example.free_body_problem;

import javafx.scene.paint.Color;

public final class VectorMath {
    private Sandbox sandbox;

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

        System.out.println(angle);

        if(angle == 0) {
            magnitude = massValue * gravityValue;
        }
        else {
            magnitude = (massValue * gravityValue) * Math.cos(90+angle);
        }

        VectorDisplay gravityVector = new VectorDisplay(positionX, positionY,
                magnitude, angle-90, "Normal", Color.RED);
        box.gravityVector = gravityVector;
        Sandbox.sandBoxPane.getChildren().add(gravityVector);
        System.out.println("Normal Vector updated for " + box);
    }

}
