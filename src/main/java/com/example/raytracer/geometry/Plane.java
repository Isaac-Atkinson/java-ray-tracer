package com.example.raytracer.geometry;
import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import javafx.scene.paint.Color;


public class Plane extends SceneObject {

    private static final double EPSILON = 1e-6;

    private final Vector planeNormal;
    private final Vector pointOnPlane;

    public Plane (Vector planeNormal, Vector pointOnPlane,
                  Color ambient, Color diffuse, Color specular,
                  double shininess ) {
        super(ambient,diffuse,specular,shininess);
        this.planeNormal = planeNormal;
        this.pointOnPlane = pointOnPlane;
    }

    public Plane (Vector planeNormal, Vector pointOnPlane){
        this.planeNormal = planeNormal;
        this.pointOnPlane = pointOnPlane;
    }

    public Intersection intersect(Ray ray){
        double denom = planeNormal.dot(ray.direction);

        if(denom == 0) return null;

        if(denom > -EPSILON && denom < EPSILON) {
            return null;
        }

        double t = planeNormal.dot(pointOnPlane.sub(ray.origin)) / denom;
        if(t < EPSILON) {
            return null;
        }
        return new Intersection(this, t);
    }

    public Vector getNormal(Vector intersection){
        return planeNormal;
    }

    public void setMinVals(Vector minVals){
        this.minVals = minVals;
    }

    public void setMaxVals(Vector maxVals){
        this.maxVals = maxVals;
    }

    public void setCentre(Vector centre){
        this.centre = centre;
    }


}
