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

public class VectorDisplay extends Pane {
    private final Line line;
    private final Polygon arrowhead;
    private final Rotate rotate;
    private double length;
    private double trueLength;
    private final Text forceName;
    private final Text forceMagnitude;
    private double endX, endY;
    private Color color;
    private double angle;

    // Logarithmic scaling constants
    private static final double MIN_VISIBLE_LENGTH = 15;
    private static final double MAX_VISIBLE_LENGTH = 200;
    private static final double SCALE_FACTOR = (MAX_VISIBLE_LENGTH - MIN_VISIBLE_LENGTH) / Math.log10(1000);

    public VBox forceText;
    private DecimalFormat df = new DecimalFormat("#.###");

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
        this.length = calculateVisualLength(trueLength);
        this.color = color;
        this.angle = angle;

        // Configure line
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(startX + this.length);
        line.setEndY(startY);
        line.setStroke(color);
        line.setStrokeWidth(3);

        // Configure arrowhead
        arrowhead.setFill(color);
        arrowhead.setStroke(color);
        arrowhead.setScaleX(1.2);
        arrowhead.setScaleY(1.2);

        // Configure text (show trueLength)
        forceName.setFill(color);
        forceName.setFont(new Font(16));
        forceName.setEffect(new DropShadow(4, Color.WHITE));

        forceMagnitude.setFill(color);
        forceMagnitude.setFont(new Font(12));
        forceMagnitude.setEffect(new DropShadow(3, Color.WHITE));
        forceMagnitude.setText(df.format(trueLength) + " N");

        // Configure rotation
        rotate.setAngle(angle);
        rotate.setPivotX(startX);
        rotate.setPivotY(startY);
        this.getTransforms().add(rotate);

        // Add components if length > 0
        if (this.length > 0) {
            updateVector();
            getChildren().addAll(line, arrowhead, forceText);
        } else {
            getChildren().clear();
        }
    }

    private void updateVector() {
        if (length <= 0) {
            getChildren().clear();
            return;
        }

        endX = line.getEndX() + 2;
        endY = line.getEndY();
        double arrowSize = 10;

        arrowhead.getPoints().setAll(
                endX, endY,
                endX - arrowSize, endY - arrowSize / 2,
                endX - arrowSize, endY + arrowSize / 2
        );

        forceText.setLayoutX(endX);
        forceText.setLayoutY(endY + 5);
        forceText.setRotate(0 - angle);
        forceMagnitude.setText(df.format(trueLength) + " N");
    }

    private double calculateVisualLength(double magnitude) {
        if (magnitude <= 0) return 0;

        double logValue = Math.log10(magnitude + 1); // +1 to avoid log(0)
        double visualLength = MIN_VISIBLE_LENGTH + (SCALE_FACTOR * logValue);

        return Math.min(visualLength, MAX_VISIBLE_LENGTH);
    }

    public void setLength(double newTrueLength) {
        this.trueLength = newTrueLength;
        this.length = calculateVisualLength(newTrueLength);

        if (newTrueLength == 0) {
            getChildren().clear();
            return;
        }
    }

    public void setRotation(double angle) {
        rotate.setAngle(angle);
        updateVector();
    }

    public Text getForceName() {
        return forceName;
    }

    public double getLength() {
        return length;  // Returns visual length
    }

    public double getTrueLength() {
        return trueLength;  // Returns original unaltered magnitude
    }

    public double getRotation() {
        return rotate.getAngle();
    }
}