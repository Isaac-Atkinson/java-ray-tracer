package com.example.raytracer.ui;

import com.example.raytracer.geometry.Model;
import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.scene.Camera;
import com.example.raytracer.math.Vector;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.raytracer.io.PLYReader;
import com.example.raytracer.scene.RenderScene;
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
    private final double defaultRadius = 600;
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

    private final String BUNNY_FILE = "/com/example/raytracer/models/bun_zipper.ply";
    private final String DRAGON_FILE = "/com/example/raytracer/models/dragon_vrip.ply";
    private final String BUDDHA_FILE = "/com/example/raytracer/models/happy_vrip.ply";

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

        ComboBox<String> modelSelector = new ComboBox<>();

        modelSelector.getItems().addAll(
                "Bunny",
                "Dragon",
                "Buddha"
        );

        modelSelector.setValue("Bunny");
        modelSelector.setMaxWidth(Double.MAX_VALUE);

        Label modelTitle = new Label("Model");
        modelTitle.getStyleClass().add("section-title");

        VBox modelSection = new VBox(6);
        modelSection.getChildren().addAll(
                modelTitle,
                modelSelector
        );

        modelSection.getStyleClass().add("controls-section");

        //Initialise camera controls
        Slider cameraDistanceSlider = new Slider(200,1000,defaultRadius);
        Slider cameraYawSlider = new Slider(0,360,defaultYaw);
        Slider cameraPitchSlider = new Slider(-89,89,defaultPitch);
        Slider cameraFovSlider = new Slider(20,100,defaultFov);

        Label cameraTitle = new Label("Camera Controls");
        cameraTitle.getStyleClass().add("section-title");

        VBox cameraSection = new VBox(10,
                cameraTitle,
                new Label("Yaw"),
                cameraYawSlider,
                new Label("Pitch"),
                cameraPitchSlider,
                new Label("Distance"),
                cameraDistanceSlider,
                new Label("FOV"),
                cameraFovSlider);

        cameraSection.getStyleClass().add("controls-section");

        //Initialise material controls
        Slider shininessSlider = new Slider(0, 100, defaultShininess);
        shininessSlider.setMajorTickUnit(1);
        shininessSlider.setMinorTickCount(0);
        shininessSlider.setSnapToTicks(true);

        ColorPicker modelColorPicker = new ColorPicker(defaultColor);

        Label materialTitle = new Label("Material Controls");
        materialTitle.getStyleClass().add("section-title");

        VBox materialSection = new VBox(10,
                materialTitle,
                new Label("Model Colour"),
                modelColorPicker,
                new Label("Shininess"),
                shininessSlider);

        materialSection.getStyleClass().add("controls-section");





        //Initialise light controls

        ColorPicker lightColorPicker = new ColorPicker(renderScene.getLight().getColor());

        Slider lightXAxisSlider = new Slider(-1000, 1000, 0);
        lightXAxisSlider.setMajorTickUnit(1);
        lightXAxisSlider.setMinorTickCount(0);
        lightXAxisSlider.setSnapToTicks(true);

        Slider lightYAxisSlider = new Slider(-1000, 1000, 0);
        lightYAxisSlider.setMajorTickUnit(1);
        lightYAxisSlider.setMinorTickCount(0);
        lightYAxisSlider.setSnapToTicks(true);

        Slider lightZAxisSlider = new Slider(-1000, -200, -400);
        lightZAxisSlider.setMajorTickUnit(1);
        lightZAxisSlider.setMinorTickCount(0);
        lightZAxisSlider.setSnapToTicks(true);

        Label lightTitle = new Label("Light Controls");
        lightTitle.getStyleClass().add("section-title");

        VBox lightSection = new VBox(10,
                lightTitle,
                new Label("Light colour"),
                lightColorPicker,
                new Label("Light X Position"),
                lightXAxisSlider,

                new Label("Light Y Position"),
                lightYAxisSlider,


                new Label("Light Z Position"),
                lightZAxisSlider
        );

        lightSection.getStyleClass().add("controls-section");


        //Intialise shadow controls
        Button shadowQualityLowButton = new Button("Low");
        Button shadowQualityHighButton = new Button("High");
        Button shadowQualityVeryHighButton = new Button("Very High");


        Label shadowTitle = new Label("Shadow quality");
        shadowTitle.getStyleClass().add("section-title");

        HBox sampleButtons = new HBox(10,
                shadowQualityLowButton,
                shadowQualityHighButton,
                shadowQualityVeryHighButton);

        VBox shadowSection = new VBox(10,
                shadowTitle,
                sampleButtons
        );

        shadowSection.getStyleClass().add("controls-section");

        //Initialise resolution controls
        ComboBox<String> resolutionSelector = new ComboBox<>();
        resolutionSelector.getItems().addAll(
                "300 x 300",
                "500 x 500",
                "750 x 750",
                "1000 x 1000"
        );

        resolutionSelector.setValue("500 x 500");

        Label resolutionTitle = new Label("Resolution");
        resolutionTitle.getStyleClass().add("section-title");

        VBox resolutionSection = new VBox(10,
                resolutionTitle,
                resolutionSelector
        );

        resolutionSection.getStyleClass().add("controls-section");

        //Initialise default controls

        Button defaultSettingsButton = new Button("Default");

        Label defaultSettingsTitle = new Label("Default Settings");
        defaultSettingsTitle.getStyleClass().add("section-title");

        VBox defaultSection = new VBox(10,
                defaultSettingsTitle,
                defaultSettingsButton
        );

        defaultSection.getStyleClass().add("controls-section");

        //Initialise progress bar
        ProgressBar renderProgressBar = new ProgressBar();
        //renderProgressBar.setVisible(false);
        renderProgressBar.setPrefWidth(300);
        renderProgressBar.setMaxWidth(Double.MAX_VALUE);







        VBox leftControlsPanel = new VBox(15,

                modelSection,

                cameraSection,

                lightSection,

                materialSection
        );

        leftControlsPanel.getStyleClass().add("controls-panel");

        leftControlsPanel.setPrefWidth(300);
        leftControlsPanel.setMinWidth(280);
        leftControlsPanel.setMaxWidth(340);

        VBox.setVgrow(modelSection, Priority.ALWAYS);
        VBox.setVgrow(cameraSection, Priority.ALWAYS);
        VBox.setVgrow(lightSection, Priority.ALWAYS);
        VBox.setVgrow(materialSection, Priority.ALWAYS);

        modelSection.setMaxHeight(Double.MAX_VALUE);
        cameraSection.setMaxHeight(Double.MAX_VALUE);
        lightSection.setMaxHeight(Double.MAX_VALUE);
        materialSection.setMaxHeight(Double.MAX_VALUE);

        HBox bottomControlsPanel = new HBox(15,

                shadowSection,

                resolutionSection,

                defaultSection
        );

        bottomControlsPanel.getStyleClass().add("controls-panel");

        bottomControlsPanel.setMaxHeight(Double.MAX_VALUE);

        HBox.setHgrow(shadowSection, Priority.ALWAYS);
        HBox.setHgrow(resolutionSection, Priority.ALWAYS);
        HBox.setHgrow(defaultSection, Priority.ALWAYS);

        shadowSection.setMaxWidth(Double.MAX_VALUE);
        resolutionSection.setMaxWidth(Double.MAX_VALUE);
        defaultSection.setMaxWidth(Double.MAX_VALUE);






        //Initialise JavaFX scene
        BorderPane root = new BorderPane();


        VBox renderArea = new VBox();

        VBox.setVgrow(view, Priority.ALWAYS);

        renderArea.getChildren().addAll(
                view,
                renderProgressBar
        );


        VBox rightSide = new VBox();

        VBox.setVgrow(renderArea, Priority.ALWAYS);

        rightSide.getChildren().addAll(
                renderArea,
                bottomControlsPanel
        );

        root.setLeft(leftControlsPanel);
        root.setCenter(rightSide);




        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        view.setPreserveRatio(true);
        view.setSmooth(true);

        view.fitWidthProperty()
                .bind(renderArea.widthProperty());

        view.fitHeightProperty()
                .bind(renderArea.heightProperty()
                        .subtract(renderProgressBar.heightProperty()));





        stage.setTitle("Ray Tracer");
        stage.setScene(scene);
        stage.setMinWidth(1010);
        stage.setMinHeight(890);
        stage.setResizable(false);
        stage.show();




        //Load initial scene
        loadModelAndRender(
                BUNNY_FILE,
                2600,
                new Vector(50,-250,0),
                leftControlsPanel,
                renderProgressBar
        );



        //Initialise actions

        //Scene loading actions

        modelSelector.setOnAction(event -> {
            String selectedModel = modelSelector.getValue();

            switch (selectedModel) {
                case "Bunny" -> {
                    loadModelAndRender(
                            BUNNY_FILE,
                            3000,
                            new Vector(50,-250,0),
                            leftControlsPanel,
                            renderProgressBar
                    );
                }

                case "Dragon" -> {
                    loadModelAndRender(
                            DRAGON_FILE,
                            3000,
                            new Vector(0,-350,-50),
                            leftControlsPanel,
                            renderProgressBar
                    );
                }

                case "Buddha" -> {
                    loadModelAndRender(
                            BUDDHA_FILE,
                            3000,
                            new Vector(20,-350,0),
                            leftControlsPanel,
                            renderProgressBar
                    );
                }
            }

        });


        //Camera actions
        cameraDistanceSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraDistanceSlider.getValue();

                requestRender(true, () -> camera.setRadius(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        cameraDistanceSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();
            requestRender(false, () -> camera.setRadius(requestedVal), leftControlsPanel,  renderProgressBar);
        });

        cameraYawSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraYawSlider.getValue();

                requestRender(true, () -> camera.setYaw(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        cameraYawSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setYaw(requestedVal), leftControlsPanel,  renderProgressBar);
        });

        cameraPitchSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraPitchSlider.getValue();

                requestRender(true, () -> camera.setPitch(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        cameraPitchSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setPitch(requestedVal), leftControlsPanel,  renderProgressBar);
        });

        cameraFovSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = cameraFovSlider.getValue();

                requestRender(true, () -> camera.setFov(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        cameraFovSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            requestedVal = newValue.doubleValue();

            requestRender(false, () -> camera.setFov(requestedVal), leftControlsPanel,  renderProgressBar);
        });







        //model actions
        modelColorPicker.setOnAction(e -> {
            Color color = modelColorPicker.getValue();

            currentModel.setColor(color);

            currentColor = color;

            startRender(leftControlsPanel, renderProgressBar);

        });

        shininessSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = shininessSlider.getValue();
                currentModel.setShininess(finalValue);

                currentShininess = finalValue;

                startRender(leftControlsPanel, renderProgressBar);
            }
        });









        //light actions
        lightColorPicker.setOnAction(e -> {
            Color color = lightColorPicker.getValue();

            renderScene.getLight().setColor(color);
            startRender(leftControlsPanel, renderProgressBar);
        });


        lightXAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightXAxisSlider.getValue();

                requestRender(true, () -> renderScene.getLight().setXPos(-finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        lightXAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightXAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setXPos(-requestedVal), leftControlsPanel,  renderProgressBar);
        });

        lightYAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightYAxisSlider.getValue();

                requestRender(true, () -> renderScene.getLight().setYPos(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        lightYAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightYAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setYPos(requestedVal), leftControlsPanel,  renderProgressBar);
        });

        lightZAxisSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                double finalValue = lightZAxisSlider.getValue();

                requestRender(true,  () -> renderScene.getLight().setZPos(finalValue), leftControlsPanel,  renderProgressBar);
            }
        });

        lightZAxisSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!lightZAxisSlider.isValueChanging()) {
                return;
            }

            requestedVal = newValue.doubleValue();

            requestRender(false, () -> renderScene.getLight().setZPos(requestedVal), leftControlsPanel,  renderProgressBar);
        });








        //shadow actions
        shadowQualityLowButton.setOnAction(e -> {
            renderer.setShadowQualityLow();
            startRender(leftControlsPanel, renderProgressBar);
        });
        shadowQualityHighButton.setOnAction(e -> {
            renderer.setShadowQualityHigh();
            startRender(leftControlsPanel, renderProgressBar);
        });
        shadowQualityVeryHighButton.setOnAction(e -> {
            renderer.setShadowQualityVeryHigh();
            startRender(leftControlsPanel, renderProgressBar);
        });

        //Resolution actions
        resolutionSelector.setOnAction(e -> {
            String selectedResolution = resolutionSelector.getValue();

            switch (selectedResolution) {
                case "300 x 300" -> {
                    currentResolution = 250;
                    requestRender(true, () -> {}, leftControlsPanel,  renderProgressBar);
                }

                case "500 x 500" -> {
                    currentResolution = 500;
                    requestRender(true, () -> {}, leftControlsPanel,  renderProgressBar);
                }

                case "750 x 750" -> {
                    currentResolution = 750;
                    requestRender(true, () -> {}, leftControlsPanel,  renderProgressBar);
                }

                case "1000 x 1000" -> {
                    currentResolution = 1000;
                    requestRender(true, () -> {}, leftControlsPanel,  renderProgressBar);
                }
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

            resolutionSelector.setValue("500 x 500");
            currentResolution = defaultResolution;


            renderer.setShadowQualityLow();

            requestRender(true, () -> {}, leftControlsPanel, renderProgressBar);
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
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);
        });

        renderTask.setOnFailed(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);

            renderTask.getException().printStackTrace();
        });



        Thread thread = new Thread(renderTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadModelAndRender(
            String filePath,
            double scale,
            Vector position,
            VBox controlsPanel,
            ProgressBar renderProgressBar) {

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() {

                loadModel(filePath, scale, position);

                renderer.render(progress -> updateProgress(progress, 1.0));

                return null;
            }
        };

        controlsPanel.setDisable(true);
        renderProgressBar.setVisible(true);
        renderProgressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);
        });

        task.setOnFailed(e -> {
            controlsPanel.setDisable(false);
            renderProgressBar.progressProperty().unbind();
            renderProgressBar.setProgress(0);

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }



    private void loadModel(String filePath, double scale, Vector offset){

        File file = null;
        try {
            file = new File(getClass()
                    .getResource(filePath)
                    .toURI());
        } catch (Exception ex) {
            throw new RuntimeException("Model could not be loaded");
        }

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

        Runnable pendingUpdate = update;

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

                renderProgressBar.progressProperty().unbind();
                renderProgressBar.setProgress(0);
            }

            if(pendingFinalRender){
                pendingFinalRender = false;
                renderAgain = false;

                requestRender(true, pendingUpdate, controlsPanel, renderProgressBar);
            } else if(renderAgain){
                renderAgain = false;
                requestRender(false, pendingUpdate, controlsPanel, renderProgressBar);
            }
        });

        task.setOnFailed(e -> {

            if (finalRender) {
                controlsPanel.setDisable(false);

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





