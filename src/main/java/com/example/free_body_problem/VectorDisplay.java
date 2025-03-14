package com.example.free_body_problem;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class VectorDisplay extends Pane {
    private final Line line;
    private final Polygon arrowhead;
    private final Rotate rotate;
    private double length;
    private final Text forceName;
    private final Text forceMagnitude;
    private VBox forceText;
    private double endX, endY;
    private Color color;
    private double angle;
    private final double MAX_LENGTH = 5;


    public VectorDisplay(double startX, double startY, double length, double angle, String name, double magnitude, Color color) {
        this.length = length;
        this.color = color;
        this.angle = angle;

        // Line for arrow shaft
        line = new Line(startX, startY, startX + length, startY);
        line.setStroke(color);
        line.setStrokeWidth(5);

        // Arrow head (Triangle)
        arrowhead = new Polygon();
        arrowhead.setFill(color);
        arrowhead.setStroke(color);
        arrowhead.setScaleX(2);
        arrowhead.setScaleY(2);



        //Force Name (Text)
        forceName = new Text(name);
        forceName.setFill(color);
        forceName.setFont(new Font(16));

        //Force Magnitude (Text)
        forceMagnitude = new Text(magnitude + " N");
        forceMagnitude.setFill(color);
        forceMagnitude.setFont(new Font(12));

        forceText = new VBox(forceName, forceMagnitude);
        updateVector();

        // Rotation transform (around the start of the arrow)
        rotate = new Rotate(angle, startX, startY);
        this.getTransforms().add(rotate);

        getChildren().addAll(line, arrowhead, forceText);

    }

    private void updateVector() {
        // Updating Arrowhead

        //Adding a little offset to the endX to remove clipping of the shaft in the arrow head
        endX = line.getEndX()+ 2;
        endY = line.getEndY();
        double arrowSize = 10;

        arrowhead.getPoints().setAll(
                endX, endY,
                endX - arrowSize, endY - arrowSize / 2,
                endX - arrowSize, endY + arrowSize / 2
        );

        // Updating text
        forceText.setLayoutX(endX);
        forceText.setLayoutY(endY+5);

        forceText.setRotate(0-angle);
    }


    public void setLength(double newLength) {
        length = newLength;
        line.setEndX(line.getStartX() + newLength);
        updateVector();
    }

    public void setRotation(double angle) {
        rotate.setAngle(angle);
        updateVector();
    }

    public double getLength() {
        return length;
    }
    public double getRotation() {
        return rotate.getAngle();
    }
}