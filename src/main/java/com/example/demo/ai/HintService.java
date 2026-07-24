package com.example.demo.ai;

import com.example.demo.engine.GameEngine;
import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Recommends a valid movement direction using a simple
 * and explainable heuristic evaluation.
 *
 * <p>Each possible direction is simulated on an independent copy
 * of the current board. Invalid moves are ignored, and the
 * highest-scoring valid move is returned.</p>
 */
public final class HintService {

    /*
     * A fixed order makes equal-scoring results deterministic,
     * which also makes the service easier to test.
     */
    private static final List<Direction> SEARCH_ORDER = List.of(
            Direction.LEFT,
            Direction.UP,
            Direction.RIGHT,
            Direction.DOWN
    );

    private static final int EMPTY_CELL_WEIGHT = 1000;
    private static final int MERGE_SCORE_WEIGHT = 20;
    private static final int CORNER_WEIGHT = 2;

    /**
     * Suggests the highest-scoring valid direction.
     *
     * @param board current game board
     * @return suggested direction, or an empty Optional when
     *         no valid move remains
     */
    public Optional<Direction> suggestMove(Board board) {
        Objects.requireNonNull(
                board,
                "Board cannot be null"
        );

        Direction bestDirection = null;
        long bestEvaluation = Long.MIN_VALUE;

        for (Direction direction : SEARCH_ORDER) {
            /*
             * The active game board must not be modified while
             * possible moves are being evaluated.
             */
            Board simulatedBoard = board.copy();
            GameEngine simulatedEngine =
                    new GameEngine(simulatedBoard);

            MoveResult moveResult =
                    simulatedEngine.move(direction);

            if (!moveResult.moved()) {
                continue;
            }

            long evaluation = evaluateBoard(
                    simulatedBoard,
                    moveResult.scoreGained()
            );

            if (evaluation > bestEvaluation) {
                bestEvaluation = evaluation;
                bestDirection = direction;
            }
        }

        return Optional.ofNullable(bestDirection);
    }

    /**
     * Calculates a heuristic score for a simulated board.
     */
    private long evaluateBoard(
            Board board,
            int scoreGained
    ) {
        int emptyCells = 0;
        int maximumTile = 0;
        int maximumRow = 0;
        int maximumColumn = 0;

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0;
                 column < board.getSize();
                 column++) {

                int value = board.getValue(row, column);

                if (value == 0) {
                    emptyCells++;
                }

                if (value > maximumTile) {
                    maximumTile = value;
                    maximumRow = row;
                    maximumColumn = column;
                }
            }
        }

        long evaluation =
                (long) emptyCells * EMPTY_CELL_WEIGHT
                        + (long) scoreGained
                        * MERGE_SCORE_WEIGHT
                        + maximumTile;

        if (isCorner(
                maximumRow,
                maximumColumn,
                board.getSize()
        )) {
            evaluation +=
                    (long) maximumTile * CORNER_WEIGHT;
        }

        return evaluation;
    }

    /**
     * Checks whether a position is one of the four board corners.
     */
    private boolean isCorner(
            int row,
            int column,
            int boardSize
    ) {
        boolean edgeRow =
                row == 0 || row == boardSize - 1;

        boolean edgeColumn =
                column == 0 || column == boardSize - 1;

        return edgeRow && edgeColumn;
    }
}
