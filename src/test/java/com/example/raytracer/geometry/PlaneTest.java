package com.example.raytracer.geometry;

import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaneTest {

    private static final double EPSILON = 1e-6;

    private Plane plane;

    @BeforeEach
    void setUp() {
        plane = new Plane(
                new Vector(0, 0, -1),
                new Vector(0, 0, 100)
        );
    }

    @Test
    void rayIntersectsPlane(){

        Ray ray = new Ray(
                new Vector(0, 0, 0),
                new Vector(0, 0, 1)
        );

        Intersection intersection = plane.intersect(ray);

        assertNotNull(intersection);

        assertEquals(100.0, intersection.t, EPSILON);
    }

    @Test
    void rayPointingAwayFromPlaneMisses(){

        Ray ray = new Ray(
                new Vector(0, 0, 0),
                new Vector(0, 0, -1)
        );

        Intersection intersection = plane.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayParallelToPlaneMisses(){

        Ray ray = new Ray(
                new Vector(0, 0, 100),
                new Vector(1, 0, 0)
        );

        Intersection intersection = plane.intersect(ray);

        assertNull(intersection);
    }

    @Test
    void rayStartingOnPlaneSurfaceDoesntIntersectItself(){

        Ray ray = new Ray(
                new Vector(0, 0, 100),
                new Vector(0, 0, 1)
        );

        Intersection intersection = plane.intersect(ray);

        assertNull(intersection);
    }

}