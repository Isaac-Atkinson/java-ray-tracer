package com.example.raytracer.UI;

import com.example.raytracer.geometry.Model;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.helper.Camera;
import com.example.raytracer.helper.Vector;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.raytracer.render.PLYReader;
import com.example.raytracer.render.RenderScene;
import com.example.raytracer.render.Renderer;

import java.io.File;
import java.util.ArrayList;


public class UIManager {

    int imageWidth = 1000;
    int imageHeight = 1000;

    Renderer renderer;

    private final Stage stage;

    private final PLYReader plyReader = new PLYReader();

    Model currentModel;

    private final Color defaultColor = Color.color(0.86,0.84,0.78);
    private final double defaultShininess = 32;

    private Color currentColor = defaultColor;
    private double currentShininess = defaultShininess;

    private final Vector cameraLookAt = new Vector(0,0,0);
    private final double defaultRadius = 1000;
    private final double defaultYaw = 180;
    private final double defaultPitch = 0;
    private final double defaultFov = 80;



    private final double defaultCameraX = 0;
    private final double defaultCameraY = 0;
    private final double defaultCameraZ = -400;


    public UIManager(Stage stage) {
        this.stage = stage;
        initialise();
    }

    private void initialise(){
        stage.setTitle("Ray Tracer");

        WritableImage image = new WritableImage(imageWidth, imageHeight);
        ImageView view = new ImageView(image);

        //Initialise camera
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

        //Initialise model controls
        Slider shininessSlider = new Slider(5, 100, defaultShininess);
        shininessSlider.setMajorTickUnit(1);
        shininessSlider.setMinorTickCount(0);
        shininessSlider.setSnapToTicks(true);

        ColorPicker modelColorPicker = new ColorPicker(defaultColor);

        VBox materialControls = new VBox(10,
                new Label("Model Material"),
                new Label("Model Colour"),
                modelColorPicker,
                new Label("Shininess"),
                shininessSlider);



        //Initialise light controls

        ColorPicker lightColorPicker = new ColorPicker(sc.getLight().getColor());

        Slider lightXAxisSlider = new Slider(-500, 500, 0);
        lightXAxisSlider.setMajorTickUnit(1);
        lightXAxisSlider.setMinorTickCount(0);
        lightXAxisSlider.setSnapToTicks(true);

        Slider lightYAxisSlider = new Slider(-500, 500, 0);
        lightYAxisSlider.setMajorTickUnit(1);
        lightYAxisSlider.setMinorTickCount(0);
        lightYAxisSlider.setSnapToTicks(true);

        Slider lightZAxisSlider = new Slider(-400, -100, -400);
        lightZAxisSlider.setMajorTickUnit(1);
        lightZAxisSlider.setMinorTickCount(0);
        lightZAxisSlider.setSnapToTicks(true);

        VBox lightControls = new VBox(10,
                new Label("Light colour"),
                lightColorPicker,
                new Label("Light X Position"),
                lightXAxisSlider,

                new Label("Light Y Position"),
                lightYAxisSlider,


                new Label("Light Z Position"),
                lightZAxisSlider
        );


        //Intialise shadow controls
        Button shadowQualityLowButton = new Button("Low");
        Button shadowQualityHighButton = new Button("High");
        Button shadowQualityVeryHighButton = new Button("Very High");


        Label sampleCountLabel = new Label("Shadow quality");

        HBox sampleButtons = new HBox(10,
                shadowQualityLowButton,
                shadowQualityHighButton,
                shadowQualityVeryHighButton);

        VBox sampleControls = new VBox(10,
                sampleCountLabel,
                sampleButtons
        );

        //Initialise default controls

        Button defaultSettingsButton = new Button("Default");

        VBox defaultControls = new VBox(10,
                new Label("Default Settings"),
                defaultSettingsButton
        );




        VBox controlsPanel = new VBox(15,
                new Separator(),
                modelControls,
                new Separator(),
                cameraControls,
                new Separator(),
                materialControls,
                new Separator(),
                lightControls,
                new Separator(),
                sampleControls,
                new Separator(),
                defaultSettingsButton
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

        //Scene loading actions
        bunnyButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/bun_zipper.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            sc.clearObjects();

            if(file.exists()) {
                ArrayList<SceneObject> modelTriangles =
                        getModelTriangles(
                                file,
                                2600,
                                new Vector(50, -250, 0)
                        );

                Model model = new Model(modelTriangles);
                model.setColor(currentColor);
                model.setShininess(currentShininess);
                sc.addObjects(model.getTriangles());

                currentModel = model;

                renderer.render();
            }
        });

        dragonButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/dragon_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            sc.clearObjects();

            if (file.exists()) {
                ArrayList<SceneObject> modelTriangles =
                        getModelTriangles(
                                file,
                                2800,
                                new Vector(10, -300, 0)
                        );

                Model model = new Model(modelTriangles);
                model.setColor(currentColor);
                model.setShininess(currentShininess);
                sc.addObjects(model.getTriangles());

                currentModel = model;

                renderer.render();
            }
        });

        buddhaButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/happy_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            sc.clearObjects();

            if(file.exists()) {
                ArrayList<SceneObject> modelTriangles =
                        getModelTriangles(
                                file,
                                2800,
                                new Vector(0, -400, 0)
                        );

                Model model = new Model(modelTriangles);
                model.setColor(currentColor);
                model.setShininess(currentShininess);
                sc.addObjects(model.getTriangles());

                currentModel = model;

                renderer.render();
            }
        });

        //Camera actions
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

        //model actions
        modelColorPicker.setOnAction(e -> {
            Color color = modelColorPicker.getValue();

            currentModel.setColor(color);

            currentColor = color;

            renderer.render();

        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                currentModel.setShininess(finalValue);

                currentShininess = finalValue;

                renderer.render();
            }
        });


        //light actions
        lightColorPicker.setOnAction(e -> {
            Color color = lightColorPicker.getValue();

            sc.getLight().setColor(color);
            renderer.render();
        });


        lightXAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightXAxisSlider.getValue();
                sc.getLight().setXPos(-finalValue);
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

        //shadow actions
        shadowQualityLowButton.setOnAction(e -> {
            renderer.setShadowQualityLow();
            renderer.render();
        });
        shadowQualityHighButton.setOnAction(e -> {
            renderer.setShadowQualityHigh();
            renderer.render();
        });
        shadowQualityVeryHighButton.setOnAction(e -> {
            renderer.setShadowQualityVeryHigh();
            renderer.render();
        });

        //Default actions
        defaultSettingsButton.setOnAction(e -> {
            cameraPitchSlider.setValue(defaultPitch);
            camera.setPitch(defaultPitch);

            cameraYawSlider.setValue(defaultYaw);
            camera.setYaw(defaultYaw);

            cameraDistanceSlider.setValue(defaultRadius);
            camera.setRadius(defaultRadius);

            cameraFovSlider.setValue(defaultFov);
            camera.setFov(defaultFov);

            modelColorPicker.setValue(defaultColor);
            currentModel.setColor(defaultColor);

            shininessSlider.setValue(defaultShininess);
            currentModel.setShininess(defaultShininess);

            lightColorPicker.setValue(Color.WHITE);
            sc.getLight().setColor(Color.WHITE);

            lightXAxisSlider.setValue(defaultCameraX);
            sc.getLight().setXPos(defaultCameraX);
            lightYAxisSlider.setValue(defaultCameraY);
            sc.getLight().setYPos(defaultCameraY);
            lightZAxisSlider.setValue(defaultCameraZ);
            sc.getLight().setZPos(defaultCameraZ);

            renderer.setShadowQualityLow();

            renderer.render();

        });
    }









    private ArrayList<SceneObject> getModelTriangles(File file, double scale, Vector offset){
        return plyReader.readPLYFile(
                file,
                scale,
                offset
        );
    }
}





