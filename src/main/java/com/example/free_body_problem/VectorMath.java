package com.example.free_body_problem;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point2D;


public final class VectorMath {

    public static Pulley
            connectionPulley;

    public VectorMath() {
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

        if(!box.isNetSet)
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

        if(box.isPulled) {
            frictionAngle += 180;
        }


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
            angle = ropeRotationAngle + 180;
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

    public static void calculateTension2Ropes(Box box, Rope leftRope, Rope rightRope) {
        double positionCenterX = box.getRectangle().getX() + box.getRectangle().getWidth() / 2;
        double positionCenterY = box.getRectangle().getY() + box.getRectangle().getHeight() / 2;
        double boxRotationAngle = box.getRectangle().getRotate();
        // Putting orientations to "normal" math angles
        double leftRopeRotationAngle = normalizeAngle(360 - leftRope.getOrientation());
        double rightRopeRotationAngle = normalizeAngle(360 - rightRope.getOrientation());


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
        double leftMagnitude;
        double rightMagnitude;
        double leftAngle;
        double rightAngle;
        double leftXTension;
        double leftYTension;
        double rightXTension;
        double rightYTension;
        boolean leftRopeStartIsHigher = leftRope.getLine().getStartY() > leftRope.getLine().getEndY();
        boolean rightRopeStartIsHigher = rightRope.getLine().getStartY() > rightRope.getLine().getEndY();

        boolean isLeftRopeChaining = leftRope.getStartConnection() instanceof Box && leftRope.getEndConnection() instanceof Box;
        boolean isRightRopeChaining = rightRope.getStartConnection() instanceof Box && rightRope.getEndConnection() instanceof Box;

        if(!box.isSnapped && !isLeftRopeChaining && !isRightRopeChaining) {
            // Convert angles to radians for calculations
            double rightAngleRad = Math.toRadians(leftRopeRotationAngle);
            double leftAngleRad = Math.toRadians(rightRopeRotationAngle);

            // Calculate magnitudes (always positive)
            rightMagnitude = Math.abs(box.gravityVector.getTrueLength() /
                    ((Math.cos(rightAngleRad)/Math.cos(leftAngleRad)) + Math.sin(rightAngleRad)));
            leftMagnitude = Math.abs((rightMagnitude * Math.cos(rightAngleRad)) / Math.cos(leftAngleRad));

            // Physics components (signs matter for calculation)
            leftXTension = -leftMagnitude * Math.cos(leftAngleRad);  // Negative for left pull
            leftYTension = -leftMagnitude * Math.sin(leftAngleRad);   // Positive for upward
            rightXTension = -rightMagnitude * Math.cos(rightAngleRad); // Positive for right
            rightYTension = -rightMagnitude * Math.sin(rightAngleRad); // Positive for upward

            leftAngle = calculateDisplayAngle(leftRope, leftRopeStartIsHigher);
            rightAngle = calculateDisplayAngle(rightRope, rightRopeStartIsHigher);


            // Create main tension vectors
            VectorDisplay leftTensionVector = new VectorDisplay(newPositionX, newPositionY,
                    leftMagnitude, rightAngle, "T1", Color.DARKVIOLET);

            VectorDisplay rightTensionVector = new VectorDisplay(newPositionX, newPositionY,
                    rightMagnitude, leftAngle, "T2", Color.DARKVIOLET);

            // Create component vectors
            VectorDisplay leftTensionX = new VectorDisplay(
                    newPositionX - 3, newPositionY, leftXTension, 0, "T1x", Color.INDIGO);

            VectorDisplay leftTensionY = new VectorDisplay(
                    newPositionX - 3, newPositionY, leftYTension, 270, "T1y", Color.INDIGO);

            VectorDisplay rightTensionX = new VectorDisplay(
                    newPositionX + 3, newPositionY, rightXTension, 0, "T2x", Color.INDIGO);

            VectorDisplay rightTensionY = new VectorDisplay(
                    newPositionX + 3, newPositionY, rightYTension, 270, "T2y", Color.INDIGO);


            Sandbox.sandBoxPane.getChildren().addAll(
                    leftTensionVector,
                    rightTensionVector,
                    adaptComponentOrientation(leftTensionX),
                    adaptComponentOrientation(leftTensionY),
                    adaptComponentOrientation(rightTensionX),
                    adaptComponentOrientation(rightTensionY)
            );
        }
    }

    public static void calculatePulleyTension(Box box1, Box box2, Pulley connectionPulley){
        VectorMath.connectionPulley = connectionPulley;
        System.out.println("\nBefore starting calculations");
        System.out.println("box1 x force:" + box1.totalXForce + "; box1 y force:" + box2.totalXForce);
        System.out.println("box2 x force: " + box2.totalXForce + "; box2 y force:" + box1.totalXForce);

        double position1X = tensionVectorPositions(box1)[0];
        double position1Y = tensionVectorPositions(box1)[1];
        double position2X = tensionVectorPositions(box2)[0];
        double position2Y = tensionVectorPositions(box2)[1];

        box1.connectedRopes.entrySet().iterator().next();


        // Cases
        double magnitude =0;
        double box1Angle = box1.getRectangle().getRotate();
        double box2Angle = box2.getRectangle().getRotate();

        double xTension1 =0;
        double yTension1 =0;
        double xTension2 =0;
        double yTension2 =0;

        double gravityValue = Double.parseDouble(Sandbox.gravityField.getText());
        double m1 = Double.parseDouble(box1.getTextField().getText());
        double m2 = Double.parseDouble(box2.getTextField().getText());
        double weight1 = m1 * gravityValue;  // Weight of box1
        double weight2 = m2 * gravityValue;  // Weight of box2


        if(!box1.isSnapped && !box2.isSnapped) {
            magnitude = (2 * m1 * m2 * gravityValue) / (m1 + m2);

            box1Angle = 270;
            box2Angle = 270;
            xTension1 = 0;
            yTension1 = magnitude;
            xTension2 = 0;
            yTension2 = magnitude;

            box1.totalYForce = -1*(weight1 - magnitude);
            box2.totalYForce = -1*(weight2 - magnitude);
            box1.totalXForce = 0;
            box2.totalXForce = 0;
            box1.isNetSet = true;
            box2.isNetSet = true;
        }

        else if(box1.isSnapped && box2.isSnapped) {
            double inclineWeight1 = weight1 * Math.sin(Math.toRadians(box1Angle));
            double inclineWeight2 = weight2 * Math.sin(Math.toRadians(box2Angle));
            magnitude = (2 * inclineWeight1 * inclineWeight2) / (inclineWeight1 + inclineWeight2);

            box1Angle = calculatePulleyBoxAngles(box1, connectionPulley);
            box2Angle = calculatePulleyBoxAngles(box2, connectionPulley);

            // Potential Friction Flip
            if(inclineWeight1 < inclineWeight2) {
                box2.isPulled = true;
                box2.frictionVector.setRotation(box2.frictionVector.getRotation() + 180);
            }
            if(inclineWeight1 > inclineWeight2) {
                box1.isPulled = true;
                box1.frictionVector.setRotation(box1.frictionVector.getRotation() + 180);
            }

            xTension1 = magnitude * Math.sin(box1Angle);
            yTension1 = magnitude * Math.cos(box1Angle);
            xTension2 = magnitude * Math.sin(box2Angle);
            yTension2 = magnitude * Math.cos(box2Angle);

            box1.totalXForce += xTension1;
            box1.totalYForce += yTension1;
            box2.totalXForce += xTension2;
            box2.totalYForce += yTension2;
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

        System.out.println("\nIn Net Vector Calculations (" + box + "):");
        System.out.println("box x force:" + box.totalXForce + "; box y force:" + box.totalYForce);

        double magnitude = Math.sqrt(xComponent*xComponent + yComponent*yComponent);
        if(xComponent == 0 && yComponent == 0) {
            magnitude = 0;
        }
        double boxAngle = box.getRectangle().getRotate()+90;
        double netAngle;

        System.out.println("net magnitude: " + magnitude);

        //Angle calculation for all 4 cases
        if(xComponent !=0 && yComponent !=0) {
            double phi = Math.toDegrees(Math.atan(yComponent / xComponent)) + 180; //Angle between net vector and x component (used as reference)

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
        else if(xComponent != 0) {
            netAngle = xComponent >= 0.0001 ? 0 : 180;
        }
        else if (yComponent != 0) {
            netAngle = yComponent >= 0.0001 ? 270 : 90;
        }
        else {
            netAngle = 0;
        }

        VectorDisplay netVector = new VectorDisplay(positionCenterX, positionCenterY,
                magnitude, netAngle, "Net", Color.BLACK);
        box.netVector = netVector;
        Sandbox.sandBoxPane.getChildren().add(netVector);



        // Components
        VectorDisplay xComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                xComponent, 0, "NetX", Color.GRAY);
        VectorDisplay yComponentVector = new VectorDisplay(positionCenterX, positionCenterY,
                yComponent, 270, "NetY", Color.GRAY);
        Sandbox.sandBoxPane.getChildren().addAll(
                adaptComponentOrientation(xComponentVector),
                adaptComponentOrientation(yComponentVector));


        //End of calculation, reset status
        box.isPulled = false;
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

    private static double calculateDisplayAngle(Rope rope, boolean startIsHigher) {
        double deltaX = rope.getLine().getEndX() - rope.getLine().getStartX();
        double deltaY = rope.getLine().getEndY() - rope.getLine().getStartY();

        // Calculate raw angle in JavaFX coordinates (0° = right, clockwise)
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
        if (angle < 0) angle += 360;

        // If the box is at the start, flip the angle to show tension direction
        if (!startIsHigher) {
            angle = (angle + 180) % 360;
        }

        return angle;
    }

    private static double calculatePulleyBoxAngles(Box box, Pulley pulley) {
        //Getting the points
        double x1 = box.getCenterX();
        double y1 = box.getCenterY();
        double x2 = pulley.getCenterX();
        double y2 = pulley.getCenterY();

        // Calculate differences
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        // Calculate angle in radians using arctan (atan2 to account for quadrants)
        double angleRadians = Math.atan2(deltaY, deltaX);
        double angleDegrees = Math.toDegrees(angleRadians);

        // Adjust to 0-360 range
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }

        return angleDegrees;
    }


}
