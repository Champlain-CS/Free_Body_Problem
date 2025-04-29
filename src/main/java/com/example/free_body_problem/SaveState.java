package com.example.free_body_problem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Used to save all necessary sandbox elements to store in a file;
 * WIP => not implemented yet
 */

public class SaveState implements Serializable {
    public double gravitySetting;
    public double frictionCoefficientSetting;
    public ArrayList<PhysicsObject> physicsObjects;

}
