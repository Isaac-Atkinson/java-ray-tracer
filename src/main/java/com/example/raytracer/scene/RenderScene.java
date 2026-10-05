package com.example.raytracer.scene;
import com.example.raytracer.bvh.BVH;
import com.example.raytracer.geometry.Plane;
import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.math.Vector;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Represents the scene to be rendered.
 * Stores the objects, light source and BVH
 */
public class RenderScene {

    private ArrayList<SceneObject> objects = new ArrayList<>();

    private LightSource light;

    private final BVH bvh;

    public RenderScene(){
        initialiseScene();
        bvh = new BVH(new ArrayList<>(objects));
    }

    /**
     * returns the closest intersection between the ray and the scene
     * @param ray the ray being traced
     * @return an Intersection containing information about the closest hit
     */
    public Intersection closestHit(Ray ray){
        return bvh.traverseBVH(ray);
    }

    public boolean isOccluded(Ray ray, double distToLight){
        return bvh.isOccluded(ray, distToLight);
    }



    public LightSource getLight(){
        return light;
    }


    public void addObjects(ArrayList<SceneObject> newObjects){
        initialiseWalls();
        objects.addAll(newObjects);
        bvh.addObjects(objects);
        bvh.constructBVH();
    }

    public void clearObjects(){
        objects.clear();
        bvh.clearObjects();
    }



    private void initialiseScene(){
        initialiseLight();
        initialiseWalls();
    }

    private void initialiseLight(){
        Vector lightPos = new Vector(0, 0, -800);
        Vector up = new Vector(0, 1, 0);
        Vector right = new Vector(1, 0, 0);
        light = new LightSource(
                lightPos,
                Color.color(1,1,1),
                20,
                20,
                right,
                up
        );
    }

    private void initialiseWalls(){
        Color ambientRed = Color.color(0.2, 0.02, 0.02);
        Color diffuseRed = Color.color(1, 0.2, 0.2);
        Color ambientGreen = Color.color(0.02, 0.2, 0.02);
        Color diffuseGreen = Color.color(0.2, 1, 0.2);
        Color ambientBlue = Color.color(0.02, 0.02, 0.2);
        Color diffuseBlue = Color.color(0.2, 0.2, 1);
        Color specular = Color.color(0, 0, 0);

        // Back wall: z = 1600
        Plane backWall = new Plane(
                new Vector(0, 0, -1),
                new Vector(0, 0, 2000),
                ambientBlue, diffuseBlue, specular, 0
        );
        backWall.minVals = new Vector(-2000, -2000, 2000);
        backWall.maxVals = new Vector(2000, 2000, 2000);
        backWall.centre = new Vector(0, 0, 2000);
        objects.add(backWall);

        // Front wall: z = -1600
        Plane frontWall = new Plane(
                new Vector(0, 0, 1),
                new Vector(0, 0, -2000),
                ambientBlue, diffuseBlue, specular, 0
        );
        frontWall.minVals = new Vector(-2000, -2000, -2000);
        frontWall.maxVals = new Vector(2000, 2000, -2000);
        frontWall.centre = new Vector(0, 0, -2000);
        objects.add(frontWall);

        // Left wall: x = -800
        Plane leftWall = new Plane(
                new Vector(1, 0, 0),
                new Vector(-2000, 0, 0),
                ambientRed, diffuseRed, specular, 0
        );
        leftWall.minVals = new Vector(-2000, -2000, -2000);
        leftWall.maxVals = new Vector(-2000, 2000, 2000);
        leftWall.centre = new Vector(-2000, 0, 0);
        objects.add(leftWall);

        // Right wall: x = 800
        Plane rightWall = new Plane(
                new Vector(-1, 0, 0),
                new Vector(2000, 0, 0),
                ambientRed, diffuseRed, specular, 0
        );
        rightWall.minVals = new Vector(2000, -2000, -2000);
        rightWall.maxVals = new Vector(2000, 2000, 2000);
        rightWall.centre = new Vector(2000, 0, 0);
        objects.add(rightWall);

        // Floor: y = -800
        Plane floor = new Plane(
                new Vector(0, 1, 0),
                new Vector(0, -2000, 0),
                ambientGreen, diffuseGreen, specular, 0
        );
        floor.minVals = new Vector(-2000, -1600, -2000);
        floor.maxVals = new Vector(2000, -1600, 2000);
        floor.centre = new Vector(0, -1600, 0);
        objects.add(floor);

        // Ceiling: y = 800
        Plane ceiling = new Plane(
                new Vector(0, -1, 0),
                new Vector(0, 2000, 0),
                ambientGreen, diffuseGreen, specular, 0
        );
        ceiling.minVals = new Vector(-2000, 2000, -2000);
        ceiling.maxVals = new Vector(2000, 2000, 2000);
        ceiling.centre = new Vector(0, 2000, 0);
        objects.add(ceiling);
    }
}
