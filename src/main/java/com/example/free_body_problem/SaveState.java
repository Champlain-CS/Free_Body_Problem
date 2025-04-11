package com.example.free_body_problem;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveState implements Serializable {
    public double gravitySetting;
    public double frictionCoefficientSetting;
    public ArrayList<PhysicsObject> physicsObjects;

}
