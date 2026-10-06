package com.example.raytracer.bvh;

import com.example.raytracer.math.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxTest {

    @Test
    void longestAxisIsX(){

        BoundingBox b =  new BoundingBox(
                new Vector(-30, -20, -10),
                new Vector(30, 20, 10)
        );

        Axis axis = b.longestAxis();

        assertSame(Axis.X, axis);
    }

    @Test
    void longestAxisIsY(){

        BoundingBox b =  new BoundingBox(
                new Vector(-10, -30, -20),
                new Vector(10, 30, 20)
        );

        Axis axis = b.longestAxis();

        assertSame(Axis.Y, axis);
    }

    @Test
    void longestAxisIsZ(){

        BoundingBox b =  new BoundingBox(
                new Vector(-10, -20, -30),
                new Vector(10, 20, 30)
        );

        Axis axis = b.longestAxis();

        assertSame(Axis.Z, axis);
    }

    @Test
    void AllAxisAreEqualReturnsXAxis(){
        BoundingBox b =  new BoundingBox(
                new Vector(-10, -10, -10),
                new Vector(10, 10, 10)
        );

        Axis axis = b.longestAxis();
        assertSame(Axis.X, axis);
    }

}