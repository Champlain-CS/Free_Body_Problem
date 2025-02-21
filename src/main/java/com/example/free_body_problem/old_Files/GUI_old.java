package com.example.free_body_problem.old_Files;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class GUI_old extends Application {
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Free Body Problem");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);

        //Main Menu
        BorderPane mainMenuRoot = new BorderPane();
        mainMenuRoot.setPadding(new Insets(75, 10, 75, 10));

        Scene mainMenuScene = new Scene(mainMenuRoot);
        mainMenuScene.getStylesheets().add("MainMenuStyleSheet.css");
        mainMenuRoot.setId("menuID");


        Label nameLabel = new Label("Free Body Problem");
        nameLabel.getStyleClass().add("titleStyle");
        nameLabel.setEffect(new DropShadow());
        StackPane labelPane = new StackPane(nameLabel);
        mainMenuRoot.setTop(labelPane);


        ImageView logoView = new ImageView(
                new Image(getClass().getResourceAsStream("/images/logoAlt.png")));
        logoView.setFitHeight(350);
        logoView.setFitWidth(350);
        logoView.setEffect(new DropShadow());
        logoView.setTranslateY(-10);
        mainMenuRoot.setCenter(logoView);


        HBox buttonBox = new HBox();
        buttonBox.setSpacing(100);
        buttonBox.setAlignment(Pos.CENTER);

        Button optionsBT = new Button("OPTIONS");
        optionsBT.getStyleClass().add("buttonStyle");
        optionsBT.setOnMouseEntered(e -> optionsBT.getStyleClass().add("buttonHover"));

        Button startBT = new Button("START");
        startBT.getStyleClass().add("buttonStyle");
        startBT.setOnMouseEntered(e -> startBT.getStyleClass().add("buttonHover"));

        Button creditsBT = new Button("CREDITS");
        creditsBT.getStyleClass().add("buttonStyle");
        creditsBT.setOnMouseEntered(e -> {creditsBT.getStyleClass().add("buttonHover");});


        buttonBox.getChildren().addAll(optionsBT,startBT,creditsBT);
        mainMenuRoot.setBottom(buttonBox);


        primaryStage.setScene(mainMenuScene);
        primaryStage.show();

    }
}