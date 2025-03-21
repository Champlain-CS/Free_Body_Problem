package com.example.free_body_problem.old_Files;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class SoundPlayer {
    private static boolean isPlatformStarted = false;

    public void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + soundFilePath);
                return;
            }
            Media sound = new Media(soundFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnError(() -> System.err.println("Error playing sound: " + mediaPlayer.getError().getMessage()));
            mediaPlayer.play();
            System.out.println("Playing sound: " + soundFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (!isPlatformStarted) {
            Platform.startup(() -> {
                isPlatformStarted = true;
                SoundPlayer soundPlayer = new SoundPlayer();
                soundPlayer.playSound("target/classes/sounds/Place.wav");
            });
        } else {
            Platform.runLater(() -> {
                SoundPlayer soundPlayer = new SoundPlayer();
                soundPlayer.playSound("target/classes/sounds/Place.wav");
            });
        }
    }
}