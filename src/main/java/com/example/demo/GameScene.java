package com.example.demo;

import com.example.demo.ai.HintService;
import com.example.demo.engine.GameEngine;
import com.example.demo.engine.TileSpawner;
import com.example.demo.history.GameHistory;
import com.example.demo.level.LevelConfig;
import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.GameState;
import com.example.demo.model.MoveResult;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Connects the JavaFX interface to the independent game engine.
 */
class GameScene {

    private static final int BOARD_AREA_SIZE = 700;
    private static final int DISTANCE_BETWEEN_CELLS = 10;

    private static final Color OBSTACLE_COLOR =
            Color.rgb(75, 75, 75);

    private static int boardSize = 4;

    private static double cellLength =
            calculateCellLength(boardSize);

    private final TextMaker textMaker =
            TextMaker.getSingleInstance();

    private final TileSpawner tileSpawner =
            new TileSpawner();

    private final GameHistory gameHistory =
            new GameHistory();

    private final HintService hintService =
            new HintService();

    private Board board;
    private GameEngine gameEngine;
    private Group boardRoot;

    private long score;
    private boolean targetMessageShown;

    /**
     * Creates a game scene controller.
     */
    GameScene() {
    }

    /**
     * Updates the board size used by the JavaFX interface.
     *
     * @param number board width and height
     */
    static void setN(int number) {
        boardSize = number;
        cellLength = calculateCellLength(number);
    }

    /**
     * Returns the current JavaFX cell length.
     *
     * @return cell length in pixels
     */
    static double getLENGTH() {
        return cellLength;
    }

    /**
     * Calculates a cell length that fits inside the board area.
     *
     * @param size number of rows and columns
     * @return calculated cell length
     */
    private static double calculateCellLength(int size) {
        return (
                BOARD_AREA_SIZE
                        - ((size + 1)
                        * DISTANCE_BETWEEN_CELLS)
        ) / (double) size;
    }

    /**
     * Creates a fresh game using the selected level configuration.
     *
     * <p>The board receives the obstacle positions supplied by the
     * level. Levels without obstacles simply provide an empty obstacle
     * collection.</p>
     *
     * @param levelConfig selected level configuration
     */
    private void initialiseGame(LevelConfig levelConfig) {
        setN(levelConfig.boardSize());

        board = new Board(
                levelConfig.boardSize(),
                levelConfig.obstacles()
        );

        gameEngine = new GameEngine(board);

        score = 0;
        targetMessageShown = false;

        gameHistory.clear();

        for (int tile = 0;
             tile < levelConfig.startingTileCount();
             tile++) {

            tileSpawner.spawn(board);
        }
    }

    /**
     * Recreates the JavaFX cells from the numerical board.
     */
    private void renderBoard() {
        boardRoot.getChildren().clear();

        for (int row = 0;
             row < board.getSize();
             row++) {

            for (int column = 0;
                 column < board.getSize();
                 column++) {

                double x =
                        column * cellLength
                                + (column + 1)
                                * DISTANCE_BETWEEN_CELLS;

                double y =
                        row * cellLength
                                + (row + 1)
                                * DISTANCE_BETWEEN_CELLS;

                Cell cell = new Cell(
                        x,
                        y,
                        cellLength,
                        boardRoot
                );

                if (board.isObstacle(row, column)) {
                    renderObstacle(x, y);
                    continue;
                }

                int value =
                        board.getValue(row, column);

                if (value != 0) {
                    Text tileText =
                            textMaker.madeText(
                                    Integer.toString(value),
                                    cell.getX(),
                                    cell.getY(),
                                    boardRoot
                            );

                    cell.setTextClass(tileText);
                    cell.setColorByNumber(value);

                    boardRoot.getChildren().add(
                            tileText
                    );
                }
            }
        }
    }

    /**
     * Draws one blocked board position.
     *
     * @param x horizontal position
     * @param y vertical position
     */
    private void renderObstacle(
            double x,
            double y
    ) {
        Rectangle obstacleRectangle =
                new Rectangle(
                        x,
                        y,
                        cellLength,
                        cellLength
                );

        obstacleRectangle.setArcWidth(14);
        obstacleRectangle.setArcHeight(14);
        obstacleRectangle.setFill(
                OBSTACLE_COLOR
        );

        Text obstacleText = new Text("X");

        obstacleText.setFill(Color.WHITE);
        obstacleText.setFont(
                Font.font(
                        Math.max(
                                30,
                                cellLength * 0.30
                        )
                )
        );

        double textWidth =
                obstacleText
                        .getLayoutBounds()
                        .getWidth();

        double textHeight =
                obstacleText
                        .getLayoutBounds()
                        .getHeight();

        obstacleText.setX(
                x + (cellLength - textWidth) / 2
        );

        obstacleText.setY(
                y
                        + (cellLength - textHeight) / 2
                        - obstacleText
                        .getLayoutBounds()
                        .getMinY()
        );

        boardRoot.getChildren().addAll(
                obstacleRectangle,
                obstacleText
        );
    }

