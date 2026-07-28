package com.example.demo;

import com.example.demo.level.LevelCatalog;
import com.example.demo.level.LevelConfig;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and displays the level-selection menu.
 */
public class Main extends Application {

    static final int WIDTH = 900;
    static final int HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {

        Group menuRoot = new Group();
        Scene menuScene = new Scene(
                menuRoot,
                WIDTH,
                HEIGHT,
                Color.rgb(238, 228, 218)
        );

        Group gameRoot = new Group();
        Scene gameScene = new Scene(
                gameRoot,
                WIDTH,
                HEIGHT,
                Color.rgb(189, 177, 92)
        );

        Group endGameRoot = new Group();
        Scene endGameScene = new Scene(
                endGameRoot,
                WIDTH,
                HEIGHT,
                Color.rgb(250, 220, 210)
        );

        GameScene game = new GameScene();

        Text title = new Text("2048");
        title.setFont(Font.font(72));

        Text menuMessage =
                new Text("Choose a level to begin");
        menuMessage.setFont(Font.font(24));

        Button classicButton =
                createLevelButton(
                        "Classic — 4 × 4",
                        LevelCatalog.CLASSIC,
                        game,
                        gameScene,
                        gameRoot,
                        primaryStage,
                        endGameScene,
                        endGameRoot,
                        menuScene
                );

        Button largeBoardButton =
                createLevelButton(
                        "Large Board — 5 × 5",
                        LevelCatalog.LARGE_BOARD,
                        game,
                        gameScene,
                        gameRoot,
                        primaryStage,
                        endGameScene,
                        endGameRoot,
                        menuScene
                );

        Text classicDescription =
                new Text("Classic target: 2048");

        Text largeBoardDescription =
                new Text("Large Board target: 4096");

        Text controls =
                new Text(
                        "Use the arrow keys to move tiles.\n"
                                + "Undo restores your previous move.\n"
                                + "Hint recommends a valid direction."
                );

        VBox menuBox = new VBox(
                18,
                title,
                menuMessage,
                classicButton,
                classicDescription,
                largeBoardButton,
                largeBoardDescription,
                controls
        );

        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPrefWidth(WIDTH);
        menuBox.setLayoutY(160);

        menuRoot.getChildren().add(menuBox);

        primaryStage.setTitle("2048 Level Challenge");
        primaryStage.setResizable(false);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    /**
     * Creates a button that starts the supplied level.
     */
    private Button createLevelButton(
            String buttonText,
            LevelConfig levelConfig,
            GameScene game,
            Scene gameScene,
            Group gameRoot,
            Stage primaryStage,
            Scene endGameScene,
            Group endGameRoot,
            Scene menuScene
    ) {
        Button button = new Button(buttonText);
        button.setPrefWidth(260);
        button.setPrefHeight(50);
        button.setFont(Font.font(18));
        button.setFocusTraversable(false);

        button.setOnAction(event -> {
            game.game(
                    gameScene,
                    gameRoot,
                    primaryStage,
                    endGameScene,
                    endGameRoot,
                    menuScene,
                    levelConfig
            );

            primaryStage.setScene(gameScene);
            gameRoot.requestFocus();
        });

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}