package com.example.raytracer.bvh;

import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.math.Vector;

import java.util.ArrayList;

/**
 * Represents a node in the Bounding Volume Hierarchy tree.
 * Nodes have a bounding box, a list of primitives in this node and
 * two children nodes.
 * A boolean isLeaf determines whether this node is a leaf node.
 * The list of primitives is only retained for leaf nodes otherwise it is
 * cleared after being used for BVH construction.
 */
public class Node {


    public BoundingBox boundingBox;
    public ArrayList<SceneObject> primitives = new ArrayList<>();
    public Node childA = null;
    public Node childB = null;
    public boolean isLeaf = false;

    public Node(){}

    public Node( ArrayList<SceneObject> primitives ) {
        this.primitives = new ArrayList<>(primitives);
    }

    public void addObject(SceneObject object) {
        primitives.add(object);
    }

    /**
     * Constructs the Axis Aligned Bounding Box for the primitives
     * in this node.
     */
    public void constructBoundingBox(){
        double lowestX = Double.POSITIVE_INFINITY;
        double highestX = Double.NEGATIVE_INFINITY;
        double lowestY = Double.POSITIVE_INFINITY;
        double highestY = Double.NEGATIVE_INFINITY;
        double lowestZ = Double.POSITIVE_INFINITY;
        double highestZ = Double.NEGATIVE_INFINITY;

        for(SceneObject obj : primitives){
            if(obj.minVals.x < lowestX ){
                lowestX = obj.minVals.x;
            }
            if(obj.minVals.y < lowestY){
                lowestY = obj.minVals.y;
            }
            if(obj.minVals.z < lowestZ){
                lowestZ = obj.minVals.z;
            }
            if(obj.maxVals.x > highestX ){
                highestX = obj.maxVals.x;
            }
            if(obj.maxVals.y > highestY){
                highestY = obj.maxVals.y;
            }
            if(obj.maxVals.z > highestZ){
                highestZ = obj.maxVals.z;
            }
        }

        Vector minValues = new Vector(lowestX, lowestY, lowestZ);
        Vector maxValues = new Vector(highestX, highestY, highestZ);
        boundingBox = new BoundingBox(minValues, maxValues);
    }

    public void sortPrimitives(Axis axis){
        primitives.sort((a, b) -> {
            if (axis == Axis.X) return Double.compare(a.centre.x, b.centre.x);
            if (axis == Axis.Y) return Double.compare(a.centre.y, b.centre.y);
            return Double.compare(a.centre.z, b.centre.z);
        });
    }
}
