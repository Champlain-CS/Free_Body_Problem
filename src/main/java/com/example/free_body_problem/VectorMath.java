package com.example.free_body_problem;

import javafx.scene.paint.Color;

public class VectorMath {
    private Sandbox sandbox;

    public VectorMath(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    public void addGravityVector() {
        double gravityValue = Double.parseDouble(sandbox.gravityField.getText());
        //double massValue = Double.parseDouble(sandbox.text.getText());
        double GravityX = 0;
        double GravityY = 0;
        VectorDisplay gravityVector = new VectorDisplay(50, 50, gravityValue * 10, 90, "Gravity", Color.BLUE);
    }
}
