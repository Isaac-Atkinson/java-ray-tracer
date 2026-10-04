package com.example.raytracer.bvh;

import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.helper.Intersection;
import com.example.raytracer.helper.Ray;

import java.util.ArrayList;

/**
 * Represents a Bounding Volume Hierarchy.
 * This class is responsible for building a BVH and for
 * traversing the BVH.
 */
public class BVH {

    private static final int MAX_TREE_DEPTH = 25; //The maximum recursive depth of the BVH
    private static final int MIN_OBJECTS = 8; //The minimum number of objects per node

    private ArrayList<SceneObject> objects;
    private Node root;

    public BVH(ArrayList<SceneObject> objects){
        this.objects = new ArrayList<>(objects);
        constructBVH();
    }

    public void constructBVH(){
        root = new Node(objects);
        root.constructBoundingBox();
        split(root, 0);
    }

    /**
     * Splits a node into two child nodes and assigns primitives to
     * each child node.
     * Terminates if maximum tree depth reached or number of primitives
     * in node less than or equal to the minimum number of primitives
     * per node.
     * @param node the current node being split
     * @param depth the current depth in the tree the given node sits
     */
    private void split(Node node, int depth){
        if(depth == MAX_TREE_DEPTH || node.primitives.size() <= MIN_OBJECTS){
            node.isLeaf = true;
            return;
        }

        node.childA = new Node();
        node.childB = new Node();

        Axis axis = node.boundingBox.longestAxis();

        node.sortPrimitives(axis);

        int mid = node.primitives.size() / 2;

        for (int i = 0; i < node.primitives.size(); i++) {
            if (i < mid) {
                node.childA.addObject(node.primitives.get(i));
            } else {
                node.childB.addObject(node.primitives.get(i));
            }
        }

        node.childA.constructBoundingBox();
        node.childB.constructBoundingBox();
        node.primitives.clear();

        split(node.childA, depth + 1);
        split(node.childB, depth + 1);
    }

    public Intersection traverseBVH(Ray ray){
        Intersection closest = new Intersection(null , Double.POSITIVE_INFINITY);
        return traverseBVH(root, ray, closest);
    }

    public boolean isOccluded(Ray ray, double distToLight){
        return isOccluded(root, ray, distToLight);
    }

    /**
     * Traverses the BVH to find the closest intersection of a ray
     * with scene geometry.
     * @param node the current node being traversed
     * @param ray the ray being tested for intersections
     * @param closest the current closest intersection found during traversal
     * @return an Intersection containing information about the closest hit
     */
    private Intersection traverseBVH(Node node, Ray ray, Intersection closest) {
        double t = rayIntersectsBox(ray, node);

        if (t < Double.POSITIVE_INFINITY) {
            if(t > closest.t) return closest;

            if (node.isLeaf) {

                for (SceneObject object : node.primitives) {
                    Intersection hit = object.intersect(ray);

                    if (hit != null) {
                        if (hit.t < closest.t) closest = hit;
                    }
                }
            } else {
                double tA = rayIntersectsBox(ray, node.childA);
                double tB = rayIntersectsBox(ray, node.childB);

                Node nearest;
                Node farthest;

                double tNear;
                double tFar;

                if(tA < tB){
                    nearest = node.childA;
                    farthest = node.childB;

                    tNear = tA;
                    tFar = tB;
                } else {
                    nearest = node.childB;
                    farthest = node.childA;

                    tNear = tB;
                    tFar = tA;
                }

                if(tNear < closest.t){
                    closest = traverseBVH(nearest, ray, closest);
                }

                if(tFar < closest.t){
                    closest = traverseBVH(farthest, ray, closest);
                }
            }
        }
        return closest;
    }

    private boolean isOccluded(Node node, Ray ray, double distToLight){
        double t = rayIntersectsBox(ray, node);

        if(t == Double.POSITIVE_INFINITY){
            return false;
        }

        if(t > distToLight){
            return false;
        }



        if (node.isLeaf) {

            for (SceneObject object : node.primitives) {
                Intersection hit = object.intersect(ray);

                if (hit != null) {
                    if (hit.t < distToLight) return true;
                }
            }

            return false;
        }


        if(isOccluded(node.childA, ray, distToLight)){
            return true;
        }

        return isOccluded(node.childB, ray, distToLight);
    }

    /**
     * Determines if a ray intersects a bounding box.
     * @param ray the ray being tested
     * @param node the node that contains the bounding box being tested
     * @return a double representing the distance t along the ray it
     * intersects the box, or positive infinity if no intersection occurs.
     */
    private double rayIntersectsBox(Ray ray, Node node) {
        double originX = ray.origin.x, originY = ray.origin.y, originZ = ray.origin.z;
        double dirX = ray.direction.x, dirY = ray.direction.y, dirZ = ray.direction.z;

        BoundingBox box = node.boundingBox;

        double tLowX = (box.minValues.x - originX) / dirX;
        double tHighX = (box.maxValues.x - originX) / dirX;
        double tCloseX = Math.min(tLowX, tHighX);
        double tFarX = Math.max(tLowX, tHighX);

        double tLowY = (box.minValues.y - originY) / dirY;
        double tHighY = (box.maxValues.y - originY) / dirY;
        double tCloseY = Math.min(tLowY, tHighY);
        double tFarY = Math.max(tLowY, tHighY);

        double tLowZ = (box.minValues.z - originZ) / dirZ;
        double tHighZ = (box.maxValues.z - originZ) / dirZ;
        double tCloseZ = Math.min(tLowZ, tHighZ);
        double tFarZ = Math.max(tLowZ, tHighZ);

        double tEntry = Math.max(tCloseX, Math.max(tCloseY, tCloseZ));
        double tExit = Math.min(tFarX, Math.min(tFarY, tFarZ));

        if (tEntry <= tExit && tExit > 0) {
            return tEntry;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public void addObjects(ArrayList<SceneObject> newObjects){
        objects.addAll(newObjects);
    }

    public void clearObjects(){
        objects.clear();
    }
}
