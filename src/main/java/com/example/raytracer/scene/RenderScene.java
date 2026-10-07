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
 * Stores the objects, light source and the BVH acceleration structure.
 */
public class RenderScene {

    private final ArrayList<SceneObject> objects = new ArrayList<>();

    private LightSource light;

    private final BVH bvh;



    public RenderScene(){
        initialiseScene();
        bvh = new BVH(new ArrayList<>(objects));
    }

    /**
     * returns the closest intersection between the ray and the scene.
     * @param ray the ray being traced
     * @return an Intersection containing information about the closest hit
     */
    public Intersection closestHit(Ray ray){
        return bvh.closestHit(ray);
    }

    /**
     * Determines if a ray is blocked from reaching the light source.
     * @param ray a ray from the intersection to the light source
     * @param distToLight the distance to the light sample
     * @return true if the light source is occluded, false otherwise
     */
    public boolean isOccluded(Ray ray, double distToLight){
        return bvh.isOccluded(ray, distToLight);
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

        // Back wall: z = 2000
        Plane backWall = createWall(
                new Vector(0, 0, -1),
                new Vector(0, 0, 2000),
                ambientBlue,
                diffuseBlue,
                specular,
                new Vector(-2000, -2000, 2000),
                new Vector(2000, 2000, 2000),
                new Vector(0, 0, 2000)
        );
        objects.add(backWall);


        // Front wall: z = -2000
        Plane frontWall = createWall(
                new Vector(0, 0, 1),
                new Vector(0, 0, -2000),
                ambientBlue,
                diffuseBlue,
                specular,
                new Vector(-2000, -2000, -2000),
                new Vector(2000, 2000, -2000),
                new Vector(0, 0, -2000)
        );
        objects.add(frontWall);


        // Left wall: x = -2000
        Plane leftWall = createWall(
                new Vector(1, 0, 0),
                new Vector(-2000, 0, 0),
                ambientRed,
                diffuseRed,
                specular,
                new Vector(-2000, -2000, -2000),
                new Vector(-2000, 2000, 2000),
                new Vector(-2000, 0, 0)
        );
        objects.add(leftWall);


        // Right wall: x = 2000
        Plane rightWall = createWall(
                new Vector(-1, 0, 0),
                new Vector(2000, 0, 0),
                ambientRed,
                diffuseRed,
                specular,
                new Vector(2000, -2000, -2000),
                new Vector(2000, 2000, 2000),
                new Vector(2000, 0, 0)
        );
        objects.add(rightWall);


        // Floor: y = -2000
        Plane floor = createWall(
                new Vector(0, 1, 0),
                new Vector(0, -2000, 0),
                ambientGreen,
                diffuseGreen,
                specular,
                new Vector(-2000, -2000, -2000),
                new Vector(2000, -2000, 2000),
                new Vector(0, -2000, 0)
        );
        objects.add(floor);


        // Ceiling: y = 2000
        Plane ceiling = createWall(
                new Vector(0, -1, 0),
                new Vector(0, 2000, 0),
                ambientGreen,
                diffuseGreen,
                specular,
                new Vector(-2000, 2000, -2000),
                new Vector(2000, 2000, 2000),
                new Vector(0, 2000, 0)
        );
        objects.add(ceiling);
    }

    private Plane createWall(
            Vector normal,
            Vector pointOnPlane,
            Color ambient,
            Color diffuse,
            Color specular,
            Vector minVals,
            Vector maxVals,
            Vector centre
    ){
        Plane wall = new Plane(
                normal,
                pointOnPlane,
                ambient,
                diffuse,
                specular,
                0
        );

        wall.setMinVals(minVals);
        wall.setMaxVals(maxVals);
        wall.setCentre(centre);

        return wall;
    }



    public void replaceModel(ArrayList<SceneObject> newObjects) {
        clearObjects();

        initialiseWalls();
        objects.addAll(newObjects);

        bvh.addObjects(objects);
        bvh.constructBVH();
    }

    public void clearObjects(){
        objects.clear();
        bvh.clearObjects();
    }

    public LightSource getLight(){
        return light;
    }
}
