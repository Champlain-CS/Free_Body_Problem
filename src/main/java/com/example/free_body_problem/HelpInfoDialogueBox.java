package com.example.free_body_problem;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HelpInfoDialogueBox extends Pane {

    HelpInfoDialogueBox() {
        Rectangle background = new Rectangle();
        background.setWidth(900);
        background.setHeight(600);
        background.setFill(Color.LIGHTBLUE);

        Label title = new Label();
        title.setText("Help Information");
        title.setTranslateX(100);
        title.setTranslateY(-20);

    }
}
