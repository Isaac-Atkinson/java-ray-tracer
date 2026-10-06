package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class SphereTest {

    private static final double EPSILON = 1e-6;

    private Sphere sphere;

    @BeforeEach
    void setUp() {
        sphere = new Sphere(new Vector(0, 0, 200), 50);
    }


    @Test
    void rayIntersectsSphere() {

        Ray ray = new Ray(
                new Vector(0, 0, 0),
                new Vector(0, 0, 1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNotNull(intersection);
        assertEquals(150.0, intersection.t, EPSILON);
    }

    @Test
    void rayHitsSphereTangentially(){

        Ray ray = new Ray(
                new  Vector(50, 0, 0),
                new Vector(0, 0, 1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNotNull(intersection);
        assertEquals(200.0, intersection.t,EPSILON);
    }

    @Test
    void rayMissesSphere(){
        Ray ray = new Ray(
                new Vector(100, 0, 0),
                new Vector(0, 0, 1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayPointingAwayFromSphereMisses(){

        Ray ray = new Ray(
                new  Vector(0, 0, 0),
                new Vector(0, 0, -1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayStartingInsideSphereIntersectsSurface(){

        Ray ray = new Ray(
                new  Vector(0, 0, 200),
                new Vector(0, 0, 1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNotNull(intersection);
        assertEquals(50.0, intersection.t, EPSILON);
    }

    @Test
    void rayStartingOnSphereSurfaceDoesntIntersectItself(){

        Ray ray = new Ray(
                new  Vector(0, 0, 250),
                new Vector(0, 0, 1)
        );

        Intersection intersection = sphere.intersect(ray);

        assertNull(intersection);
    }




    @Test
    void calculateBoundsSetsMinAndMaxVals(){

        Sphere s = new Sphere(new Vector(10, -20, 30), 5); // min (5, -25, 25) // max(15, -15, 35)


        assertEquals(5, s.minVals.x);
        assertEquals(-25, s.minVals.y);
        assertEquals(25, s.minVals.z);

        assertEquals(15, s.maxVals.x);
        assertEquals(-15, s.maxVals.y);
        assertEquals(35, s.maxVals.z);
    }

}