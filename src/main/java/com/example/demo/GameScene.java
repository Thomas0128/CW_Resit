package com.example.demo;

import com.example.demo.engine.GameEngine;
import com.example.demo.engine.TileSpawner;
import com.example.demo.history.GameHistory;
import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.GameState;
import com.example.demo.model.MoveResult;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Connects the JavaFX game interface to the independent game engine
 */
class GameScene {

    private static final int HEIGHT = 700;
    private static final int DISTANCE_BETWEEN_CELLS = 10;

    private static int n = 4;

    private static double LENGTH =
            (HEIGHT - ((n + 1) * DISTANCE_BETWEEN_CELLS))
                    / (double) n;

    private final TextMaker textMaker =
            TextMaker.getSingleInstance();

    private final TileSpawner tileSpawner =
            new TileSpawner();

    private final GameHistory gameHistory =
            new GameHistory();

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

    /**
     * Creates a new empty board and places the first two tiles
     */
    private void initialiseGame() {
        board = new Board(n);
        gameEngine = new GameEngine(board);
        score = 0;

        gameHistory.clear();

        tileSpawner.spawn(board);
        tileSpawner.spawn(board);
    }

    /**
     * Recreates the visible board from the numerical board model
     */
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

    /**
     * Converts a JavaFX key code to a game direction
     *
     * @param keyCode pressed keyboard key
     * @return movement direction, or null for a non-direction key
     */
    private Direction convertDirection(KeyCode keyCode) {
        return switch (keyCode) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * Restores the most recently saved board and score
     *
     * @param scoreText visible score display
     * @param undoButton Undo button
     */
    private void undoLastMove(
            Text scoreText,
            Button undoButton
    ) {
        gameHistory.undo().ifPresent(savedState -> {
            board = savedState.board();
            gameEngine = new GameEngine(board);
            score = savedState.score();

            scoreText.setText(Long.toString(score));
            renderBoard();
        });

        undoButton.setDisable(!gameHistory.canUndo());
    }

    /**
     * Displays the existing end-game scene
     */
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
        gameHistory.clear();
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

        Button undoButton = new Button("Undo");
        undoButton.setLayoutX(735);
        undoButton.setLayoutY(210);
        undoButton.setPrefWidth(110);
        undoButton.setDisable(true);
        undoButton.setFocusTraversable(false);
        root.getChildren().add(undoButton);

        initialiseGame();
        renderBoard();

        undoButton.setOnAction(event ->
                undoLastMove(scoreText, undoButton)
        );

        /*
         * setOnKeyPressed replaces the previous handler instead of
         * adding another handler whenever a new game is started
         */
        gameScene.setOnKeyPressed(keyEvent -> {
            Direction direction =
                    convertDirection(keyEvent.getCode());

            /*
             * Ignore letters, Space, Enter and all other
             * non-direction keys.
             */
            if (direction == null) {
                return;
            }

            /*
             * Capture the board and score before attempting the move
             * The snapshot is only saved if the move is valid
             */
            GameState previousState =
                    new GameState(board, score);

            MoveResult moveResult =
                    gameEngine.move(direction);

            /*
             * Invalid moves are not added to the Undo history
             */
            if (!moveResult.moved()) {
                return;
            }

            gameHistory.save(previousState);
            undoButton.setDisable(false);

            score += moveResult.scoreGained();
            scoreText.setText(Long.toString(score));

            tileSpawner.spawn(board);
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
