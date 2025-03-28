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
    private final Text forceName;
    private final Text forceMagnitude;
    private VBox forceText;
    private double endX, endY;
    private Color color;
    private double angle;
    private final double MAX_LENGTH = 100;

    private DecimalFormat df = new DecimalFormat("#.####");


    public VectorDisplay(double startX, double startY, double length, double angle, String name, Color color) {
        this.length = length;
        if(length>MAX_LENGTH)
            this.length = MAX_LENGTH;

        this.color = color;
        this.angle = angle;


        // Line for arrow shaft
        line = new Line(startX, startY, startX + this.length, startY);
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
        forceName.setEffect(new DropShadow(4, color.WHITE));

        //Force Magnitude (Text)
        forceMagnitude = new Text(df.format(length) + " N");
        forceMagnitude.setFill(color);
        forceMagnitude.setFont(new Font(12));
        forceMagnitude.setEffect(new DropShadow(3, color.WHITE));

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
        if(newLength > MAX_LENGTH)
            length = MAX_LENGTH;

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