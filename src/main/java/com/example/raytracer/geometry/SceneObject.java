package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import javafx.scene.paint.Color;


public abstract class SceneObject {

    public Color diffuse;
    public Color ambient;
    public Color specular;

    public double shininess;

    public Vector centre = new Vector();

    public Vector minVals;
    public Vector maxVals;

    public SceneObject(){};

    public SceneObject(Color ambient, Color diffuse, Color specular, double shininess, Vector centre) {
        this.diffuse = diffuse;
        this.ambient = ambient;
        this.specular = specular;
        this.shininess = shininess;
        this.centre = centre;
    }

    public SceneObject(Color ambient, Color diffuse, Color specular, double shininess) {
        this.diffuse = diffuse;
        this.ambient = ambient;
        this.specular = specular;
        this.shininess = shininess;
    }

    public abstract Intersection intersect(Ray ray);

    public abstract Vector getNormal(Vector intersection);

    public void setAmbient(Color ambient){
        this.ambient = ambient;
    }
    public void setDiffuse(Color diffuse){
        this.diffuse = diffuse;
    }
    public void setSpecular(Color specular){
        this.specular = specular;
    }
    public void setShininess(double shininess) {this.shininess = shininess;};

}
