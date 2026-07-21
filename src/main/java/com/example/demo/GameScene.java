package com.example.demo;

import com.example.demo.engine.GameEngine;
import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GameScene {

    private static final int HEIGHT = 700;
    private static final int DISTANCE_BETWEEN_CELLS = 10;

    private static int n = 4;

    private static double LENGTH =
            (HEIGHT - ((n + 1) * DISTANCE_BETWEEN_CELLS))
                    / (double) n;

    private final TextMaker textMaker =
            TextMaker.getSingleInstance();

    private final Random random = new Random();

    private Board board;
    private GameEngine gameEngine;
    private Group boardRoot;

    private long score;

    static void setN(int number) {
        n = number;

        LENGTH =
                (HEIGHT - ((n + 1) * DISTANCE_BETWEEN_CELLS))
                        / (double) n;
    }

    static double getLENGTH() {
        return LENGTH;
    }

    private void initialiseGame() {
        board = new Board(n);
        gameEngine = new GameEngine(board);
        score = 0;

        spawnRandomTile();
        spawnRandomTile();
    }

    private boolean spawnRandomTile() {
        List<int[]> emptyPositions = new ArrayList<>();

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0;
                 column < board.getSize();
                 column++) {

                if (board.isEmpty(row, column)) {
                    emptyPositions.add(
                            new int[]{row, column}
                    );
                }
            }
        }

        if (emptyPositions.isEmpty()) {
            return false;
        }

        int[] selectedPosition =
                emptyPositions.get(
                        random.nextInt(emptyPositions.size())
                );

        int value = random.nextInt(10) == 0 ? 4 : 2;

        board.setValue(
                selectedPosition[0],
                selectedPosition[1],
                value
        );

        return true;
    }

    private void renderBoard() {
        boardRoot.getChildren().clear();

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0;
                 column < board.getSize();
                 column++) {

                double x =
                        column * LENGTH
                                + (column + 1)
                                * DISTANCE_BETWEEN_CELLS;

                double y =
                        row * LENGTH
                                + (row + 1)
                                * DISTANCE_BETWEEN_CELLS;

                Cell cell = new Cell(
                        x,
                        y,
                        LENGTH,
                        boardRoot
                );

                int value = board.getValue(row, column);

                if (value != 0) {
                    Text tileText = textMaker.madeText(
                            Integer.toString(value),
                            cell.getX(),
                            cell.getY(),
                            boardRoot
                    );

                    cell.setTextClass(tileText);
                    cell.setColorByNumber(value);
                    boardRoot.getChildren().add(tileText);
                }
            }
        }
    }

    private Direction convertDirection(KeyCode keyCode) {
        return switch (keyCode) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    private void showEndGame(
            Scene endGameScene,
            Group endGameRoot,
            Stage primaryStage,
            Group root
    ) {
        primaryStage.setScene(endGameScene);

        EndGame.getInstance().endGameShow(
                endGameScene,
                endGameRoot,
                primaryStage,
                score
        );

        root.getChildren().clear();
        score = 0;
    }

    void game(
            Scene gameScene,
            Group root,
            Stage primaryStage,
            Scene endGameScene,
            Group endGameRoot
    ) {
        root.getChildren().clear();

        boardRoot = new Group();
        root.getChildren().add(boardRoot);

        Text scoreLabel = new Text("SCORE :");
        scoreLabel.setFont(Font.font(30));
        scoreLabel.relocate(750, 100);
        root.getChildren().add(scoreLabel);

        Text scoreText = new Text("0");
        scoreText.setFont(Font.font(20));
        scoreText.relocate(750, 150);
        root.getChildren().add(scoreText);

        initialiseGame();
        renderBoard();

        gameScene.setOnKeyPressed(keyEvent -> {
            Direction direction =
                    convertDirection(keyEvent.getCode());

            if (direction == null) {
                return;
            }

            MoveResult moveResult =
                    gameEngine.move(direction);

            if (!moveResult.moved()) {
                return;
            }

            score += moveResult.scoreGained();
            scoreText.setText(Long.toString(score));

            spawnRandomTile();
            renderBoard();

            if (gameEngine.isGameOver()) {
                showEndGame(
                        endGameScene,
                        endGameRoot,
                        primaryStage,
                        root
                );
            }

            keyEvent.consume();
        });
    }
}
