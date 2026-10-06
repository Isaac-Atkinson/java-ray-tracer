package com.example.raytracer.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    private static final double EPSILON = 1e-6;

    private final Vector a = new Vector (1, 2, 3);
    private final Vector b = new Vector (4, 5, 6);

    @Test
    void addsVectors() {
        assertVector(5, 7, 9, a.add(b));
    }

    @Test
    void subtractsVectors() {
        assertVector(-3, -3, -3, a.sub(b));
    }

    @Test
    void multipliesVectors() {
        assertVector(2,4,6, a.mul(2));
    }

    @Test
    void calculatesDotProduct() {
        assertEquals(32, a.dot(b), EPSILON);
    }

    @Test
    void calculatesMagnitude() {
        Vector v = new Vector(3, 4, 12); // magnitude 13

        assertEquals(13, v.magnitude(), EPSILON);
    }

    @Test
    void normalisesVector() {
        Vector v = new Vector(3, 4, 12); // magnitude 13

        v.normalise();

        assertVector(3.0 / 13, 4.0 / 13, 12.0 / 13, v);
    }

    @Test
    void normalisedVectorHasMagnitudeOne() {
        Vector v = new Vector(3, 4, 12);

        v.normalise();

        assertEquals(1.0, v.magnitude(), EPSILON);
    }

    @Test
    void calculatesCrossProduct() {
        assertVector(-3, 6, -3, a.cross(b));
    }

    @Test
    void crossProductIsPerpendicularToBothVectors() {
        Vector c = a.cross(b);
        assertEquals(0.0, c.dot(a), EPSILON);
        assertEquals(0.0, c.dot(b), EPSILON);
    }

    @Test
    void crossProductOfParallelVectorsIsZero() {
        assertVector(0, 0, 0, a.cross(a.mul(3)));
    }

    private static void assertVector(double x, double y, double z, Vector v){
        assertEquals(x, v.x, EPSILON);
        assertEquals(y, v.y, EPSILON);
        assertEquals(z, v.z, EPSILON);
    }

}