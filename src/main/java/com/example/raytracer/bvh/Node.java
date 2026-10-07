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


    private BoundingBox boundingBox;
    private ArrayList<SceneObject> primitives = new ArrayList<>();
    private Node childA = null;
    private Node childB = null;
    private boolean isLeaf = false;

    public Node(){}

    public Node( ArrayList<SceneObject> primitives ) {
        this.primitives = new ArrayList<>(primitives);
    }

    public void addObject(SceneObject object) {
        getPrimitives().add(object);
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

        for(SceneObject obj : getPrimitives()){
            if(obj.getMinVals().x < lowestX ){
                lowestX = obj.getMinVals().x;
            }
            if(obj.getMinVals().y < lowestY){
                lowestY = obj.getMinVals().y;
            }
            if(obj.getMinVals().z < lowestZ){
                lowestZ = obj.getMinVals().z;
            }
            if(obj.getMaxVals().x > highestX ){
                highestX = obj.getMaxVals().x;
            }
            if(obj.getMaxVals().y > highestY){
                highestY = obj.getMaxVals().y;
            }
            if(obj.getMaxVals().z > highestZ){
                highestZ = obj.getMaxVals().z;
            }
        }

        Vector minValues = new Vector(lowestX, lowestY, lowestZ);
        Vector maxValues = new Vector(highestX, highestY, highestZ);
        boundingBox = new BoundingBox(minValues, maxValues);
    }

    /**
     * Sorts the primitives in this node along a given axis
     * @param axis the axis to sort along
     */
    public void sortPrimitives(Axis axis){
        getPrimitives().sort((a, b) -> {
            if (axis == Axis.X) return Double.compare(a.getCentre().x, b.getCentre().x);
            if (axis == Axis.Y) return Double.compare(a.getCentre().y, b.getCentre().y);
            return Double.compare(a.getCentre().z, b.getCentre().z);
        });
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public ArrayList<SceneObject> getPrimitives() {
        return primitives;
    }

    public Node getChildA() {
        return childA;
    }

    public Node getChildB() {
        return childB;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public void setChildA(Node childA) {
        this.childA = childA;
    }

    public void setChildB(Node childB) {
        this.childB = childB;
    }
}
