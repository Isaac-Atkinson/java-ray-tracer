package com.example.raytracer.helper;

import java.util.ArrayList;
import javafx.scene.paint.Color;


/**
 * Represents a light source
 * Light sources are defined by their position, colour, width, height
 * and two direction vectors up and right defining its orientation.
 */
public class LightSource {

    private Vector pos;

    private double width;
    private double height;

    public Color color;

    private Vector right;
    private Vector up;


    public LightSource(Vector pos, Color color, double width, double height, Vector right, Vector up) {
        this.pos = pos;
        this.color = color;
        this.width = width;
        this.height = height;
        this.right = right;
        this.up = up;
    }


    /**
     * Returns a list of sample points across the light's surface.
     * Samples are generated in a grid pattern.
     * @return a list of sample points
     */
    public ArrayList<Vector> sampleLightSource(int sampleCount) {
        ArrayList<Vector> samples = new ArrayList<>();

        int n = (int) (Math.sqrt(sampleCount));

        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                double x = ((i + 0.5) / n * width) - (width / 2);
                double y = ((j + 0.5) / n * height) - (height / 2);

                Vector rightOffset = right.mul(x);
                Vector upOffset = up.mul(y);

                Vector sample = pos.add(rightOffset).add(upOffset);

                samples.add(sample);
            }
        }
        return samples;
    }

    public void setXPos(double xPos) {
        pos.x = xPos;
    }

    public void setYPos(double yPos) {
        pos.y = yPos;
    }

    public void setZPos(double zPos) {
        pos.z = zPos;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
