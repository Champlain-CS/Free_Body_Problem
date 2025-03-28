package com.example.free_body_problem;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HelpInfoDialogueBox extends Pane {
    private Rectangle background;
    private Label title;
    private TextField text;

    HelpInfoDialogueBox() {
        background = new Rectangle();
        background.setWidth(900);
        background.setHeight(600);
        background.setFill(Color.LIGHTBLUE);

        title = new Label();
        title.setText("Help Information");
        title.setTranslateX(100);
        title.setTranslateY(-20);

        text = new TextField();
    }
}
