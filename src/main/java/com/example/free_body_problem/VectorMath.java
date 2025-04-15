package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point2D;

import java.util.Map;

public final class VectorMath {
    private Sandbox sandbox;

    public VectorMath(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    public static void calculateGravityVector(Box box) {
        System.out.println("Box at "+box.rectangle.getRotate()+" degrees");
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
        double xTension = 0;
        double yTension = 0;

        if(!box.snappedToPlane) {
            magnitude = box.gravityVector.getTrueLength();
            angle = 270;

            xTension = 0;
            yTension = magnitude;
        }
        if(box.isSliding && isRopeHigherThanBox(box, rope)) {
            xTension = -1 * box.totalXForce;
            yTension = -1 * box.totalYForce;
            magnitude = Math.sqrt(xTension * xTension + yTension * yTension);
            System.out.println("tension: " + magnitude);
            angle = ropeRotationAngle;
        }


        VectorDisplay tensionVector = new VectorDisplay(newPositionX, newPositionY,
                magnitude, angle, "Tension", Color.DARKVIOLET);
        box.tensionVector1 = tensionVector;
        Sandbox.sandBoxPane.getChildren().add(tensionVector);

        box.totalXForce = 0; //Static equilibrium necessarily
        box.totalYForce = 0;


        VectorDisplay tensionX = new VectorDisplay(
                newPositionX, newPositionY, xTension, 0, "Tx", Color.INDIGO);
        VectorDisplay tensionY = new VectorDisplay(
                newPositionX, newPositionY, yTension, 270, "Ty", Color.INDIGO);

        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(tensionX), adaptComponentOrientation(tensionY));

    }

    public static void calculateTension2Ropes(Box box, Rope rightRope, Rope leftRope) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double boxRotationAngle = box.getRectangle().getRotate();
        // Putting orientations to "normal" math angles
        double leftRopeRotationAngle = normalizeAngle(360 - leftRope.getOrientation());
        double rightRopeRotationAngle = normalizeAngle(360 - rightRope.getOrientation());
        System.out.println("box rotation " + boxRotationAngle +
                "  left rope rotation " + leftRopeRotationAngle + " right rope rotation " + rightRopeRotationAngle);


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
        double leftMagnitude = 0;
        double rightMagnitude = 0;
        double angle = 0;
        double leftXTension = 0;
        double leftYTension = 0;
        double rightXTension = 0;
        double rightYTension = 0;
        boolean leftRopeStartHigher = leftRope.getLine().getStartY() > leftRope.getLine().getEndY();
        boolean rightRopeStartHigher = rightRope.getLine().getStartY() > rightRope.getLine().getEndY();

        if(box.isSliding) {


        }



