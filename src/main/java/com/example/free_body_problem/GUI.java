package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


public class GUI extends Application {
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Free Body Problem");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);

        //Main Menu
        VBox mainMenuRoot = new VBox();
        mainMenuRoot.setSpacing(75);
        mainMenuRoot.setPadding(new Insets(70, 100, 100, 150));
        mainMenuRoot.setId("menuID");



        Label nameLabel = new Label("Free Body Problem");
        nameLabel.getStyleClass().add("titleStyle");
        nameLabel.setEffect(new DropShadow());


        ImageView logoView = new ImageView(
                new Image(getClass().getResourceAsStream("/images/logoAlt.png")));
        logoView.setFitHeight(450);
        logoView.setFitWidth(450);
        logoView.setTranslateX(150);
        logoView.setTranslateY(-50);
        logoView.setEffect(new DropShadow());


        VBox buttonBox = new VBox();
        buttonBox.setSpacing(20);

        Button optionsBT = new Button("OPTIONS");
        optionsBT.getStyleClass().add("buttonStyle");
        optionsBT.setOnMouseEntered(e -> buttonHover(optionsBT));

        Button startBT = new Button("START");
        startBT.getStyleClass().add("buttonStyle");
        startBT.setOnMouseEntered(e -> buttonHover(startBT));

        Button creditsBT = new Button("CREDITS");
        creditsBT.getStyleClass().add("buttonStyle");
        creditsBT.setOnMouseEntered(e -> buttonHover(creditsBT));

        buttonBox.getChildren().addAll(optionsBT,startBT,creditsBT);




        HBox buttonsAndLogo = new HBox();
        buttonsAndLogo.setSpacing(30);
        buttonsAndLogo.getChildren().addAll(buttonBox, logoView);
        mainMenuRoot.getChildren().addAll(nameLabel, buttonsAndLogo);

        Scene mainMenuScene = new Scene(mainMenuRoot);
        mainMenuScene.getStylesheets().add("StyleSheet.css");


        primaryStage.setScene(mainMenuScene);
        primaryStage.show();

    }

    private void buttonHover(Button button) {
        button.getStyleClass().add("buttonHover");
    }
}