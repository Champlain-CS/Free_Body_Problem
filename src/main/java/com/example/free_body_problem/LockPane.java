package com.example.free_body_problem;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class LockPane extends Pane {
    private Rectangle lockPane;

    LockPane(double width, double height) {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.setStyle("-fx-background-color: gray; -fx-opacity: 0");

        ImageView padlock = new ImageView(new Image(getClass().getResourceAsStream("/images/lock.png")));
        padlock.setPreserveRatio(true);
        padlock.setFitWidth(50);
        padlock.setTranslateY(10);

        getChildren().addAll(rectangle, padlock);
    }
}
