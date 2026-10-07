package com.example.raytracer.bvh;

import com.example.raytracer.math.Vector;

/**
 * Represents a 3D bounding box which encloses all its primitives
 * as tightly as possible.
 * Stores its minimum and maximum extents.
 */
public class BoundingBox {

    private final Vector minValues;
    private final Vector maxValues;



    public BoundingBox(Vector minValues, Vector maxValues) {
        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    /**
     * Returns the longest axis of this bounding box
     * @return an enum describing the longest axis
     */
    public Axis longestAxis(){
        double xDiff = maxValues.x - minValues.x;
        double yDiff = maxValues.y - minValues.y;
        double zDiff = maxValues.z - minValues.z;

        if(xDiff >= yDiff && xDiff >= zDiff){
            return Axis.X;
        } else if(yDiff >= xDiff && yDiff >= zDiff){
            return Axis.Y;
        }  else {
            return Axis.Z;
        }
    }

    public Vector getMinValues() {
        return minValues;
    }

    public Vector getMaxValues() {
        return maxValues;
    }
}
