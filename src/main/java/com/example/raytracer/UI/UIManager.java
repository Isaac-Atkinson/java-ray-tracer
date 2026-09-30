package com.example.raytracer.UI;

import com.example.raytracer.geometry.Plane;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.helper.Camera;
import com.example.raytracer.helper.LightSource;
import com.example.raytracer.helper.Vector;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.raytracer.render.PLYReader;
import com.example.raytracer.render.RenderScene;
import com.example.raytracer.render.Renderer;

import java.io.File;


public class UIManager {

    int imageWidth = 1000;
    int imageHeight = 1000;

    Renderer renderer;

    private final Stage stage;

    private final Vector cameraLookAt = new Vector(0,0,0);
    private final double defaultRadius = 600;
    private final double defaultYaw = 180;
    private final double defaultPitch = 0;
    private final double defaultFov = 60;

    private double defaultShininess = 32;


    public UIManager(Stage stage) {
        this.stage = stage;
        initialise();
    }

    private void initialise(){
        stage.setTitle("Ray Tracer");

        WritableImage image = new WritableImage(imageWidth, imageHeight);
        ImageView view = new ImageView(image);

        Camera camera = new Camera(
                cameraLookAt,
                defaultRadius,
                defaultYaw,
                defaultPitch,
                defaultFov
        );



        //Initialise RenderScene and Renderer classes
        RenderScene sc =  new RenderScene();
        renderer = new Renderer(image, camera, sc);
        renderer.render();




        //Initialise UI elements


        //Initialise scene loading controls
        Button bunnyButton = new Button("Bunny");
        Button dragonButton = new Button("Dragon");
        Button buddhaButton = new Button("Buddha");

        HBox modelButtons = new HBox(10,
                bunnyButton,
                dragonButton,
                buddhaButton);

        VBox modelControls = new VBox(10,
                new Label("Model"),
                modelButtons
        );

        //Initialise light controls
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

        VBox lightControls = new VBox(10,
                new Label("Light X Position"),
                lightXAxisSlider,

                new Label("Light Y Position"),
                lightYAxisSlider,


                new Label("Light Z Position"),
                lightZAxisSlider
        );

        //Initialise shininess controls
        Slider shininessSlider = new Slider(15, 100, defaultShininess);
        shininessSlider.setMajorTickUnit(1);
        shininessSlider.setMinorTickCount(0);
        shininessSlider.setSnapToTicks(true);

        VBox materialControls = new VBox(10,
                new Label("Shininess"),
                shininessSlider);

        //Initialise camera controls
        Slider cameraDistanceSlider = new Slider(200,1000,defaultRadius);
        Slider cameraYawSlider = new Slider(-180,180,defaultYaw);
        Slider cameraPitchSlider = new Slider(-89,89,defaultPitch);
        Slider cameraFovSlider = new Slider(20,100,defaultFov);

        VBox cameraControls = new VBox(10,
                new Label("Camera Controls"),
                new Label("X"),
                cameraYawSlider,
                new Label("Y"),
                cameraPitchSlider,
                new Label("Distance"),
                cameraDistanceSlider,
                new Label("FOV"),
                cameraFovSlider);


        //Intialise shadow controls
        Button sampleCountUpButton = new Button("Increase");
        Button sampleCountDownButton = new Button("Decrease");

        Label sampleCountLabel = new Label("Shadow sample count: " + renderer.getSampleCount());

        HBox sampleButtons = new HBox(10,
                sampleCountDownButton,
                sampleCountUpButton);

        VBox sampleControls = new VBox(10,
                sampleCountLabel,
                sampleButtons
        );




        VBox controlsPanel = new VBox(15,
                new Separator(),
                modelControls,
                new Separator(),
                lightControls,
                new Separator(),
                sampleControls,
                new Separator(),
                materialControls,
                new Separator(),
                cameraControls
        );



        BorderPane root = new BorderPane();
        root.setLeft(controlsPanel);
        root.setRight(view);

        GridPane.setHgrow(view, Priority.ALWAYS);
        GridPane.setVgrow(view, Priority.ALWAYS);


        //Initialise JavaFX scene
        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

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
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setMaximized(true);
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

            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        defaultShininess,
                        2600,
                        new Vector(50, -250, 0)
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


            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        defaultShininess,
                        2800,
                        new Vector(10, -300, 0)
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

            if(file.exists()) {
                sc.addObjects(plyReader.readPLYFile(file,
                        Color.color(0.15,0.14,0.13),
                        Color.color(0.86,0.84,0.78),
                        Color.color(0.95,0.95,0.95),
                        defaultShininess,
                        2800,
                        new Vector(0, -400, 0)
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
                sc.getLight().setXPos(finalValue);
                renderer.render();
            }
        });

        lightYAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightYAxisSlider.getValue();
                sc.getLight().setYPos(finalValue);
                renderer.render();
            }
        });

        lightZAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightZAxisSlider.getValue();
                sc.getLight().setZPos(finalValue);
                renderer.render();
            }
        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                defaultShininess = finalValue;
                sc.setShininess(defaultShininess);
                renderer.render();
            }
        });

        cameraDistanceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraDistanceSlider.getValue();
                camera.setRadius(finalValue);
                renderer.render();
            }
        });

        cameraYawSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraYawSlider.getValue();
                camera.setYaw(finalValue);
                renderer.render();
            }
        });

        cameraPitchSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraPitchSlider.getValue();
                camera.setPitch(finalValue);
                renderer.render();
            }
        });

        cameraFovSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraFovSlider.getValue();
                camera.setFov(finalValue);
                renderer.render();
            }
        });


    }
}





