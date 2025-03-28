package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point2D;

public final class VectorMath {
    private Sandbox sandbox;
    private static double normalMag;

    public VectorMath(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    public static void calculateGravityVector(Box box) {
        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double massValue = Double.parseDouble(box.getTextField().getText());

        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double rotationAngle = box.getRectangle().getRotate();

        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);

        // The vector's original position relative to the center (before rotation)
        double vectorX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double vectorY = box.getRectangle().getY() + box.getRectangle().getHeight();

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(rotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double newPositionX = rotatedVector.getX();
        double newPositionY = rotatedVector.getY();

        double magnitude = massValue*gravityValue;

        VectorDisplay gravityVector = new VectorDisplay(newPositionX, newPositionY,
                magnitude, 90, "Gravity", Color.BLUE);
        box.gravityVector = gravityVector;
        Sandbox.sandBoxPane.getChildren().add(gravityVector);
        System.out.println("Gravity Vector updated for " + box);

        box.totalYForce -= magnitude; //always down
    }

    public static void calculateNormalVector(Box box) {
        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double massValue = Double.parseDouble(box.getTextField().getText());
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double rotationAngle = box.getRectangle().getRotate();

        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);

        // The vector's original position relative to the center (before rotation)
        double vectorX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double vectorY = box.getRectangle().getY();

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(rotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double newPositionX = rotatedVector.getX();
        double newPositionY = rotatedVector.getY();


        double angle = box.rectangle.getRotate();
        double magnitude;

        if(angle == 0) {
            magnitude = massValue * gravityValue;
        }
        else {
            magnitude = (massValue * gravityValue) * Math.cos(Math.toRadians(angle));
        }
        normalMag = magnitude;

        VectorDisplay normalVector = new VectorDisplay(newPositionX, newPositionY,
                magnitude, angle-90, "Normal", Color.RED);
        box.normalVector = normalVector;
        Sandbox.sandBoxPane.getChildren().add(normalVector);
        System.out.println("Normal Vector updated for " + box);

        box.totalYForce += magnitude * Math.cos(Math.toRadians(angle));

        if(angle <= 90)
            box.totalXForce += magnitude * Math.sin(Math.toRadians(angle));
        else
            box.totalXForce -= magnitude * Math.sin(Math.toRadians(angle));
    }

    public static void calculateFrictionVector(Box box) {
        double coefficient = Double.parseDouble(Sandbox.coefficientField.getText());
        double magnitude  = coefficient * normalMag;
        double angle;

        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double rotationAngle = box.getRectangle().getRotate();


        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);
        double rawBoxAngle = box.rectangle.getRotate();

        // The vector's original position relative to the center (before rotation)
        double vectorX;
        if(box.rectangle.getRotate() < 180) {
            vectorX = box.getRectangle().getX();
            angle = box.rectangle.getRotate() + 180;
        }
        else {
            vectorX = box.getRectangle().getX() + box.getRectangle().getWidth();
            angle = box.rectangle.getRotate();
        }
        double vectorY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(rotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double newPositionX = rotatedVector.getX();
        double newPositionY = rotatedVector.getY();



        VectorDisplay frictionVector = new VectorDisplay(
                newPositionX, newPositionY, magnitude, angle, "Friction", Color.GREEN);
        box.frictionVector = frictionVector;
        Sandbox.sandBoxPane.getChildren().add(frictionVector);
        System.out.println("Friction Vector updated for " + box);

        //Sending component magnitudes for net vector
        box.totalYForce += magnitude * Math.sin(Math.toRadians(angle));

        if(rawBoxAngle <= 90)
            box.totalXForce -= magnitude * Math.cos(Math.toRadians(angle));
        else
            box.totalXForce += magnitude * Math.cos(Math.toRadians(angle));
    }

    public static void calculateNetVector(Box box) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;

        double xComponent = box.totalXForce;
        double yComponent = box.totalYForce;

        double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
        double boxAngle = box.getRectangle().getRotate();
        double netAngle;

        //Angle calculation for all 4 cases
        double phi = Math.toDegrees(Math.atan(yComponent/xComponent))-90; //Angle between net vector and x component (used as reference)

        if(boxAngle <= 90) {
            netAngle = phi;
        }
        else if(boxAngle <= 180) {
            netAngle = 90 + phi;
        }
        else if (boxAngle <= 270) {
            netAngle = 180 + phi;
        }
        else {
            netAngle = 360 - phi;
        }

        VectorDisplay xComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                xComponent, 270, "x", Color.GRAY);
        VectorDisplay yComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                yComponent, 0, "y", Color.GRAY);
        Sandbox.sandBoxPane.getChildren().add(xComponentVector);
        Sandbox.sandBoxPane.getChildren().add(yComponentVector);


        VectorDisplay netVector = new VectorDisplay(positionCenterX, positionCenterY,
                magnitude, netAngle, "Net", Color.BLACK);
        box.gravityVector = netVector;
        Sandbox.sandBoxPane.getChildren().add(netVector);
        System.out.println("Net Vector updated for " + box + " at " + netAngle + "degrees");


    }
}
