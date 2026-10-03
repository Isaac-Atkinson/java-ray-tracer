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

    private final int defaultResolution = 500;

    private int currentResolution = defaultResolution;

    private final int movingResolution = 250;

    private ImageView view;

    private Camera camera;

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

    private boolean renderInProgress = false;
    private boolean renderAgain = false;
    private boolean pendingFinalRender = false;
    private double requestedVal;


    public UIManager(Stage stage) {
        this.stage = stage;
        initialise();
    }

    private void initialise(){

        //Initialise image
        WritableImage image = new WritableImage(defaultResolution,defaultResolution);
        view = new ImageView(image);

        //Initialise camera
        camera = new Camera(
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
        Slider shininessSlider = new Slider(0, 100, defaultShininess);
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

        //Initialise resolution controls
        Label resolutionLabel = new Label("Resolution:" + currentResolution + " x " + currentResolution);

        Slider resolutionSlider = new Slider(300, 1000, defaultResolution);

        VBox resolutionControls = new VBox(10,
                resolutionLabel,
                resolutionSlider
        );

        //Initialise default controls

        Button defaultSettingsButton = new Button("Default");

        VBox defaultControls = new VBox(10,
                new Label("Default Settings"),
                defaultSettingsButton
        );

        //Initialise progress bar
        ProgressBar renderProgressBar = new ProgressBar();
        renderProgressBar.setVisible(false);
        renderProgressBar.setPrefWidth(300);
        renderProgressBar.setMaxWidth(Double.MAX_VALUE);






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
                resolutionControls,
                new Separator(),
                defaultControls,
                new Separator()

        );





        //Initialise JavaFX scene
        BorderPane root = new BorderPane();


        VBox renderArea = new VBox();

        VBox.setVgrow(view, Priority.ALWAYS);

        renderArea.getChildren().addAll(
                view,
                renderProgressBar
        );

        root.setLeft(controlsPanel);
        root.setRight(renderArea);



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
                .bind(renderArea.heightProperty()
                        .subtract(renderProgressBar.heightProperty()));

        controlsPanel.prefWidthProperty()
                .bind(scene.widthProperty().multiply(0.3));


        stage.setTitle("Ray Tracer");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.show();




        //Load initial scene
        File defaultScenefile = null;
        try {
            defaultScenefile = new File(getClass().getResource("/com/example/raytracer/models/bun_zipper.ply").toURI());
        } catch (Exception ex) {
            throw new RuntimeException("Model could not be loaded");
        }

        loadModelAndRender(
                defaultScenefile,
                2600,
                new Vector(50,-250,0),
                controlsPanel,
                renderProgressBar
        );



        //Initialise actions

        //Scene loading actions
        bunnyButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/bun_zipper.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            loadModelAndRender(
                    file,
                    2600,
                    new Vector(50,-250,0),
                    controlsPanel,
                    renderProgressBar
            );
        });

        dragonButton.setOnAction(e -> {

            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/dragon_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            loadModelAndRender(
                    file,
                    2600,
                    new Vector(50,-250,0),
                    controlsPanel,
                    renderProgressBar
            );
        });

        buddhaButton.setOnAction(e -> {
            File file = null;
            try {
                file = new File(getClass().getResource("/com/example/raytracer/models/happy_vrip.ply").toURI());
            } catch (Exception ex) {
                throw new RuntimeException("Model could not be loaded");
            }

            loadModelAndRender(
                    file,
                    2600,
                    new Vector(50,-250,0),
                    controlsPanel,
                    renderProgressBar
            );
        });






        //Camera actions
        cameraDistanceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraDistanceSlider.getValue();

                requestRender(true, () -> camera.setRadius(finalValue), controlsPanel,  renderProgressBar);
            }
        });

        cameraDistanceSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();
            requestRender(false, () -> camera.setRadius(requestedVal),  controlsPanel,  renderProgressBar);
        });

        cameraYawSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraYawSlider.getValue();

                requestRender(true, () -> camera.setYaw(finalValue),  controlsPanel,  renderProgressBar);
            }
        });

        cameraYawSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setYaw(requestedVal),   controlsPanel,  renderProgressBar);
        });

        cameraPitchSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraPitchSlider.getValue();

                requestRender(true, () -> camera.setPitch(finalValue),   controlsPanel,  renderProgressBar);
            }
        });

        cameraPitchSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setPitch(requestedVal), controlsPanel,  renderProgressBar);
        });

        cameraFovSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraFovSlider.getValue();

                requestRender(true, () -> camera.setFov(finalValue), controlsPanel,  renderProgressBar);
            }
        });

        cameraFovSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setFov(requestedVal), controlsPanel,  renderProgressBar);
        });







        //model actions
        modelColorPicker.setOnAction(e -> {
            Color color = modelColorPicker.getValue();

            currentModel.setColor(color);

            currentColor = color;

            startRender(controlsPanel, renderProgressBar);

        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                currentModel.setShininess(finalValue);

                currentShininess = finalValue;

                startRender(controlsPanel, renderProgressBar);
            }
        });









        //light actions
        lightColorPicker.setOnAction(e -> {
            Color color = lightColorPicker.getValue();

            renderScene.getLight().setColor(color);
            startRender(controlsPanel, renderProgressBar);
        });


        lightXAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightXAxisSlider.getValue();

                requestRender(true, () -> renderScene.getLight().setXPos(-finalValue), controlsPanel,  renderProgressBar);
            }
        });

        lightXAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightXAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setXPos(-requestedVal), controlsPanel,  renderProgressBar);
        });

        lightYAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightYAxisSlider.getValue();

                requestRender(true, () -> renderScene.getLight().setYPos(finalValue), controlsPanel,  renderProgressBar);
            }
        });

        lightYAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightYAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setYPos(requestedVal), controlsPanel,  renderProgressBar);
        });

        lightZAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightZAxisSlider.getValue();

                requestRender(true,  () -> renderScene.getLight().setZPos(finalValue), controlsPanel,  renderProgressBar);
            }
        });

        lightZAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightZAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setZPos(requestedVal), controlsPanel,  renderProgressBar);
        });








        //shadow actions
        shadowQualityLowButton.setOnAction(e -> {
            renderer.setShadowQualityLow();
            startRender(controlsPanel, renderProgressBar);
        });
        shadowQualityHighButton.setOnAction(e -> {
            renderer.setShadowQualityHigh();
            startRender(controlsPanel, renderProgressBar);
        });
        shadowQualityVeryHighButton.setOnAction(e -> {
            renderer.setShadowQualityVeryHigh();
            startRender(controlsPanel, renderProgressBar);
        });

        //Resolution actions
        resolutionSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = resolutionSlider.getValue();

                WritableImage newImage = new WritableImage((int) finalValue, (int) finalValue);
                view.setImage(newImage);
                renderer.setWritableImage(newImage);

                currentResolution = (int) finalValue;
                resolutionLabel.setText("Resolution: " + currentResolution + " x " + currentResolution);

                startRender(controlsPanel, renderProgressBar);
            }
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
            currentColor = defaultColor;

            shininessSlider.setValue(defaultShininess);
            currentModel.setShininess(defaultShininess);
            currentShininess = defaultShininess;

            lightColorPicker.setValue(Color.WHITE);
            renderScene.getLight().setColor(Color.WHITE);

            lightXAxisSlider.setValue(defaultCameraX);
            renderScene.getLight().setXPos(defaultCameraX);
            lightYAxisSlider.setValue(defaultCameraY);
            renderScene.getLight().setYPos(defaultCameraY);
            lightZAxisSlider.setValue(defaultCameraZ);
            renderScene.getLight().setZPos(defaultCameraZ);

            resolutionSlider.setValue(defaultResolution);
            currentResolution = defaultResolution;
            resolutionLabel.setText("Resolution: " + currentResolution + " x " + currentResolution);

            renderer.setShadowQualityLow();

            requestRender(true, () -> {}, controlsPanel, renderProgressBar);
        });
    }






    private void startRender(VBox controlsPanel, ProgressBar renderProgressBar) {
        Task<Void> renderTask = new Task<>() {
            @Override
            protected Void call() {
                renderer.render(progress -> updateProgress(progress, 1.0));
                return null;
            }
        };

        controlsPanel.setDisable(true);
        renderProgressBar.setVisible(true);
        renderProgressBar.progressProperty().bind(renderTask.progressProperty());

        renderTask.setOnSucceeded(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.setVisible(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);
        });

        renderTask.setOnFailed(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.setVisible(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);

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
            VBox controlsPanel,
            ProgressBar renderProgressBar) {

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() {

                loadModel(file, scale, position);

                renderer.render(progress -> updateProgress(progress, 1.0));

                return null;
            }
        };

        controlsPanel.setDisable(true);
        renderProgressBar.setVisible(true);
        renderProgressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.setVisible(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);
        });

        task.setOnFailed(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.setVisible(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);

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

    private void requestRender(boolean finalRender, Runnable update, VBox controlsPanel, ProgressBar renderProgressBar) {
        if(renderInProgress){
            renderAgain = true;

            if(finalRender){
                pendingFinalRender = true;
            }
            return;
        }


        renderInProgress = true;

        Runnable pendingCameraUpdate = update;

        WritableImage img;
        if(finalRender){
            img = new WritableImage(currentResolution, currentResolution);
        } else {
            img = new WritableImage(movingResolution, movingResolution);
        }

        renderer.setWritableImage(img);



        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                update.run();

                if (finalRender) {
                    renderer.render(progress -> updateProgress(progress, 1.0));
                } else {
                    renderer.render();
                }

                return null;
            }
        };

        if (finalRender) {
            controlsPanel.setDisable(true);

            renderProgressBar.setVisible(true);
            renderProgressBar.progressProperty()
                    .bind(task.progressProperty());
        }

        task.setOnSucceeded(e -> {
            view.setImage(img);

            renderInProgress = false;

            if (finalRender) {
                controlsPanel.setDisable(false);

                renderProgressBar.setVisible(false);
                renderProgressBar.progressProperty().unbind();
                renderProgressBar.setProgress(0);
            }

            if(pendingFinalRender){
                pendingFinalRender = false;
                renderAgain = false;

                requestRender(true, pendingCameraUpdate, controlsPanel, renderProgressBar);
            } else if(renderAgain){
                renderAgain = false;
                requestRender(false, pendingCameraUpdate, controlsPanel, renderProgressBar);
            }
        });

        task.setOnFailed(e -> {

            if (finalRender) {
                controlsPanel.setDisable(false);

                renderProgressBar.setVisible(false);
                renderProgressBar.progressProperty().unbind();
                renderProgressBar.setProgress(0);
            }

            renderInProgress = false;

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    private ArrayList<SceneObject> getModelTriangles(File file, double scale, Vector offset){
        return plyReader.readPLYFile(
                file,
                scale,
                offset
        );
    }
}





