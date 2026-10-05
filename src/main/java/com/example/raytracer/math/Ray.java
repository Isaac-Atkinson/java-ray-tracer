package com.example.raytracer.math;

/**
 * Represents a ray
 * rays have an origin and a direction
 */
public class Ray {

    public Vector origin;
    public Vector direction;


    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
}
