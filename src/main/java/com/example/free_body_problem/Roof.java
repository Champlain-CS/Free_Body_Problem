package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.geometry.Point2D;

public class Roof extends PhysicsObject {

    private Rectangle roof;
    private double width;
    private double height;
    private Color color;
    private boolean useMetalTexture = true;

    public Roof(double x, double y, double width, double height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;

        // Create the roof rectangle
        roof = new Rectangle(x, y, width, height);
        // Add the rectangle to this PhysicsObject
        getChildren().add(roof);

        // Apply metal texture
        applyMetalTexture();

        roof.setStroke(Color.BLACK);
        roof.setStrokeWidth(2);

        // Set userData to reference this object for event handling
        roof.setUserData(this);
    }

    private void applyMetalTexture() {
        if (useMetalTexture) {
            // Create a metallic gradient effect
            Color baseColor = color;
            Color lighterColor = baseColor.brighter();
            Color darkerColor = baseColor.darker();

            // Create a gradient with multiple stops to simulate metal panels
            Stop[] stops = new Stop[] {
                    new Stop(0.0, lighterColor),
                    new Stop(0.3, baseColor),
                    new Stop(0.5, darkerColor),
                    new Stop(0.7, baseColor),
                    new Stop(1.0, lighterColor)
            };

            LinearGradient metalGradient = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.REPEAT, stops
            );

            roof.setFill(metalGradient);

            // Add lighting effect to simulate shine and texture
            Lighting lighting = new Lighting();
            Light.Distant light = new Light.Distant();
            light.setAzimuth(-135.0);
            light.setElevation(30.0);
            lighting.setLight(light);
            lighting.setSurfaceScale(5.0);
            lighting.setSpecularConstant(1.0);
            lighting.setSpecularExponent(20.0);
            lighting.setDiffuseConstant(1.0);

            // Add inner shadow for depth
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setRadius(5);
            innerShadow.setChoke(0.2);
            innerShadow.setOffsetX(1);
            innerShadow.setOffsetY(1);
            innerShadow.setColor(Color.rgb(0, 0, 0, 0.3));

            // Combine effects
            lighting.setContentInput(innerShadow);
            roof.setEffect(lighting);
        } else {
            // Default behavior if not using metal texture
            roof.setFill(color);
        }
    }

    public Shape getShape() {
        return roof;
    }

    public Rectangle getRoof() {
        return roof;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setWidth(double width) {
        this.width = width;
        roof.setWidth(width);
        // Reapply texture when size changes
        applyMetalTexture();
    }

    public void setHeight(double height) {
        this.height = height;
        roof.setHeight(height);
        // Reapply texture when size changes
        applyMetalTexture();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        applyMetalTexture();
    }

    public void setMetalTextureEnabled(boolean enabled) {
        this.useMetalTexture = enabled;
        applyMetalTexture();
    }

    public void setPosition(double x, double y) {
        roof.setX(x);
        roof.setY(y);
    }

    public double getX() {
        return roof.getX();
    }

    public double getY() {
        return roof.getY();
    }

    // Add drag functionality to the roof
    public void addDragListener() {
        roof.setOnMousePressed(event -> {
            event.consume();
        });

        // The roof shouldn't be draggable in this implementation
        // But we can add specific drag functionality later if needed
    }

    // Method to update the size of the roof to match the sandbox width
    public void adjustToSandboxWidth(double sandboxWidth) {
        setWidth(sandboxWidth);
    }

    @Override
    public double getCenterX() {
        return roof.getX() + width / 2;
    }

    @Override
    public double getCenterY() {
        return roof.getY() + height / 2;
    }
}