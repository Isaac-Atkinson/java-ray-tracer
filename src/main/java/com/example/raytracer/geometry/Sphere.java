package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;

/**
 * Represents a 3D sphere.
 * Stores a centre and radius.
 */
public class Sphere extends SceneObject {


    private final double radius;


    public Sphere(Vector sphereCentre, double radius) {
        this.centre = sphereCentre;
        this.radius = radius;
        calculateBounds();
    }

    /**
     * Tests a ray for intersection with this sphere
     * @param ray the ray to test
     * @return the intersection, or null if the ray does not hit the sphere
     */
    public Intersection intersect(Ray ray){
        Vector v = ray.origin.sub(getCentre());

        double a = ray.direction.dot(ray.direction);
        double b = 2 * v.dot(ray.direction);
        double c = v.dot(v) - (radius * radius);

        double disc = b * b - 4 * a * c;

        if(disc < 0) return null; //ray misses sphere

        double t1 = (-b - Math.sqrt(disc)) / (2 * a);
        double t2 = (-b + Math.sqrt(disc)) / (2 * a);
        double t = smallestPositive(t1, t2);

        if(t == -1) return null;

        return new Intersection(this, t);
    }

    @Override
    public Vector getNormal(Vector intersection) {
        return intersection.sub(getCentre());
    }


    private double smallestPositive(double a, double b) {
        if(a > 0 && b > 0) {
            return Math.min(a, b);
        } else if(a > 0){
            return a;
        } else if(b > 0){
            return b;
        } else{
            return -1;
        }
    }

    /**
     * Calculates the minimum and maximum values of this sphere in 3D space.
     */
    private void calculateBounds() {
        double lowestX = getCentre().x - radius;
        double highestX = getCentre().x + radius;
        double lowestY = getCentre().y - radius;
        double highestY = getCentre().y + radius;
        double lowestZ = getCentre().z - radius;
        double highestZ = getCentre().z + radius;

        minVals = new Vector(lowestX, lowestY, lowestZ);
        maxVals = new Vector(highestX, highestY, highestZ);
    }

}