        if(!box.snappedToPlane) {
            double leftAngleAdapted = Math.toRadians(180 - leftRopeRotationAngle);
            double rightAngleAdapted = Math.toRadians(rightRopeRotationAngle);

            rightMagnitude = box.gravityVector.getTrueLength() /
                    ((Math.cos(rightAngleAdapted)/Math.cos(leftAngleAdapted)) + Math.sin(rightAngleAdapted));
            leftMagnitude = (rightMagnitude * Math.cos(rightAngleAdapted)) / Math.cos(leftAngleAdapted);

            leftXTension = leftMagnitude * Math.cos(leftRopeRotationAngle);
            leftYTension = leftMagnitude * Math.sin(leftRopeRotationAngle);
            rightXTension = rightMagnitude * Math.cos(rightRopeRotationAngle);
            rightYTension = rightMagnitude * Math.sin(rightRopeRotationAngle);

            VectorDisplay leftTensionVector = new VectorDisplay(newPositionX, newPositionY,
                    leftMagnitude, normalizeAngle(360 - leftRopeRotationAngle), "T1", Color.DARKVIOLET);
            box.tensionVector1 = leftTensionVector;
            Sandbox.sandBoxPane.getChildren().add(leftTensionVector);

            VectorDisplay rightTensionVector = new VectorDisplay(newPositionX, newPositionY,
                    rightMagnitude, normalizeAngle(360 - rightRopeRotationAngle), "T2", Color.DARKVIOLET);
            box.tensionVector2 = rightTensionVector;

            box.totalXForce = 0; //Static equilibrium necessarily
            box.totalYForce = 0;


            VectorDisplay leftTensionX = new VectorDisplay(
                    newPositionX - 2, newPositionY, leftXTension, 0, "T1x", Color.INDIGO);
            VectorDisplay leftTensionY = new VectorDisplay(
                    newPositionX, newPositionY, leftYTension, 270, "T1y", Color.INDIGO);

            VectorDisplay rightTensionX = new VectorDisplay(
                    newPositionX + 2, newPositionY, rightXTension, 0, "T2x", Color.INDIGO);
            VectorDisplay rightTensionY = new VectorDisplay(
                    newPositionX, newPositionY, rightYTension, 270, "T2y", Color.INDIGO);


            Sandbox.sandBoxPane.getChildren().addAll(
                    adaptComponentOrientation(leftTensionX), adaptComponentOrientation(leftTensionY));
            Sandbox.sandBoxPane.getChildren().addAll(
                    adaptComponentOrientation(rightTensionX), adaptComponentOrientation(rightTensionY));


        }
    }

    public static void calculatePulleyTension(Box box1, Box box2, Pulley connectionPulley){

        double position1X = tensionVectorPositions(box1)[0];
        double position1Y = tensionVectorPositions(box1)[1];
        double position2X = tensionVectorPositions(box2)[0];
        double position2Y = tensionVectorPositions(box2)[1];

        Map.Entry<Rope, Boolean> hashMap1 = box1.connectedRopes.entrySet().iterator().next();
        Rope rope1 = hashMap1.getKey();
        Map.Entry<Rope, Boolean> hashMap2 = box2.connectedRopes.entrySet().iterator().next();
        Rope rope2 = hashMap2.getKey();


        // Cases
        double magnitude =0;
        double box1Angle =0;
        double box2Angle =0;

        double xTension1 =0;
        double yTension1 =0;
        double xTension2 =0;
        double yTension2 =0;

        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double tempNet1 = Double.parseDouble(box1.getTextField().getText()) * gravityValue;
        double tempNet2 = Double.parseDouble(box2.getTextField().getText()) * gravityValue;


        if(!box1.isSnapped && !box2.isSnapped) {
            if (tempNet1 > tempNet2) {
                magnitude = tempNet1 - tempNet2;
                box1Angle = 270;
                box2Angle = 270;
            } else {
                magnitude = tempNet2 - tempNet1;
                box1Angle = 270;
                box2Angle = 270;
            }

            xTension1 = 0;
            yTension1 = magnitude;
            xTension2 = 0;
            yTension2 = magnitude;
        }



        VectorDisplay tensionVector1 = new VectorDisplay(position1X, position1Y,
                magnitude, box1Angle, "Tension", Color.DARKVIOLET);
        box1.tensionVector1 = tensionVector1;

        VectorDisplay tensionVector2 = new VectorDisplay(position2X, position2Y,
                magnitude, box2Angle, "Tension", Color.DARKVIOLET);
        box2.tensionVector1 = tensionVector2;

        Sandbox.sandBoxPane.getChildren().addAll(tensionVector1, tensionVector2);


        VectorDisplay tension1X = new VectorDisplay(
                position1X, position1Y, xTension1, 0, "Tx", Color.INDIGO);
        VectorDisplay tension1Y = new VectorDisplay(
                position1X, position1Y, yTension1, 270, "Ty", Color.INDIGO);

        VectorDisplay tension2X = new VectorDisplay(
                position2X, position2Y, xTension2, 0, "Tx", Color.INDIGO);
        VectorDisplay tension2Y = new VectorDisplay(
                position2X, position2Y, yTension2, 270, "Ty", Color.INDIGO);


        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(tension1X), adaptComponentOrientation(tension1Y));
        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(tension2X), adaptComponentOrientation(tension2Y));





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
        if(xComponent !=0 && yComponent !=0) {
            double phi = Math.toDegrees(Math.atan(yComponent / xComponent)) + 180; //Angle between net vector and x component (used as reference)
            System.out.println("phi = " + phi);

            //Net angle adjustment
            if (0 <= boxAngle && boxAngle < 90) {
                netAngle = phi;
            } else if (90 <= boxAngle && boxAngle < 180) {
                netAngle = 180 - phi;
            } else if (180 <= boxAngle && boxAngle < 270) {
                netAngle = 180 + phi;
            } else {
                netAngle = 360 - phi;
            }
        }
        else {
            netAngle = 0;
        }

        VectorDisplay netVector = new VectorDisplay(positionCenterX, positionCenterY,
                magnitude, netAngle, "Net", Color.BLACK);
        box.netVector = netVector;
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

    private static boolean isRopeHigherThanBox(Box box, Rope rope) {
        double ropeTopX;
        if(rope.getLine().getStartY() < rope.getLine().getEndY()) {
            ropeTopX = rope.getLine().getStartX();
        }
        else {
            ropeTopX = rope.getLine().getEndX();
        }

        double boxAngle = box.getRectangle().getRotate();
        if(boxAngle >= 0 && boxAngle < 90) {
            return (ropeTopX < box.getRectangle().getX());
        }
        else {
            return (ropeTopX > box.getRectangle().getX());
        }
    }

    static double normalizeAngle(double angle) {
        return (angle % 360 + 360) % 360;
    }

    private static double[] tensionVectorPositions(Box box) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double boxRotationAngle = box.getRectangle().getRotate();

        // Create a Point2D to represent the center of the rectangle
        Point2D center = new Point2D(positionCenterX, positionCenterY);

        // The vector's original position relative to the center (before rotation)
        double vectorX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double vectorY = box.getRectangle().getY();

        // Rotate the vector point relative to the center
        Rotate rotate = new Rotate(boxRotationAngle, center.getX(), center.getY());
        Point2D rotatedVector = rotate.transform(new Point2D(vectorX, vectorY));

        // Now, rotatedVector gives the new position of the vector after the box is rotated
        double[] positions = new double[2];
        positions[0] = rotatedVector.getX();
        positions[1] = rotatedVector.getY();

        return positions;
    }

}