    /**
     * Converts an arrow key into a game direction.
     *
     * @param keyCode pressed keyboard key
     * @return matching direction, or null for another key
     */
    private Direction convertDirection(
            KeyCode keyCode
    ) {
        return switch (keyCode) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }

    /**
     * Updates the visible level-progress message.
     *
     * @param levelConfig selected level configuration
     * @param statusText visible status message
     */
    private void updateTargetStatus(
            LevelConfig levelConfig,
            Text statusText
    ) {
        if (gameEngine.hasReachedTarget(
                levelConfig.targetTile()
        )) {
            statusText.setText(
                    "Target reached!"
            );
        } else {
            statusText.setText(
                    "Reach "
                            + levelConfig.targetTile()
            );

            targetMessageShown = false;
        }
    }

    /**
     * Displays a message the first time the level target is reached.
     *
     * @param levelConfig selected level configuration
     * @param statusText visible status message
     * @param primaryStage main application stage
     */
    private void checkTargetReached(
            LevelConfig levelConfig,
            Text statusText,
            Stage primaryStage
    ) {
        boolean targetReached =
                gameEngine.hasReachedTarget(
                        levelConfig.targetTile()
                );

        if (!targetReached) {
            return;
        }

        statusText.setText(
                "Target reached!"
        );

        if (targetMessageShown) {
            return;
        }

        targetMessageShown = true;

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.initOwner(primaryStage);
        alert.setTitle("Level Complete");

        alert.setHeaderText(
                levelConfig.name()
                        + " completed"
        );

        alert.setContentText(
                "You reached the "
                        + levelConfig.targetTile()
                        + " tile.\n"
                        + "You may continue playing."
        );

        alert.showAndWait();
    }

    /**
     * Restores the most recently saved board and score.
     *
     * @param scoreText visible score
     * @param undoButton undo control
     * @param hintText visible hint
     * @param statusText visible target status
     * @param levelConfig selected level configuration
     */
    private void undoLastMove(
            Text scoreText,
            Button undoButton,
            Text hintText,
            Text statusText,
            LevelConfig levelConfig
    ) {
        gameHistory.undo().ifPresent(
                savedState -> {
                    board = savedState.board();
                    gameEngine =
                            new GameEngine(board);

                    score = savedState.score();

                    scoreText.setText(
                            Long.toString(score)
                    );

                    hintText.setText("Hint: -");

                    updateTargetStatus(
                            levelConfig,
                            statusText
                    );

                    renderBoard();
                }
        );

        undoButton.setDisable(
                !gameHistory.canUndo()
        );
    }

