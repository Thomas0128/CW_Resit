package com.example.demo.engine;

import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;

import java.util.Arrays;
import java.util.Objects;

/**
 * Performs the movement and merging rules of the 2048 game
 *
 * <p>This class contains no JavaFX code, allowing the game rules to be
 * tested independently from the user interface</p>
 */
public final class GameEngine {

    private final Board board;

    /**
     * Creates an engine that operates on the supplied board
     *
     * @param board board controlled by this engine
     */
    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(
                board,
                "Board cannot be null"
        );
    }

    /**
     * Returns the board controlled by this engine
     *
     * @return current board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Moves and merges all tiles in the requested direction
     *
     * <p>Each tile may merge only once during a move. The returned score
     * is the sum of the newly created tile values.</p>
     *
     * @param direction direction in which tiles should move
     * @return result describing whether the board changed and the score gained
     */
    public MoveResult move(Direction direction) {
        Objects.requireNonNull(
                direction,
                "Direction cannot be null"
        );

        boolean moved = false;
        int totalScoreGained = 0;

        for (int index = 0; index < board.getSize(); index++) {
            int[] originalLine = readLine(index, direction);
            LineResult lineResult = mergeLine(originalLine);
            int[] updatedLine = lineResult.values();

            if (!Arrays.equals(originalLine, updatedLine)) {
                moved = true;
            }

            writeLine(index, direction, updatedLine);
            totalScoreGained += lineResult.scoreGained();
        }

        if (!moved) {
            return MoveResult.noMove();
        }

        return new MoveResult(true, totalScoreGained);
    }

    /**
     * Determines whether at least one valid move remains
     *
     * @return true when a tile can move or merge
     */
    public boolean canMove() {
        if (!board.isFull()) {
            return true;
        }

        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                int value = board.getValue(row, column);

                if (column + 1 < size
                        && value == board.getValue(row, column + 1)) {
                    return true;
                }

                if (row + 1 < size
                        && value == board.getValue(row + 1, column)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines whether no valid moves remain
     *
     * @return true when the game is over
     */
    public boolean isGameOver() {
        return !canMove();
    }

    /**
     * Determines whether the board contains the target tile
     *
     * @param target target tile value
     * @return true when the target has been reached
     */
    public boolean hasReachedTarget(int target) {
        validateTarget(target);
        return board.contains(target);
    }

    private int[] readLine(int index, Direction direction) {
        int size = board.getSize();
        int[] values = new int[size];

        for (int offset = 0; offset < size; offset++) {
            values[offset] = switch (direction) {
                case LEFT -> board.getValue(index, offset);
                case RIGHT -> board.getValue(index, size - 1 - offset);
                case UP -> board.getValue(offset, index);
                case DOWN -> board.getValue(size - 1 - offset, index);
            };
        }

        return values;
    }

    private void writeLine(
            int index,
            Direction direction,
            int[] values
    ) {
        int size = board.getSize();

        for (int offset = 0; offset < size; offset++) {
            switch (direction) {
                case LEFT ->
                        board.setValue(index, offset, values[offset]);

                case RIGHT ->
                        board.setValue(
                                index,
                                size - 1 - offset,
                                values[offset]
                        );

                case UP ->
                        board.setValue(offset, index, values[offset]);

                case DOWN ->
                        board.setValue(
                                size - 1 - offset,
                                index,
                                values[offset]
                        );
            }
        }
    }

    private LineResult mergeLine(int[] originalLine) {
        int[] compactedValues = new int[originalLine.length];
        int valueCount = 0;

        for (int value : originalLine) {
            if (value != 0) {
                compactedValues[valueCount] = value;
                valueCount++;
            }
        }

        int[] mergedValues = new int[originalLine.length];
        int outputIndex = 0;
        int scoreGained = 0;

        for (int inputIndex = 0;
             inputIndex < valueCount;
             inputIndex++) {

            int value = compactedValues[inputIndex];

            if (inputIndex + 1 < valueCount
                    && value == compactedValues[inputIndex + 1]) {

                value *= 2;
                scoreGained += value;
                inputIndex++;
            }

            mergedValues[outputIndex] = value;
            outputIndex++;
        }

        return new LineResult(mergedValues, scoreGained);
    }

    private static void validateTarget(int target) {
        boolean isPowerOfTwo =
                target > 0 && (target & (target - 1)) == 0;

        if (!isPowerOfTwo) {
            throw new IllegalArgumentException(
                    "Target must be a positive power of two"
            );
        }
    }

    /**
     * Internal result created while processing one row or column
     *
     * @param values      processed line values
     * @param scoreGained score gained from merges in the line
     */
    private record LineResult(int[] values, int scoreGained) {
    }
}
