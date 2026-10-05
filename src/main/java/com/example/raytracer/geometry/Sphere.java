package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import javafx.scene.paint.Color;
import com.example.raytracer.math.Vector;

/**
 * Represents a 3D sphere.
 * Stores a centre and radius.
 */
public class Sphere extends SceneObject {


    public double radius;


    public Sphere(Vector centre, double radius, Color ambient, Color diffuse, Color specular, double shininess) {
        super(ambient, diffuse, specular, shininess, centre);
        this.radius = radius;
        calculateBounds();
    }

    public Sphere(Vector sphereCentre, double radius) {
        this.centre = sphereCentre;
        this.radius = radius;
        calculateBounds();
    }

    /**
     * Tests a ray for intersection with this sphere
     * @param ray the ray to test
     * @return a HitObject representing the intersection, or null
     * if there is no intersection
     */
    public Intersection intersect(Ray ray){
        Vector v = ray.origin.sub(centre);

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
        return intersection.sub(centre);
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

    private void calculateBounds() {
        double lowestX = centre.x - radius;
        double highestX = centre.x + radius;
        double lowestY = centre.y - radius;
        double highestY = centre.y + radius;
        double lowestZ = centre.z - radius;
        double highestZ = centre.z + radius;

        minVals = new Vector(lowestX, lowestY, lowestZ);
        maxVals = new Vector(highestX, highestY, highestZ);
    }

}