    /**
     * Displays the existing game-over scene.
     *
     * @param endGameScene game-over scene
     * @param endGameRoot game-over root
     * @param primaryStage main application stage
     * @param root current game root
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

    /**
     * Creates and starts the selected playable level.
     *
     * @param gameScene game scene
     * @param root game scene root
     * @param primaryStage main application stage
     * @param endGameScene game-over scene
     * @param endGameRoot game-over root
     * @param menuScene level-selection scene
     * @param levelConfig selected level configuration
     */
    void game(
            Scene gameScene,
            Group root,
            Stage primaryStage,
            Scene endGameScene,
            Group endGameRoot,
            Scene menuScene,
            LevelConfig levelConfig
    ) {
        root.getChildren().clear();

        boardRoot = new Group();
        root.getChildren().add(boardRoot);

        Text levelNameText =
                new Text(levelConfig.name());

        levelNameText.setFont(
                Font.font(25)
        );

        levelNameText.relocate(720, 35);
        root.getChildren().add(
                levelNameText
        );

        String boardDetails =
                levelConfig.boardSize()
                        + " × "
                        + levelConfig.boardSize()
                        + " | Target "
                        + levelConfig.targetTile();

        if (levelConfig.hasObstacles()) {
            boardDetails +=
                    " | Obstacles "
                            + levelConfig
                            .obstacles()
                            .size();
        }

        Text boardDetailsText =
                new Text(boardDetails);

        boardDetailsText.setFont(
                Font.font(15)
        );

        boardDetailsText.relocate(720, 75);
        root.getChildren().add(
                boardDetailsText
        );

        Text scoreLabel =
                new Text("SCORE");

        scoreLabel.setFont(
                Font.font(27)
        );

        scoreLabel.relocate(735, 115);
        root.getChildren().add(
                scoreLabel
        );

        Text scoreText = new Text("0");

        scoreText.setFont(
                Font.font(22)
        );

        scoreText.relocate(735, 155);
        root.getChildren().add(
                scoreText
        );

        Button undoButton =
                new Button("Undo");

        undoButton.setLayoutX(730);
        undoButton.setLayoutY(205);
        undoButton.setPrefWidth(120);
        undoButton.setDisable(true);
        undoButton.setFocusTraversable(false);

        root.getChildren().add(
                undoButton
        );

        Button hintButton =
                new Button("Hint");

        hintButton.setLayoutX(730);
        hintButton.setLayoutY(250);
        hintButton.setPrefWidth(120);
        hintButton.setFocusTraversable(false);

        root.getChildren().add(
                hintButton
        );

        Button restartButton =
                new Button("Restart");

        restartButton.setLayoutX(730);
        restartButton.setLayoutY(295);
        restartButton.setPrefWidth(120);
        restartButton.setFocusTraversable(false);

        root.getChildren().add(
                restartButton
        );

        Button levelsButton =
                new Button("Level Menu");

        levelsButton.setLayoutX(730);
        levelsButton.setLayoutY(340);
        levelsButton.setPrefWidth(120);
        levelsButton.setFocusTraversable(false);

        root.getChildren().add(
                levelsButton
        );

        Text hintText =
                new Text("Hint: -");

        hintText.setFont(
                Font.font(18)
        );

        hintText.relocate(720, 410);
        root.getChildren().add(
                hintText
        );

        Text statusText =
                new Text(
                        "Reach "
                                + levelConfig.targetTile()
                );

        statusText.setFont(
                Font.font(18)
        );

        statusText.relocate(720, 455);
        root.getChildren().add(
                statusText
        );

        initialiseGame(levelConfig);
        renderBoard();

        undoButton.setOnAction(
                event -> {
                    undoLastMove(
                            scoreText,
                            undoButton,
                            hintText,
                            statusText,
                            levelConfig
                    );

                    gameScene
                            .getRoot()
                            .requestFocus();
                }
        );

        hintButton.setOnAction(
                event -> {
                    hintService
                            .suggestMove(board)
                            .ifPresentOrElse(
                                    direction ->
                                            hintText.setText(
                                                    "Hint: "
                                                            + direction
                                            ),
                                    () ->
                                            hintText.setText(
                                                    "No valid move"
                                            )
                            );

                    gameScene
                            .getRoot()
                            .requestFocus();
                }
        );

        restartButton.setOnAction(
                event -> {
                    initialiseGame(levelConfig);
                    renderBoard();

                    scoreText.setText("0");
                    hintText.setText("Hint: -");

                    statusText.setText(
                            "Reach "
                                    + levelConfig
                                    .targetTile()
                    );

                    undoButton.setDisable(true);

                    gameScene
                            .getRoot()
                            .requestFocus();
                }
        );

        levelsButton.setOnAction(
                event -> {
                    gameScene.setOnKeyPressed(null);
                    gameHistory.clear();

                    primaryStage.setScene(
                            menuScene
                    );
                }
        );

        gameScene.setOnKeyPressed(
                keyEvent -> {
                    Direction direction =
                            convertDirection(
                                    keyEvent.getCode()
                            );

                    if (direction == null) {
                        return;
                    }

                    GameState previousState =
                            new GameState(
                                    board,
                                    score
                            );

                    MoveResult moveResult =
                            gameEngine.move(
                                    direction
                            );

                    if (!moveResult.moved()) {
                        keyEvent.consume();
                        return;
                    }

                    gameHistory.save(
                            previousState
                    );

                    undoButton.setDisable(false);
                    hintText.setText("Hint: -");

                    score +=
                            moveResult.scoreGained();

                    scoreText.setText(
                            Long.toString(score)
                    );

                    tileSpawner.spawn(board);
                    renderBoard();

                    checkTargetReached(
                            levelConfig,
                            statusText,
                            primaryStage
                    );

                    if (gameEngine.isGameOver()) {
                        gameScene.setOnKeyPressed(
                                null
                        );

                        showEndGame(
                                endGameScene,
                                endGameRoot,
                                primaryStage,
                                root
                        );
                    }

                    keyEvent.consume();
                }
        );

        gameScene
                .getRoot()
                .requestFocus();
    }
}
