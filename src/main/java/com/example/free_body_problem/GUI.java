package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUI extends Application {
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Free Body Problem");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);

        //Main Menu
        BorderPane mainMenuRoot = new BorderPane();
        mainMenuRoot.setPadding(new Insets(75, 10, 75, 10));

        Scene mainMenUScene = new Scene(mainMenuRoot);
        mainMenUScene.getStylesheets().add("StyleSheet.css");
        mainMenuRoot.setId("menuID");


        Label nameLabel = new Label("Free Body Problem");
        nameLabel.getStyleClass().add("titleStyle");
        StackPane labelPane = new StackPane(nameLabel);
        mainMenuRoot.setTop(labelPane);

        Circle logo = new Circle();
        logo.setRadius(100);
        mainMenuRoot.setCenter(logo);


        HBox buttonBox = new HBox();
        buttonBox.setSpacing(100);
        buttonBox.setAlignment(Pos.CENTER);

        Button optionsBT = new Button("OPTIONS");
        optionsBT.getStyleClass().add("buttonStyle");

        Button startBT = new Button("START");
        startBT.getStyleClass().add("buttonStyle");

        Button creditsBT = new Button("CREDITS");
        creditsBT.getStyleClass().add("buttonStyle");

        buttonBox.getChildren().addAll(optionsBT,startBT,creditsBT);
        mainMenuRoot.setBottom(buttonBox);


        primaryStage.setScene(mainMenUScene);
        primaryStage.show();

    }
}