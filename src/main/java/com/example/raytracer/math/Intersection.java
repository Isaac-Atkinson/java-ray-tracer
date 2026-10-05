package com.example.raytracer.math;

import com.example.raytracer.geometry.SceneObject;


/**
 * Represents an intersection.
 * Stores the object that was hit and the distance t along
 * the ray the intersection occurred.
 */
public class Intersection {

    public SceneObject hit;
    public double t;

    public Intersection(SceneObject hit, double t) {
        this.hit = hit;
        this.t = t;
    }
}
