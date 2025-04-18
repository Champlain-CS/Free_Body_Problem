package com.example.free_body_problem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageStyle;

import java.io.File;

public class GUI extends Application {
    VBox mainMenuRoot = new VBox();
    StackPane optionsRoot = new StackPane();
    StackPane creditsRoot = new StackPane();

    double xOffset, yOffset;

    // Instantiate SoundPlayer
    private SoundPlayer menuSoundPlayer = new SoundPlayer();
    public static MediaPlayer backgroundMusicPlayer;

    public void start(Stage primaryStage) {
        primaryStage.setMaxHeight(650);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        // Create a custom title bar
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("title-bar");
        titleBar.setMinWidth(Region.USE_PREF_SIZE);

        Label title = new Label("Free Body Problem");
        title.getStyleClass().add("title-text");

        // Spacer to push buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");

        // Add style classes
        closeBtn.getStyleClass().add("close-button");

        // Add event handlers
        closeBtn.setOnAction(e -> primaryStage.close());
        titleBar.getChildren().addAll(title, spacer, closeBtn);





        primaryStage.setTitle("Free Body Problem");
        primaryStage.setResizable(false);

        // Load and play background music
        String musicFilePath = "src/main/resources/sounds/FrenchVersion.wav";
        File musicFile = new File(musicFilePath);
        if (musicFile.exists()) {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
            }
            Media backgroundMusic = new Media(musicFile.toURI().toString());
            backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            backgroundMusicPlayer.setVolume(SoundPlayer.getVolume()); // Set initial volume
            backgroundMusicPlayer.play();
        } else {
            System.err.println("Background music file not found: " + musicFilePath);
        }

        // Main Menu
        mainMenuRoot.setId("menuID");
        titleBar.prefWidthProperty().bind(mainMenuRoot.widthProperty());

