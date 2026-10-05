package com.example.raytracer.ui;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        UIManager uiManager = new UIManager(stage);
    }

    public static void main(String[] args) {
        launch();
    }


}
