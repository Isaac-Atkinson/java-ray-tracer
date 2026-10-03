package com.example.raytracer.UI;

import com.example.raytracer.geometry.Model;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.helper.Camera;
import com.example.raytracer.helper.Vector;
import javafx.concurrent.Task;
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

    private Renderer renderer;
    private RenderScene renderScene;

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

        //Initialise image
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
        renderScene =  new RenderScene();
        renderer = new Renderer(image, camera, renderScene);





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

        ColorPicker lightColorPicker = new ColorPicker(renderScene.getLight().getColor());

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
                defaultControls
        );






        //Load initial scene
        File defaultScenefile = null;
        try {
            defaultScenefile = new File(getClass().getResource("/com/example/raytracer/models/bun_zipper.ply").toURI());
        } catch (Exception ex) {
            throw new RuntimeException("Model could not be loaded");
        }
        loadModelAndRender(defaultScenefile, 2600, new Vector(50,-250,0), controlsPanel);







        //Initialise JavaFX scene
        BorderPane root = new BorderPane();
        root.setLeft(controlsPanel);
        root.setRight(view);

        GridPane.setHgrow(view, Priority.ALWAYS);
        GridPane.setVgrow(view, Priority.ALWAYS);


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


        stage.setTitle("Ray Tracer");
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

            loadModelAndRender(file, 2600, new Vector(50,-250,0), controlsPanel);
        });

        dragonButton.setOnAction(e -> {

            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/dragon_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            loadModelAndRender(file,2800, new Vector(10,-300,0), controlsPanel);
        });

        buddhaButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/happy_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            loadModelAndRender(file,2800, new Vector(0,-400,0), controlsPanel);
        });






        //Camera actions
        cameraDistanceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraDistanceSlider.getValue();
                camera.setRadius(finalValue);
                startRender(controlsPanel);
            }
        });

        cameraYawSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraYawSlider.getValue();
                camera.setYaw(finalValue);
                startRender(controlsPanel);
            }
        });

        cameraPitchSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraPitchSlider.getValue();
                camera.setPitch(finalValue);
                startRender(controlsPanel);
            }
        });

        cameraFovSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraFovSlider.getValue();
                camera.setFov(finalValue);
                startRender(controlsPanel);
            }
        });







        //model actions
        modelColorPicker.setOnAction(e -> {
            Color color = modelColorPicker.getValue();

            currentModel.setColor(color);

            currentColor = color;

            startRender(controlsPanel);

        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                currentModel.setShininess(finalValue);

                currentShininess = finalValue;

                startRender(controlsPanel);
            }
        });








        //light actions
        lightColorPicker.setOnAction(e -> {
            Color color = lightColorPicker.getValue();

            renderScene.getLight().setColor(color);
            startRender(controlsPanel);
        });


        lightXAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightXAxisSlider.getValue();
                renderScene.getLight().setXPos(-finalValue);
                startRender(controlsPanel);
            }
        });

        lightYAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightYAxisSlider.getValue();
                renderScene.getLight().setYPos(finalValue);
                startRender(controlsPanel);
            }
        });

        lightZAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightZAxisSlider.getValue();
                renderScene.getLight().setZPos(finalValue);
                startRender(controlsPanel);
            }
        });








        //shadow actions
        shadowQualityLowButton.setOnAction(e -> {
            renderer.setShadowQualityLow();
            startRender(controlsPanel);
        });
        shadowQualityHighButton.setOnAction(e -> {
            renderer.setShadowQualityHigh();
            startRender(controlsPanel);
        });
        shadowQualityVeryHighButton.setOnAction(e -> {
            renderer.setShadowQualityVeryHigh();
            startRender(controlsPanel);
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
            renderScene.getLight().setColor(Color.WHITE);

            lightXAxisSlider.setValue(defaultCameraX);
            renderScene.getLight().setXPos(defaultCameraX);
            lightYAxisSlider.setValue(defaultCameraY);
            renderScene.getLight().setYPos(defaultCameraY);
            lightZAxisSlider.setValue(defaultCameraZ);
            renderScene.getLight().setZPos(defaultCameraZ);

            renderer.setShadowQualityLow();

            startRender(controlsPanel);

        });
    }






    private void startRender(VBox controlsPanel){
        Task<Void> renderTask = new Task<>() {
            @Override
            protected Void call() {
                renderer.render();
                return null;
            }
        };

        controlsPanel.setDisable(true);

        renderTask.setOnSucceeded(e -> {
            controlsPanel.setDisable(false);
        });

        renderTask.setOnFailed(e -> {
            controlsPanel.setDisable(false);

            renderTask.getException().printStackTrace();
        });



        Thread thread = new Thread(renderTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadModelAndRender(
            File file,
            double scale,
            Vector position,
            VBox controlsPanel) {

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() {

                loadModel(file, scale, position);

                renderer.render();

                return null;
            }
        };

        controlsPanel.setDisable(true);

        task.setOnSucceeded(e -> {
            controlsPanel.setDisable(false);
        });

        task.setOnFailed(e -> {

            controlsPanel.setDisable(false);

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }



    private void loadModel(File file, double scale, Vector offset){
        renderScene.clearObjects();

        if(file.exists()) {
            ArrayList<SceneObject> modelTriangles =
                    getModelTriangles(
                            file,
                            scale,
                            offset
                    );

            Model model = new Model(modelTriangles);
            model.setColor(currentColor);
            model.setShininess(currentShininess);
            renderScene.addObjects(model.getTriangles());

            currentModel = model;
        }
    }


    private ArrayList<SceneObject> getModelTriangles(File file, double scale, Vector offset){
        return plyReader.readPLYFile(
                file,
                scale,
                offset
        );
    }
}





