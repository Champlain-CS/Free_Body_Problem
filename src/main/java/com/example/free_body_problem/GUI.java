package com.example.free_body_problem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class creates the GUI for the main menu, the options and credits screens.
 * <br> When the user launches the application, this class will run first.
 * <p>
 * Each section is labelled accordingly with inline comments
 */

public class GUI extends Application {
    VBox mainMenuRoot = new VBox();
    StackPane optionsRoot = new StackPane();
    StackPane creditsRoot = new StackPane();

    double xOffset, yOffset;

    // Screen resolution options
    private final List<String> resolutions = Arrays.asList(
            "800x600", "1024x768", "1280x720", "1366x768", "1600x900", "1920x1080"
    );

    // Current window settings
    private boolean isFullscreen = false;
    private String currentResolution = "1024x768"; // Default resolution

    // UI Elements that need to be accessible
    private ComboBox<String> resolutionComboBox;

    // Instantiate SoundPlayer
    private final SoundPlayer menuSoundPlayer = new SoundPlayer();
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
        closeBtn.setOnAction(_ -> primaryStage.close());
        titleBar.getChildren().addAll(title, spacer, closeBtn);

        primaryStage.setTitle("Free Body Problem");
        primaryStage.setResizable(false);

        // Load and play background music
        String musicFilePath = "src/main/resources/sounds/Music.wav";
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

        ImageView logoView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logoAlt.png"))));
        logoView.setFitHeight(350);
        logoView.setFitWidth(350);
        logoView.setTranslateX(150);
        logoView.setTranslateY(-50);
        logoView.setEffect(new DropShadow());

        // Menu buttons
        VBox buttonBox = new VBox();
        buttonBox.setSpacing(15);

        Button startBT = new Button("START");
        startBT.getStyleClass().add("buttonStyle");
        startBT.setOnMouseEntered(_ -> startBT.getStyleClass().add("buttonHover"));
        startBT.setOnMouseClicked(_ -> {
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu Buttons.wav");
            Sandbox app = new Sandbox();
            app.start(new Stage());
            primaryStage.close();
        });

        Button optionsBT = new Button("OPTIONS");
        optionsBT.getStyleClass().add("buttonStyle");
        optionsBT.setOnMouseEntered(_ -> optionsBT.getStyleClass().add("buttonHover"));
        optionsBT.setOnMouseClicked(_ -> {
            menuSoundPlayer.playSound("src/main/resources/sounds/Menu Buttons.wav");
            primaryStage.getScene().setRoot(optionsRoot);
        });

