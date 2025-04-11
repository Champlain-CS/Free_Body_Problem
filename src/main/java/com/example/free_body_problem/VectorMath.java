package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point2D;

public final class VectorMath {
    private Sandbox sandbox;

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



        // For a box on an inclined plane, normal force is perpendicular to the plane
        double angle = box.rectangle.getRotate();
        double magnitude = massValue * gravityValue * Math.cos(Math.toRadians(angle));


        VectorDisplay normalVector = new VectorDisplay(newPositionX, newPositionY,
                magnitude, angle-90, "Normal", Color.RED);
        box.normalVector = normalVector;
        Sandbox.sandBoxPane.getChildren().add(normalVector);
        System.out.println("Normal Vector updated for " + box);


        // The normal force is perpendicular to the surface
        double normalAngleRad = Math.toRadians(angle);
        double normalXComponent = magnitude * Math.sin(normalAngleRad);
        double normalYComponent = magnitude * Math.cos(normalAngleRad);

        box.totalXForce += normalXComponent;
        box.totalYForce += normalYComponent;

        System.out.println("normal components: " + normalXComponent + " " + normalYComponent);


        // Components
        VectorDisplay normalX = new VectorDisplay(newPositionX, newPositionY, normalXComponent, 0, "Nx", Color.DARKRED);
        VectorDisplay normalY = new VectorDisplay(newPositionX, newPositionY, normalYComponent, 270, "Ny", Color.DARKRED);

        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(normalX),
                adaptComponentOrientation(normalY));
    }

    public static void calculateFrictionVector(Box box) {
        double coefficient = Double.parseDouble(Sandbox.coefficientField.getText());

        // Friction acts parallel to the inclined plane, opposing potential motion
        // For a box on an inclined plane, friction points up the plane
        double frictionAngle = box.rectangle.getRotate();
        if(frictionAngle < 180) {
            frictionAngle += 180;  // Point up the incline
        } else {
            frictionAngle -= 180;  // Point up the incline for angles > 180
        }

        double maxFriction = coefficient * box.normalVector.getTrueLength();
        double gravityAlongIncline =
                Math.abs(box.gravityVector.getTrueLength() * Math.sin(Math.toRadians(frictionAngle)));

        double magnitude;
        if (gravityAlongIncline >= maxFriction) {
            magnitude = maxFriction; // sliding
            box.isSliding = true;
        } else {
            magnitude = gravityAlongIncline; // static equilibrium
            box.isSliding = false;
        }

        System.out.println("friction magnitude: " + magnitude);


        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double rotationAngle = box.getRectangle().getRotate();

        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);

        // The vector's original position relative to the center (before rotation)
        double vectorX;
        if(box.rectangle.getRotate() < 180) {
            vectorX = box.getRectangle().getX();
            frictionAngle = box.rectangle.getRotate() + 180;
        }
        else {
            vectorX = box.getRectangle().getX() + box.getRectangle().getWidth();
            frictionAngle = box.rectangle.getRotate();
        }
        double vectorY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(rotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double newPositionX = rotatedVector.getX();
        double newPositionY = rotatedVector.getY();



        VectorDisplay frictionVector = new VectorDisplay(
                newPositionX, newPositionY, magnitude, frictionAngle, "Friction", Color.GREEN);
        box.frictionVector = frictionVector;
        Sandbox.sandBoxPane.getChildren().add(frictionVector);
        System.out.println("Friction Vector updated for " + box);


        // The friction force is parallel to the surface
        double frictionAngleRad = Math.toRadians(frictionAngle + 90); // Parallel to surface
        double frictionXComponent = magnitude * Math.sin(frictionAngleRad);
        double frictionYComponent = magnitude * Math.cos(frictionAngleRad);

        box.totalXForce += frictionXComponent;
        box.totalYForce += frictionYComponent;

        System.out.println("friction components: " + frictionXComponent + " " + frictionYComponent);

        // Components
        VectorDisplay frictionX = new VectorDisplay(newPositionX, newPositionY, frictionXComponent, 0, "Fx", Color.DARKGREEN);
        VectorDisplay frictionY = new VectorDisplay(newPositionX, newPositionY, frictionYComponent, 270, "Fy", Color.DARKGREEN);
        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(frictionX),
                adaptComponentOrientation(frictionY));
    }

    public static void calculateTension1Rope(Box box, Rope rope) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double boxRotationAngle = box.getRectangle().getRotate();
        double ropeRotationAngle = rope.getOrientation();
        System.out.println("box rotation " + boxRotationAngle + "  rope rotation " + ropeRotationAngle);


        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);

        // The vector's original position relative to the center (before rotation)
        double vectorX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double vectorY = box.getRectangle().getY();

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(boxRotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double newPositionX = rotatedVector.getX();
        double newPositionY = rotatedVector.getY();



        //Cases
        double magnitude = 0;
        double angle = 0;

        if(!box.snappedToPlane) {
            magnitude = box.gravityVector.getTrueLength();
            angle = 270;
        }
        if(box.isSliding && ropeRotationAngle >= boxRotationAngle && ropeRotationAngle <= 270) {
            double xTension = -1 * box.totalXForce;
            double yTension = -1 * box.totalYForce;
            magnitude = Math.sqrt(xTension * xTension + yTension * yTension);
            System.out.println("tension: " + magnitude);
            angle = ropeRotationAngle;
        }


        VectorDisplay tensionVector = new VectorDisplay(newPositionX, newPositionY,
                magnitude, angle, "Tension", Color.PURPLE);
        box.tensionVector1 = tensionVector;
        Sandbox.sandBoxPane.getChildren().add(tensionVector);

        box.totalYForce += magnitude; //always down

    }

    public static void calculateNetVector(Box box) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;

        double xComponent = box.totalXForce;
        double yComponent = box.totalYForce;

        System.out.println(xComponent + " " + yComponent);

        double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
        double boxAngle = box.getRectangle().getRotate()+90;
        double netAngle;

        //Angle calculation for all 4 cases
        double phi = Math.toDegrees(Math.atan(yComponent/xComponent))+180; //Angle between net vector and x component (used as reference)
        System.out.println("phi = " + phi);

        //Net angle adjustment
        if(0 <= boxAngle && boxAngle < 90) {
            netAngle = phi;
        }
        else if(90 <= boxAngle && boxAngle < 180) {
            netAngle = 180 - phi;
        }
        else if (180 <= boxAngle && boxAngle < 270) {
            netAngle = 180 + phi;
        }
        else {
            netAngle = 360 - phi;
        }

        VectorDisplay netVector = new VectorDisplay(positionCenterX, positionCenterY,
                magnitude, netAngle, "Net", Color.BLACK);
        box.gravityVector = netVector;
        Sandbox.sandBoxPane.getChildren().add(netVector);
        System.out.println("Net Vector updated for " + box + " at " + netAngle + "degrees");


        // Components
        VectorDisplay xComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                xComponent, 0, "NetX", Color.GRAY);
        VectorDisplay yComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                yComponent, 270, "NetY", Color.GRAY);
        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(xComponentVector),
                adaptComponentOrientation(yComponentVector));
    }


    private static VectorDisplay adaptComponentOrientation(VectorDisplay vector) {
        if (vector.getTrueLength() < 0) {
            // flip the sign of the true length while maintaining visual length
            vector.setDisplayLength(-1 * vector.getTrueLength());
            vector.setRotation((vector.getRotation() + 180) % 360);

            double originalRotation = vector.getRotation();
            double originalTextRotation = vector.forceText.getRotate();

            if (originalRotation % 360 >= 90 && originalRotation % 360 < 270) {
                // Only flip text if vector was pointing left
                vector.forceText.setRotate((originalTextRotation + 180) % 360);
            }
        }

        return vector;
    }
}
