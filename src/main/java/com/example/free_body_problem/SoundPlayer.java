// src/main/java/com/example/free_body_problem/old_Files/SoundPlayer.java
package com.example.free_body_problem;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class SoundPlayer {
    private static double volume = 1.0; // Default volume

    public void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + soundFilePath);
                return;
            }
            Media sound = new Media(soundFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(volume); // Set the volume
            mediaPlayer.setOnError(() -> System.err.println("Error playing sound: " + mediaPlayer.getError().getMessage()));
            mediaPlayer.play();
            System.out.println("Playing sound: " + soundFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setVolume(double newVolume) {
        volume = newVolume;
    }
}