        // Make the window draggable
        mainMenuRoot.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        mainMenuRoot.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });


        Label nameLabel = new Label("Free Body Problem");
        nameLabel.getStyleClass().add("titleStyle");
        nameLabel.setEffect(new DropShadow());

        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/images/logoAlt.png")));
        logoView.setFitHeight(350);
        logoView.setFitWidth(350);
        logoView.setTranslateX(150);
        logoView.setTranslateY(-50);
        logoView.setEffect(new DropShadow());

        VBox buttonBox = new VBox();
        buttonBox.setSpacing(15);

        Button startBT = new Button("START");
        startBT.getStyleClass().add("buttonStyle");
        startBT.setOnMouseEntered(e -> startBT.getStyleClass().add("buttonHover"));
        startBT.setOnMouseClicked(e -> {
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu Buttons.wav");
            Sandbox app = new Sandbox();
            app.start(new Stage());
            primaryStage.close();
        });

        Button optionsBT = new Button("OPTIONS");
        optionsBT.getStyleClass().add("buttonStyle");
        optionsBT.setOnMouseEntered(e -> optionsBT.getStyleClass().add("buttonHover"));
        optionsBT.setOnMouseClicked(e -> {
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu Buttons.wav");
            primaryStage.getScene().setRoot(optionsRoot);
        });

        Button creditsBT = new Button("CREDITS");
        creditsBT.getStyleClass().add("buttonStyle");
        creditsBT.setOnMouseEntered(e -> creditsBT.getStyleClass().add("buttonHover"));
        creditsBT.setOnMouseClicked(e -> {
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu Buttons.wav");
            primaryStage.getScene().setRoot(creditsRoot);
            creditsRoot.requestFocus();
        });

        buttonBox.getChildren().addAll(startBT, optionsBT, creditsBT);

        HBox buttonsAndLogo = new HBox();
        buttonsAndLogo.setSpacing(30);
        buttonsAndLogo.getChildren().addAll(buttonBox, logoView);

        VBox mainMenuContent = new VBox();
        mainMenuContent.getChildren().addAll(nameLabel, buttonsAndLogo);
        mainMenuContent.setPadding(new Insets(70, 100, 100, 120));
        mainMenuContent.setSpacing(60);

        mainMenuRoot.getChildren().addAll(titleBar, mainMenuContent);
        mainMenuRoot.getStylesheets().add("MainMenuStyleSheet.css");



        // Options Menu
        optionsRoot.setId("root");

        // Make the window draggable
        optionsRoot.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        optionsRoot.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });


        Rectangle optionsBox = new Rectangle();
        optionsBox.setHeight(500);
        optionsBox.setWidth(1000);
        optionsBox.getStyleClass().add("roundBox");

        VBox optionsVBox = new VBox();
        optionsVBox.setSpacing(50);
        optionsVBox.setAlignment(Pos.CENTER);
        optionsVBox.setTranslateY(-50);

        Label optionsLabel = new Label("OPTIONS");
        optionsLabel.getStyleClass().add("titleStyle");

        Label musicVolumeLabel = new Label("Music Volume");
        musicVolumeLabel.getStyleClass().add("textStyle");
        Slider musicVolumeSlider = new Slider(0, 1, SoundPlayer.getVolume()); // Set slider to current volume
        musicVolumeSlider.getStyleClass().add("slider");
        musicVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SoundPlayer.setVolume(newValue.doubleValue()); // Update volume
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setVolume(newValue.doubleValue());
            }
        });
        HBox musicVolumeBox = new HBox();
        musicVolumeBox.setSpacing(30);
        musicVolumeBox.setAlignment(Pos.TOP_CENTER);
        musicVolumeBox.getChildren().addAll(musicVolumeLabel, musicVolumeSlider);

        Label effectsVolumeLabel = new Label("Effect Volume");
        effectsVolumeLabel.getStyleClass().add("textStyle");
        Slider effectsVolumeSlider = new Slider(0, 1, SoundPlayer.getVolume()); // Set slider to current volume
        effectsVolumeSlider.getStyleClass().add("slider");
        effectsVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            SoundPlayer.setVolume(newValue.doubleValue()); // Update volume
        });
        HBox effectsVolumeBox = new HBox();
        effectsVolumeBox.setSpacing(30);
        effectsVolumeBox.setAlignment(Pos.CENTER);
        effectsVolumeBox.getChildren().addAll(effectsVolumeLabel, effectsVolumeSlider);

        ImageView optionsX = new ImageView(new Image(getClass().getResourceAsStream("/images/blueX.png")));
        optionsX.setPreserveRatio(true);
        optionsX.setFitHeight(35);
        optionsX.setTranslateX(460);
        optionsX.setTranslateY(-210);
        optionsX.setOnMouseEntered(e -> optionsX.setFitHeight(37));
        optionsX.setOnMouseExited(e -> optionsX.setFitHeight(35));
        optionsX.setOnMouseClicked(e -> primaryStage.getScene().setRoot(mainMenuRoot));

        optionsVBox.getChildren().addAll(optionsLabel, musicVolumeBox, effectsVolumeBox);
        optionsRoot.getChildren().addAll(optionsBox, optionsVBox, optionsX);
        optionsRoot.getStylesheets().add("OptionsAndCreditsStyleSheet.css");

        optionsRoot.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.getScene().setRoot(mainMenuRoot);
            }
        });




        // Credits
        creditsRoot.setId("root");

        // Make the window draggable
        creditsRoot.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        creditsRoot.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });

        Rectangle creditsBase = new Rectangle();
        creditsBase.setHeight(500);
        creditsBase.setWidth(1000);
        creditsBase.getStyleClass().add("roundBox");

        Label matthew = new Label("MATTHEW CHEUNG");
        matthew.getStyleClass().add("textStyle");
        Label justin = new Label("JUSTIN DAVIGNON");
        justin.getStyleClass().add("textStyle");
        Label chris = new Label("CHRISTOPHER LABERGE");
        chris.getStyleClass().add("textStyle");

        VBox names = new VBox();
        names.setSpacing(40);
        names.setAlignment(Pos.CENTER);
        names.getChildren().addAll(matthew, justin, chris);

        Label creditsLabel = new Label("CREDITS");
        creditsLabel.getStyleClass().add("titleStyle");

        ImageView creditsX = new ImageView(new Image(getClass().getResourceAsStream("/images/blueX.png")));
        creditsX.setPreserveRatio(true);
        creditsX.setFitHeight(35);
        creditsX.setTranslateX(460);
        creditsX.setTranslateY(-210);
        creditsX.setOnMouseEntered(e -> creditsX.setFitHeight(37));
        creditsX.setOnMouseExited(e -> creditsX.setFitHeight(35));
        creditsX.setOnMouseClicked(e -> primaryStage.getScene().setRoot(mainMenuRoot));

        VBox creditsBox = new VBox();
        creditsBox.setSpacing(80);
        creditsBox.setAlignment(Pos.CENTER);
        creditsBox.getChildren().addAll(creditsLabel, names);
        creditsRoot.getChildren().addAll(creditsBase, creditsBox, creditsX);
        creditsRoot.getStylesheets().add("OptionsAndCreditsStyleSheet.css");

        creditsRoot.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                primaryStage.getScene().setRoot(mainMenuRoot);
            }
        });

        // Display
        Scene projectScene = new Scene(mainMenuRoot);
        primaryStage.setScene(projectScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}