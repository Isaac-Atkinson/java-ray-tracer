package com.example.raytracer.math;

/**
 * represents a 3D vector.
 * Vectors can represent a point in space or a direction.
 */
public class Vector {

    public double x;
    public double y;
    public double z;


    public Vector () {}

    public Vector (double x , double y , double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * adds the given vector to this one
     * @param other the other vector
     * @return the new vector after addition
     */
    public Vector add (Vector other) {
        return new Vector (x + other.x , y + other.y , z + other.z );
    }

    /**
     * Subtracts the given vector from this one
     * @param other the other vector
     * @return the new vector after subtraction
     */
    public Vector sub (Vector other) {
        return new Vector (x - other.x , y - other.y , z - other.z );
    }

    /**
     * Multiplies this vector by a scalar
     * @param val the scalar
     * @return the new vector after multiplication
     */
    public Vector mul (double val) {
        return new Vector (val * x , val * y , val * z );
    }

    /**
     * Performs the dot product between this vector and the given vector
     * @param other the other vector
     * @return the dot product between the two vectors
     */
    public double dot (Vector other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    public double magnitude () {
        return Math.sqrt(x * x + y * y + z * z) ;
    }

    public void normalise () {
        double mag = magnitude ();

        if (mag !=0) {
            x /= mag;
            y /= mag;
            z /= mag;
        }
    }

    /**
     * Performs cross product between this vector and the given vector
     * @param other the other vector
     * @return the cross product between the two vectors
     */
    public Vector cross(Vector other) {
        return new Vector(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x );
    }

    public String toString () {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
