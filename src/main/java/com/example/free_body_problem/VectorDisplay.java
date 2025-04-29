package com.example.free_body_problem;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.text.DecimalFormat;

/**
 * This class allows for the creation, modification, and use of the vector objects.
 * <p>
 * Note that each vector has a true length corresponding to its actual physical magnitude and
 * a visual length, logarithmically scaled and with upper and lower bounds for viewing coherence.
 */

public class VectorDisplay extends Pane {
    final Line line;
    final Polygon arrowhead;
    private final Rotate rotate;
    private double displayLength;
    private double trueLength;
    private final Text forceName;
    private final Text forceMagnitude;
    private final double angle;

    // Logarithmic scaling constants
    private static final double MIN_VISIBLE_LENGTH = 15;
    private static final double MAX_VISIBLE_LENGTH = 150;
    private static final double SCALE_FACTOR = 0.3;
    private static final double STRETCH_FACTOR = 10000;

    public VBox forceText;
    private final DecimalFormat df = new DecimalFormat("#.###");

    public VectorDisplay(double startX, double startY, double trueLength, double angle, String name, Color color) {
        // Initialize fields first
        this.line = new Line();
        this.arrowhead = new Polygon();
        this.rotate = new Rotate();
        this.forceName = new Text(name);
        this.forceMagnitude = new Text();
        this.forceText = new VBox(forceName, forceMagnitude);

        // Set true length and calculate visual length
        this.trueLength = trueLength;  // Fixed parameter name (was using wrong variable)
        this.displayLength = calculateVisualLength(trueLength);
        this.angle = angle;

        // Configure line
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(startX + this.displayLength);
        line.setEndY(startY);
        line.setStroke(color);
        line.setStrokeWidth(3);

        // Configure arrowhead
        arrowhead.setFill(color);
        arrowhead.setStroke(color);
        arrowhead.setScaleX(1.2);
        arrowhead.setScaleY(1.2);

        forceName.setFill(Color.WHITE);  // Keep white text
        forceName.setFont(new Font(16));  // Original font size
        forceName.setEffect(new DropShadow(4, color));  // Vector-colored shadow instead of white

        forceMagnitude.setFill(Color.WHITE);  // Keep white text
        forceMagnitude.setFont(new Font(12));  // Original font size
        forceMagnitude.setEffect(new DropShadow(3, color));  // Vector-colored shadow
        forceMagnitude.setText(df.format(trueLength) + " N");

        // Configure the VBox container
        forceText.setStyle(
                "-fx-background-color: rgba(0,0,0,0.1);" +  // Semi-transparent dark background
                        "-fx-background-radius: 2;" +               // Rounded corners
                        "-fx-padding: 2px 4px;"                     // Internal padding
        );


        // Configure rotation
        rotate.setAngle(angle);
        rotate.setPivotX(startX);
        rotate.setPivotY(startY);
        this.getTransforms().add(rotate);

        updateVector();
        getChildren().addAll(line, arrowhead, forceText);

        // If the length is zero or very small, hide the line and arrowhead but keep the text
        if (Math.abs(this.trueLength) < 0.001) {
            line.setOpacity(0);
            arrowhead.setOpacity(0);
            forceText.setOpacity(1);
            System.out.println("hiding " + this.forceName.getText() + " arrow");
        }
    }

    private void updateVector() {

        double endX = line.getEndX() + 2;
        double endY = line.getEndY();
        double arrowSize = 10;

        arrowhead.getPoints().setAll(
                endX, endY,
                endX - arrowSize, endY - arrowSize / 2,
                endX - arrowSize, endY + arrowSize / 2
        );

        forceText.setLayoutX(endX);
        forceText.setLayoutY(endY + 5);
        forceText.setRotate(0 - rotate.getAngle()); // counter-rotate text to keep it upright
        forceMagnitude.setText(df.format(trueLength) + " N");
    }

    private double calculateVisualLength(double magnitude) {
        // Take absolute value for length calculation
        double absMagnitude = Math.abs(magnitude);

        if (absMagnitude <= 0.001)
            return 0;

        // Logarithmic scaling
        double scaledLength = MIN_VISIBLE_LENGTH +
                (MAX_VISIBLE_LENGTH - MIN_VISIBLE_LENGTH) *
                        Math.log10(1 + SCALE_FACTOR * absMagnitude) /
                        Math.log10(1 + SCALE_FACTOR * STRETCH_FACTOR);

        return Math.min(scaledLength, MAX_VISIBLE_LENGTH);
    }

    public void setDisplayLength(double newTrueLength) {
        this.trueLength = newTrueLength;
        this.displayLength = calculateVisualLength(newTrueLength);

        // Update visibility of components based on magnitude
        if (Math.abs(newTrueLength) < 0.001) {
            line.setOpacity(0);
            arrowhead.setOpacity(0);
            forceText.setOpacity(1);
        } else {
            line.setOpacity(1);
            arrowhead.setOpacity(1);
            forceText.setOpacity(1);
        }

        updateVector();
    }

    public void setRotation(double angle) {
        rotate.setAngle(angle);
        // Counter-rotate the text by the exact same amount
        forceText.setRotate(-rotate.getAngle());
        updateVector();
    }

    public Text getForceName() {
        return forceName;
    }

    public double getTrueLength() {
        return trueLength;  // Returns original unaltered magnitude
    }

    public double getRotation() {
        return rotate.getAngle();
    }
}