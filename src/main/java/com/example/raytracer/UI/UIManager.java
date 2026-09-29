package com.example.raytracer.UI;

import com.example.raytracer.geometry.Plane;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.geometry.Sphere;
import com.example.raytracer.helper.Intersection;
import com.example.raytracer.helper.LightSource;
import com.example.raytracer.helper.Vector;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.raytracer.render.PLYReader;
import com.example.raytracer.render.RenderScene;
import com.example.raytracer.render.Renderer;

import java.io.File;
import java.util.ArrayList;

public class UIManager {

    int imageWidth = 500;
    int imageHeight = 500;
    Renderer renderer;

    ArrayList<SceneObject> objects = new ArrayList<>();

    private final Stage stage;

    private double shininess = 32;


    public UIManager(Stage stage) {
        this.stage = stage;
        initialise();
    }

    private void initialise(){
        stage.setTitle("Ray Tracer");

        WritableImage image = new WritableImage(imageWidth, imageHeight);
        ImageView view = new ImageView(image);




        //Initialise initial objects in scene
        Vector lightPos = new Vector(0, 0, -300);
        Vector up = new Vector(0, 1, 0);
        Vector right = new Vector(1, 0, -1);
        LightSource light = new LightSource(
                lightPos,
                Color.color(1,1,1),
                20,
                20,
                right,
                up
        );

        Vector planeNormal1 = new Vector(0,0,-1);
        Vector pointOnPlane1 = new Vector(0,0,800);
        Plane plane = new Plane(planeNormal1, pointOnPlane1, Color.color(0.1,0.1,0.1), Color.color(0.7,0.7,0.7),
                Color.color(0,0,0), 0);
        plane.minVals = new Vector(-800, -800, 800);
        plane.maxVals = new Vector(800, 800, 800);
        plane.centre = new Vector(0,0,800);
        objects.add(plane);





        //Initialise RenderScene and Renderer classes
        RenderScene sc = new RenderScene(objects, light);
        renderer = new Renderer(image, sc);
        renderer.render();





        //Initialise UI elements
        Button sampleCountUpButton = new Button("Increase");
        Button sampleCountDownButton = new Button("Decrease");
        Button bunnyButton = new Button("Bunny");
        Button dragonButton = new Button("Dragon");
        Button buddhaButton = new Button("Buddha");

        Slider lightXAxisSlider = new Slider(-(imageWidth / 2), imageWidth / 2, 0);
        lightXAxisSlider.setMajorTickUnit(1);
        lightXAxisSlider.setMinorTickCount(0);
        lightXAxisSlider.setSnapToTicks(true);

        Slider lightYAxisSlider = new Slider(-(imageWidth / 2), imageWidth / 2, 0);
        lightYAxisSlider.setMajorTickUnit(1);
        lightYAxisSlider.setMinorTickCount(0);
        lightYAxisSlider.setSnapToTicks(true);

        Slider lightZAxisSlider = new Slider(-400, 0, -200);
        lightZAxisSlider.setMajorTickUnit(1);
        lightZAxisSlider.setMinorTickCount(0);
        lightZAxisSlider.setSnapToTicks(true);

        Slider shininessSlider = new Slider(15, 100, shininess);
        shininessSlider.setMajorTickUnit(1);
        shininessSlider.setMinorTickCount(0);
        shininessSlider.setSnapToTicks(true);

        Label sampleCountLabel = new Label("Shadow sample count: " + renderer.getSampleCount());

        VBox lightControls = new VBox(10,
                new Label("Light X Position"),
                lightXAxisSlider,

                new Label("Light Y Position"),
                lightYAxisSlider,


                new Label("Light Z Position"),
                lightZAxisSlider
        );

        HBox modelButtons = new HBox(10,
                bunnyButton,
                dragonButton,
                buddhaButton);

        HBox.setHgrow(bunnyButton, Priority.ALWAYS);
        HBox.setHgrow(dragonButton, Priority.ALWAYS);
        HBox.setHgrow(buddhaButton, Priority.ALWAYS);

        VBox modelControls = new VBox(10,
                new Label("Model"),
                modelButtons
        );

        HBox sampleButtons = new HBox(10,
                sampleCountDownButton,
                sampleCountUpButton);

        VBox sampleControls = new VBox(10,
                sampleCountLabel,
                sampleButtons
        );

        VBox materialControls = new VBox(10,
                new Label("Shininess"),
                shininessSlider);

        VBox controlsPanel = new VBox(15,
                new Separator(),
                modelControls,
                new Separator(),
                lightControls,
                new Separator(),
                sampleControls,
                new Separator(),
                materialControls
        );



        BorderPane root = new BorderPane();
        root.setLeft(controlsPanel);
        root.setRight(view);

        GridPane.setHgrow(view, Priority.ALWAYS);
        GridPane.setVgrow(view, Priority.ALWAYS);





        //Initialise JavaFX scene
        Scene scene = new Scene(root);

        view.setPreserveRatio(true);
        view.setSmooth(true);

        view.fitWidthProperty()
                .bind(scene.widthProperty()
                        .subtract(controlsPanel.widthProperty()));

        view.fitHeightProperty()
                .bind(scene.heightProperty());

        controlsPanel.prefWidthProperty()
                .bind(scene.widthProperty().multiply(0.3));

        stage.setScene(scene);
        stage.setMinWidth(750);
        stage.setMinHeight(550);
        stage.show();




        //Initialise actions
        bunnyButton.setOnAction(e -> {
            PLYReader plyReader = new PLYReader();
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/bun_zipper.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }



            sc.clearObjects();
            sc.addObjects(objects);

            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        shininess,
                        2600,
                        new Vector(50, -250, 200)
                ));
                renderer.render();
            }
        });

        dragonButton.setOnAction(e -> {
            PLYReader plyReader = new PLYReader();
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/dragon_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            sc.clearObjects();
            sc.addObjects(objects);

            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        shininess,
                        2800,
                        new Vector(10, -300, 200)
                ));
                renderer.render();



            }
        });

        buddhaButton.setOnAction(e -> {
            PLYReader plyReader = new PLYReader();
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/happy_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            sc.clearObjects();
            sc.addObjects(objects);

            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        shininess,
                        2800,
                        new Vector(0, -400, 200)
                ));
                renderer.render();
            }
        });

        sampleCountUpButton.setOnAction(e -> {
            renderer.increaseSampleCount();
            sampleCountLabel.setText("Shadow sample count: " + renderer.getSampleCount());
            renderer.render();
        });
        sampleCountDownButton.setOnAction(e -> {
            renderer.decreaseSampleCount();
            sampleCountLabel.setText("Shadow sample count: " + renderer.getSampleCount());
            renderer.render();
        });




        lightXAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightXAxisSlider.getValue();
                light.setXPos(finalValue);
                renderer.render();
            }
        });

        lightYAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightYAxisSlider.getValue();
                light.setYPos(finalValue);
                renderer.render();
            }
        });

        lightZAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightZAxisSlider.getValue();
                light.setZPos(finalValue);
                renderer.render();
            }
        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                shininess = finalValue;
                sc.setShininess(shininess);
                renderer.render();
            }
        });
    }
}





