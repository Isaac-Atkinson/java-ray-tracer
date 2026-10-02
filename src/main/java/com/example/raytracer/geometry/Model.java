package com.example.raytracer.geometry;

import javafx.scene.effect.Light;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Model {

    private static final double AMBIENT_FACTOR = 0.1;

    private static final Color SPECULAR_COLOR = Color.color(0.95, 0.95, 0.95);

    private ArrayList<SceneObject> triangles = new ArrayList<SceneObject>();


    public Model(ArrayList<SceneObject> triangles) {
        this.triangles = triangles;
        setSpecular();
    }

    public ArrayList<SceneObject> getTriangles() {
        return triangles;
    }

    public void setShininess(double shininess) {
        for(SceneObject obj: triangles){
            obj.setShininess(shininess);
        }
    }

    public void setColor(Color color) {
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();

        Color ambient = Color.color(
                red * AMBIENT_FACTOR,
                green * AMBIENT_FACTOR,
                blue *  AMBIENT_FACTOR
        );


        for(SceneObject obj: triangles){
            obj.setDiffuse(color);
            obj.setAmbient(ambient);
        }
    }

    private void setSpecular() {
        for(SceneObject obj: triangles){
            obj.setSpecular(SPECULAR_COLOR);
        }
    }
}
