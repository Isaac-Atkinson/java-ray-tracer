package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;



/**
 * Represents a triangle in 3D space
 */
public class Triangle extends SceneObject {

    private static final double EPSILON = 1e-6;

    public Vector pointA;
    public Vector pointB;
    public Vector pointC;
    private Vector normal;

    Vector aTob;
    Vector aToc;


    public Triangle(Vector pointA, Vector pointB, Vector pointC){
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        calculateCentre();
        aTob = pointB.sub(pointA);
        aToc = pointC.sub(pointA);
        normal = aTob.cross(aToc).mul(-1);
        calculateBounds();
    }


    /**
     * Tests a ray for intersection with this triangle
     * @param ray the ray to test
     * @return a HitObject representing the intersection, or null
     * if there is no intersection
     */
    public Intersection intersect(Ray ray){

        double det = aTob.dot(ray.direction.cross(aToc));

        if(det > -EPSILON && det < EPSILON) {
            return null;
        }

        Vector oMinusV0 = ray.origin.sub(pointA);

        double u = oMinusV0.dot(ray.direction.cross(aToc)) / det;
        if(u < 0 || u > 1 ) {
            return null;
        }

        Vector r = oMinusV0.cross(aTob);

        double v = ray.direction.dot(r) / det;
        if(v < 0 || u + v > 1 ) {
            return null;
        }

        double t = aToc.dot(r) / det;

        if(t > EPSILON) {
            return new Intersection(this, t);
        } else {
            return null;
        }
    }

    @Override
    public Vector getNormal(Vector intersection){
        return normal;
    }

    private void calculateCentre(){
        centre.x = (pointA.x + pointB.x + pointC.x) / 3;
        centre.y = (pointA.y + pointB.y + pointC.y) / 3;
        centre.z = (pointA.z + pointB.z + pointC.z) / 3;
    }


    /**
     * Calculates the minimum and maximum values of this triangle in 3D space.
     */
    private void calculateBounds(){
        double lowestX = Math.min(pointA.x, Math.min(pointB.x, pointC.x));
        double lowestY = Math.min(pointA.y, Math.min(pointB.y, pointC.y));
        double lowestZ = Math.min(pointA.z, Math.min(pointB.z, pointC.z));
        double highestX = Math.max(pointA.x, Math.max(pointB.x, pointC.x));
        double highestY = Math.max(pointA.y, Math.max(pointB.y, pointC.y));
        double highestZ = Math.max(pointA.z, Math.max(pointB.z, pointC.z));

        minVals = new Vector(lowestX, lowestY, lowestZ);
        maxVals = new Vector(highestX, highestY, highestZ);
    }



}
