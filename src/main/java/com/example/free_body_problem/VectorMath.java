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

        VectorDisplay gravityVector = new VectorDisplay(positionX, positionY,
                gravityValue * massValue, 90, "Gravity", Color.BLUE);
        box.gravityVector = gravityVector;
        Sandbox.sandBoxPane.getChildren().add(gravityVector);
    }

}
