package com.example.raytracer.scene;

import com.example.raytracer.math.Vector;

public class Camera {

    private Vector lookAt;

    private Vector position;
    private Vector forward;
    private Vector right;
    private Vector up;

    private double radius;
    private double yaw;
    private double pitch;

    private double fov;

    public Camera(Vector lookAt, double radius, double yaw, double pitch, double fov) {
        this.lookAt = lookAt;
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.fov = fov;

        update();
    }

    public void update(){
        updatePosition();
        updateVectors();
    }

    private void updatePosition(){
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = lookAt.x
                + radius * Math.cos(pitchRad) * Math.sin(yawRad);

        double y = lookAt.y
                + radius * Math.sin(pitchRad);

        double z = lookAt.z
                + radius * Math.cos(pitchRad) * Math.cos(yawRad);

        position = new Vector(x, y, z);
    }

    private void updateVectors(){
        forward = lookAt.sub(position);
        forward.normalise();

        Vector worldUp = new Vector(0, 1, 0);

        right = forward.cross(worldUp);
        right.normalise();

        up = right.cross(forward);
        up.normalise();
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getForward() {
        return forward;
    }

    public Vector getRight() {
        return right;
    }

    public Vector getUp() {
        return up;
    }

    public double getFov() {
        return fov;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
        update();
    }

    public void setPitch(double pitch) {
        this.pitch = Math.max(-89, Math.min(89, pitch));
        update();
    }

    public void setRadius(double radius) {
        this.radius = radius;
        update();
    }

    public void setLookAt(Vector lookAt) {
        this.lookAt = lookAt;
        update();
    }

    public void setFov(double fov) {
        this.fov = fov;
        update();
    }


}