        Button creditsBT = new Button("CREDITS");
        creditsBT.getStyleClass().add("buttonStyle");
        creditsBT.setOnMouseEntered(_ -> creditsBT.getStyleClass().add("buttonHover"));
        creditsBT.setOnMouseClicked(_ -> {
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
        optionsVBox.setSpacing(40);
        optionsVBox.setAlignment(Pos.CENTER);
        optionsVBox.setTranslateY(-25);

        Label optionsLabel = new Label("OPTIONS");
        optionsLabel.getStyleClass().add("titleStyle");

        // Audio Options
        Label audioLabel = new Label("AUDIO");
        audioLabel.getStyleClass().add("header");

        Label musicVolumeLabel = new Label("Music Volume");
        musicVolumeLabel.getStyleClass().add("textStyle");
        Slider musicVolumeSlider = new Slider(0, 1, SoundPlayer.getVolume()); // Set slider to current volume
        musicVolumeSlider.getStyleClass().add("slider");
        musicVolumeSlider.valueProperty().addListener((_, _, newValue) -> {
            SoundPlayer.setVolume(newValue.doubleValue()); // Update volume
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setVolume(newValue.doubleValue());
            }
        });
        HBox musicVolumeBox = new HBox();
        musicVolumeBox.setSpacing(30);
        musicVolumeBox.setAlignment(Pos.CENTER);
        musicVolumeBox.getChildren().addAll(musicVolumeLabel, musicVolumeSlider);

        Label effectsVolumeLabel = new Label("Effect Volume");
        effectsVolumeLabel.getStyleClass().add("textStyle");
        Slider effectsVolumeSlider = new Slider(0, 1, SoundPlayer.getVolume()); // Set slider to current volume
        effectsVolumeSlider.getStyleClass().add("slider");
        effectsVolumeSlider.valueProperty().addListener((_, _, newValue) -> {
            SoundPlayer.setVolume(newValue.doubleValue()); // Update volume
        });
        HBox effectsVolumeBox = new HBox();
        effectsVolumeBox.setSpacing(30);
        effectsVolumeBox.setAlignment(Pos.CENTER);
        effectsVolumeBox.getChildren().addAll(effectsVolumeLabel, effectsVolumeSlider);

        // Display Options
        Label displayLabel = new Label("DISPLAY");
        displayLabel.getStyleClass().add("header");

        // Fullscreen option
        Label fullscreenLabel = new Label("Fullscreen");
        fullscreenLabel.getStyleClass().add("textStyle");
        CheckBox fullscreenCheckBox = new CheckBox();
        fullscreenCheckBox.setSelected(isFullscreen);
        fullscreenCheckBox.getStyleClass().add("check-box");
        fullscreenCheckBox.selectedProperty().addListener((_, _, newValue) -> {
            isFullscreen = newValue;

            // Apply fullscreen immediately
            primaryStage.setFullScreen(isFullscreen);

            // Enable/disable resolution dropdown based on fullscreen state
            resolutionComboBox.setDisable(isFullscreen);

            // If exiting fullscreen, apply the selected resolution
            if (!isFullscreen) {
                applyResolution(primaryStage, currentResolution);
            }
        });
        HBox fullscreenBox = new HBox();
        fullscreenBox.setSpacing(120);
        fullscreenBox.setAlignment(Pos.CENTER);
        fullscreenBox.getChildren().addAll(fullscreenLabel, fullscreenCheckBox);

        // Resolution dropdown
        Label resolutionLabel = new Label("Resolution");
        resolutionLabel.getStyleClass().add("textStyle");
        resolutionComboBox = new ComboBox<>(FXCollections.observableArrayList(resolutions));
        resolutionComboBox.setValue(currentResolution);
        resolutionComboBox.getStyleClass().add("combo-box");
        resolutionComboBox.setDisable(isFullscreen); // Disable if fullscreen is enabled
        resolutionComboBox.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                currentResolution = newValue;
                // Apply the new resolution immediately if not in fullscreen
                if (!isFullscreen) {
                    applyResolution(primaryStage, currentResolution);
                }
            }
        });
        HBox resolutionBox = new HBox();
        resolutionBox.setSpacing(30);
        resolutionBox.setAlignment(Pos.CENTER);
        resolutionBox.getChildren().addAll(resolutionLabel, resolutionComboBox);

        ImageView optionsX = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blueX.png"))));
        optionsX.setPreserveRatio(true);
        optionsX.setFitHeight(35);
        optionsX.setTranslateX(460);
        optionsX.setTranslateY(-210);
        optionsX.setOnMouseEntered(_ -> optionsX.setFitHeight(37));
        optionsX.setOnMouseExited(_ -> optionsX.setFitHeight(35));
        optionsX.setOnMouseClicked(_ -> primaryStage.getScene().setRoot(mainMenuRoot));

        // Group audio and display options
        VBox audioOptionsVBox = new VBox();
        audioOptionsVBox.setSpacing(15);
        audioOptionsVBox.setAlignment(Pos.CENTER);
        audioOptionsVBox.getChildren().addAll(audioLabel, musicVolumeBox, effectsVolumeBox);

        VBox displayOptionsVBox = new VBox();
        displayOptionsVBox.setSpacing(15);
        displayOptionsVBox.setAlignment(Pos.CENTER);
        displayOptionsVBox.getChildren().addAll(displayLabel, fullscreenBox, resolutionBox);

        optionsVBox.getChildren().addAll(optionsLabel, audioOptionsVBox, displayOptionsVBox);
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

        ImageView creditsX = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/blueX.png"))));
        creditsX.setPreserveRatio(true);
        creditsX.setFitHeight(35);
        creditsX.setTranslateX(460);
        creditsX.setTranslateY(-210);
        creditsX.setOnMouseEntered(_ -> creditsX.setFitHeight(37));
        creditsX.setOnMouseExited(_ -> creditsX.setFitHeight(35));
        creditsX.setOnMouseClicked(_ -> primaryStage.getScene().setRoot(mainMenuRoot));

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

        // Apply initial resolution
        applyResolution(primaryStage, currentResolution);
    }

    /**
     * Applies the specified resolution to the stage
     * @param stage The stage to apply the resolution to
     * @param resolution The resolution string in format "width x height"
     */
    private void applyResolution(Stage stage, String resolution) {
        if (resolution == null || resolution.isEmpty()) {
            return;
        }

        try {
            String[] dimensions = resolution.split("x");
            if (dimensions.length == 2) {
                double width = Double.parseDouble(dimensions[0]);
                double height = Double.parseDouble(dimensions[1]);

                // Ensure resolution doesn't exceed screen bounds
                double screenWidth = Screen.getPrimary().getBounds().getWidth();
                double screenHeight = Screen.getPrimary().getBounds().getHeight();

                width = Math.min(width, screenWidth);
                height = Math.min(height, screenHeight);

                // Apply the new window size
                stage.setWidth(width);
                stage.setHeight(height);

                // Center the window on screen
                stage.centerOnScreen();
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid resolution format: " + resolution);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}