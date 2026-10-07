package com.example.raytracer.bvh;

import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.math.Intersection;
import com.example.raytracer.math.Ray;

import java.util.ArrayList;

/**
 * Represents a Bounding Volume Hierarchy.
 * This class is responsible for building a BVH and for
 * traversing the BVH.
 */
public class BVH {

    //The maximum recursive depth of the BVH
    private static final int MAX_TREE_DEPTH = 25;
    //The minimum number of objects per node
    private static final int MIN_OBJECTS = 8;

    private final ArrayList<SceneObject> objects;

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

    public Intersection closestHit(Ray ray){
        Intersection closest = new Intersection(null , Double.POSITIVE_INFINITY);
        return traverseBVH(root, ray, closest);
    }

    public boolean isOccluded(Ray ray, double distToLight){
        return isOccluded(root, ray, distToLight);
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
        if(depth >= MAX_TREE_DEPTH || node.getPrimitives().size() <= MIN_OBJECTS){
            node.setIsLeaf(true);
            return;
        }

        node.setChildA(new Node());;
        node.setChildB(new Node());

        Axis axis = node.getBoundingBox().longestAxis();

        node.sortPrimitives(axis);

        int mid = node.getPrimitives().size() / 2;

        for (int i = 0; i < node.getPrimitives().size(); i++) {
            if (i < mid) {
                node.getChildA().addObject(node.getPrimitives().get(i));
            } else {
                node.getChildB().addObject(node.getPrimitives().get(i));
            }
        }

        node.getChildA().constructBoundingBox();
        node.getChildB().constructBoundingBox();
        node.getPrimitives().clear();

        split(node.getChildA(), depth + 1);
        split(node.getChildB(), depth + 1);
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

        if(t == Double.POSITIVE_INFINITY || t > closest.t){
            return closest;
        }

        if (node.isLeaf()) {

            for (SceneObject object : node.getPrimitives()) {
                Intersection hit = object.intersect(ray);

                if (hit != null) {
                    if (hit.t < closest.t) closest = hit;
                }
            }
        } else {
            double tA = rayIntersectsBox(ray, node.getChildA());
            double tB = rayIntersectsBox(ray, node.getChildB());

            Node nearest;
            Node farthest;

            double tNear;
            double tFar;

            if (tA < tB) {
                nearest = node.getChildA();
                farthest = node.getChildB();

                tNear = tA;
                tFar = tB;
            } else {
                nearest = node.getChildB();
                farthest = node.getChildA();

                tNear = tB;
                tFar = tA;
            }

            if (tNear < closest.t) {
                closest = traverseBVH(nearest, ray, closest);
            }

            if (tFar < closest.t) {
                closest = traverseBVH(farthest, ray, closest);
            }
        }

        return closest;
    }

    /**
     * Traverses the BVH to determine if a ray intersects any scene geometry
     * before reaching the light source.
     * @param node the current node being traversed
     * @param ray the ray being tested for intersections
     * @param distToLight the distance to the light source
     * @return true if any geometry blocks the ray before the light, false otherwise
     */
    private boolean isOccluded(Node node, Ray ray, double distToLight){
        double t = rayIntersectsBox(ray, node);

        if(t == Double.POSITIVE_INFINITY){
            return false;
        }

        if(t > distToLight){
            return false;
        }



        if (node.isLeaf()) {

            for (SceneObject object : node.getPrimitives()) {
                Intersection hit = object.intersect(ray);

                if (hit != null) {
                    if (hit.t < distToLight) return true;
                }
            }

            return false;
        }


        if(isOccluded(node.getChildA(), ray, distToLight)){
            return true;
        }

        return isOccluded(node.getChildB(), ray, distToLight);
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

        BoundingBox box = node.getBoundingBox();

        double tLowX = (box.getMinValues().x - originX) / dirX;
        double tHighX = (box.getMaxValues().x - originX) / dirX;
        double tCloseX = Math.min(tLowX, tHighX);
        double tFarX = Math.max(tLowX, tHighX);

        double tLowY = (box.getMinValues().y - originY) / dirY;
        double tHighY = (box.getMaxValues().y - originY) / dirY;
        double tCloseY = Math.min(tLowY, tHighY);
        double tFarY = Math.max(tLowY, tHighY);

        double tLowZ = (box.getMinValues().z - originZ) / dirZ;
        double tHighZ = (box.getMaxValues().z - originZ) / dirZ;
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
