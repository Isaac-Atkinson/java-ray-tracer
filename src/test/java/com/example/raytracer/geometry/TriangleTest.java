package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {

    private static final double EPSILON = 1e-6;

    private Triangle triangle;

    @BeforeEach
    void setUp() {
        triangle = new Triangle(
                new Vector(-10, 0 , 100),
                new Vector(10, 0 , 100),
                new Vector(0, 10 , 100)
        );
    }

    @Test
    void rayIntersectsTriangle(){

        Ray ray = new Ray(
                new Vector(0, 0 , 0),
                new Vector(0, 0 , 1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNotNull(intersection);
        assertEquals(100.0, intersection.t, EPSILON);
    }

    @Test
    void rayHitsTriangleVertex(){
        Ray ray = new Ray(
                new Vector(10, 0 , 0),
                new Vector(0, 0 , 1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNotNull(intersection);
        assertEquals(100.0, intersection.t, EPSILON);
    }

    @Test
    void rayHitsTriangleEdge(){
        Ray ray = new Ray(
                new  Vector(5, 5 , 0),
                new Vector(0, 0 , 1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNotNull(intersection);
        assertEquals(100.0, intersection.t, EPSILON);
    }

    @Test
    void rayMissesTriangle(){
        Ray ray = new Ray(
                new Vector(100, 0 , 0),
                new Vector(0, 0 , 1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayPointingAwayFromTriangleMisses(){

        Ray ray = new Ray(
                new  Vector(0, 0, 0),
                new Vector(0, 0, -1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNull(intersection);
    }



    @Test
    void rayParallelToTriangleMisses(){
        Ray ray = new Ray(
                new  Vector(100, 100, 0),
                new Vector(-1, 0, 0)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayStartingOnTriangleDoesntIntersectItself(){
        Ray ray = new Ray(
                new Vector(0, 5 , 100),
                new Vector(0, 0 , 1)
        );

        Intersection intersection = triangle.intersect(ray);

        assertNull(intersection);
    }

}