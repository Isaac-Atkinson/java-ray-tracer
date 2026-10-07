package com.example.raytracer.bvh;

import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.geometry.Sphere;
import com.example.raytracer.math.Ray;
import com.example.raytracer.math.Vector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void constructBoundingBoxEnclosesPrimitive(){
        Sphere sphere = new Sphere(new Vector(0, 0, 0), 10);  // min (-10, -10, -10) max (10, 10, 10)

        Node node = new Node();
        node.addObject(sphere);

        node.constructBoundingBox();

        assertEquals(-10, node.getBoundingBox().getMinValues().x);
        assertEquals(-10, node.getBoundingBox().getMinValues().y);
        assertEquals(-10, node.getBoundingBox().getMinValues().z);

        assertEquals(10, node.getBoundingBox().getMaxValues().x);
        assertEquals(10, node.getBoundingBox().getMaxValues().y);
        assertEquals(10, node.getBoundingBox().getMaxValues().z);
    }

    @Test
    void constructBoundingBoxEnclosesLargerOfTwoPrimitives(){
        Sphere a = new Sphere(new Vector(0, 0, 0), 10); // min (-10, -10, -10) max (10, 10, 10)
        Sphere b = new Sphere(new Vector(0, 0, 0), 20); // min (-20, -20, -20) max (20, 20, 20)

        Node node =  new Node();
        node.addObject(a);
        node.addObject(b);

        node.constructBoundingBox();

        assertEquals(-20, node.getBoundingBox().getMinValues().x);
        assertEquals(-20, node.getBoundingBox().getMinValues().y);
        assertEquals(-20, node.getBoundingBox().getMinValues().z);

        assertEquals(20, node.getBoundingBox().getMaxValues().x);
        assertEquals(20, node.getBoundingBox().getMaxValues().y);
        assertEquals(20, node.getBoundingBox().getMaxValues().z);
    }

    @Test
    void constructBoundingBoxEnclosesAllPrimitives(){

        Sphere a = new Sphere(new Vector(0, 5, -10), 10);   // min (-10,-5,-20)  max (10,15,0)
        Sphere b = new Sphere(new Vector(20, -30, 15), 5);  // min (15,-35,10)   max (25,-25,20)

        Node node =  new Node();
        node.addObject(a);
        node.addObject(b);

        node.constructBoundingBox();

        assertEquals(-10, node.getBoundingBox().getMinValues().x);
        assertEquals(-35, node.getBoundingBox().getMinValues().y);
        assertEquals(-20, node.getBoundingBox().getMinValues().z);

        assertEquals(25, node.getBoundingBox().getMaxValues().x);
        assertEquals(15, node.getBoundingBox().getMaxValues().y);
        assertEquals(20, node.getBoundingBox().getMaxValues().z);
    }



    @Test
    void sortPrimitivesOrdersAlongXAxis(){
        Sphere left = new Sphere(new Vector(-50, 0, 0), 5);
        Sphere middle = new Sphere(new Vector(0, 0, 0), 5);
        Sphere right = new Sphere(new Vector(50, 0, 0), 5);

        ArrayList<SceneObject> objects = new ArrayList<>(
                List.of(middle, right, left)
        );

        Node node = new Node(objects);

        node.sortPrimitives(Axis.X);

        assertSame(left, node.getPrimitives().get(0));
        assertSame(middle, node.getPrimitives().get(1));
        assertSame(right, node.getPrimitives().get(2));
    }

    @Test
    void sortPrimitivesOrdersAlongYAxis(){
        Sphere bottom = new Sphere(new Vector(0, -50, 0), 5);
        Sphere middle = new Sphere(new Vector(0, 0, 0), 5);
        Sphere top = new Sphere(new Vector(0, 50, 0), 5);

        ArrayList<SceneObject> objects = new ArrayList<>(
                List.of(top, bottom, middle)
        );

        Node node = new Node(objects);

        node.sortPrimitives(Axis.Y);

        assertSame(bottom, node.getPrimitives().get(0));
        assertSame(middle, node.getPrimitives().get(1));
        assertSame(top, node.getPrimitives().get(2));
    }

    @Test
    void sortPrimitivesOrdersAlongZAxis(){
        Sphere back = new Sphere(new Vector(0, 0, -50), 5);
        Sphere middle = new Sphere(new Vector(0, 0, 0), 5);
        Sphere front = new Sphere(new Vector(0, 0, 50), 5);

        ArrayList<SceneObject> objects = new ArrayList<>(
                List.of(middle, back, front)
        );

        Node node = new Node(objects);

        node.sortPrimitives(Axis.Z);

        assertSame(back, node.getPrimitives().get(0));
        assertSame(middle, node.getPrimitives().get(1));
        assertSame(front, node.getPrimitives().get(2));
    }

}