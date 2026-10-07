package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;



/**
 * Represents a 3D triangle.
 * Stores its three vertices.
 */
public class Triangle extends SceneObject {

    private static final double EPSILON = 1e-6;

    private final Vector pointA;
    private final Vector pointB;
    private final Vector pointC;

    private final Vector normal;

    private final Vector edgeAB;
    private final Vector edgeAC;


    public Triangle(Vector pointA, Vector pointB, Vector pointC){
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;

        calculateCentre();

        calculateBounds();

        edgeAB = pointB.sub(pointA);
        edgeAC = pointC.sub(pointA);

        normal = edgeAB.cross(edgeAC).mul(-1);
        normal.normalise();
    }


    /**
     * Tests a ray for intersection with this triangle
     * @param ray the ray to test
     * @return the intersection, or null if the ray does not hit the triangle
     */
    public Intersection intersect(Ray ray){

        double det = edgeAB.dot(ray.direction.cross(edgeAC));

        if(det > -EPSILON && det < EPSILON) {
            return null;
        }

        Vector oMinusV0 = ray.origin.sub(pointA);

        double u = oMinusV0.dot(ray.direction.cross(edgeAC)) / det;
        if(u < 0 || u > 1 ) {
            return null;
        }

        Vector r = oMinusV0.cross(edgeAB);

        double v = ray.direction.dot(r) / det;
        if(v < 0 || u + v > 1 ) {
            return null;
        }

        double t = edgeAC.dot(r) / det;

        if (t <= EPSILON) {
            return null;
        }

        return new Intersection(this, t);
    }

    @Override
    public Vector getNormal(Vector intersection){
        return normal;
    }

    private void calculateCentre(){
        getCentre().x = (pointA.x + pointB.x + pointC.x) / 3;
        getCentre().y = (pointA.y + pointB.y + pointC.y) / 3;
        getCentre().z = (pointA.z + pointB.z + pointC.z) / 3;
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
