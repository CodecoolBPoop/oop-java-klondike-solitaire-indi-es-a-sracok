package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class Klondike extends Application {

    private static final double WINDOW_WIDTH = 1400;
    private static final double WINDOW_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Card.loadCardImages("card_images/card_back.png");
        Game game = new Game();
        game.setTableBackground(new Image("/table/green.png"));

        primaryStage.setTitle("Klondike Solitaire");

        Button themeButton = new Button("Cute Theme");
        game.getChildren().add(themeButton);
        themeButton.setLayoutX(10);
        themeButton.setLayoutY(10);

        themeButton.setOnAction((event) -> {
            game.setTableBackground(new Image("/table/teddy_bg.png"));
            // Card.cardBackImage = new Image("card_images/card_back_rainbow.png");
            Card.loadCardImages("card_images/card_back_rainbow.png");
        });

        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }

}
