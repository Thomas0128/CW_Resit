package com.example.demo.engine;

import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;
import com.example.demo.model.Position;

import java.util.Arrays;
import java.util.Objects;

/**
 * Performs the movement and merging rules of the 2048 game
 *
 * <p>Obstacle cells divide rows and columns into independent segments
 * Tiles can move and merge only inside their own segment
 */
public final class GameEngine {

    private final Board board;

    /**
     * Creates an engine for the supplied board
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
     * Moves and merges tiles in the requested direction
     *
     * <p>Each obstacle divides a row or column into separate playable
     * segments. Tiles cannot move through an obstacle or merge with
     * tiles on the opposite side of an obstacle
     *
     * @param direction movement direction
     * @return movement result and score gained
     */
    public MoveResult move(Direction direction) {
        Objects.requireNonNull(
                direction,
                "Direction cannot be null"
        );

        boolean moved = false;
        int totalScoreGained = 0;

        for (int lineIndex = 0;
             lineIndex < board.getSize();
             lineIndex++) {

            SegmentResult result =
                    processLine(lineIndex, direction);

            moved |= result.moved();
            totalScoreGained += result.scoreGained();
        }

        if (!moved) {
            return MoveResult.noMove();
        }

        return new MoveResult(
                true,
                totalScoreGained
        );
    }

    /**
     * Determines whether at least one valid move remains
     *
     * <p>The four directions are simulated on independent board copies
     * This keeps movement validation consistent with the real movement
     * rules, including obstacle boundaries
     *
     * @return true when at least one direction changes the board
     */
    public boolean canMove() {
        for (Direction direction : Direction.values()) {
            Board simulatedBoard = board.copy();

            GameEngine simulatedEngine =
                    new GameEngine(simulatedBoard);

            MoveResult result =
                    simulatedEngine.move(direction);

            if (result.moved()) {
                return true;
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

    /**
     * Processes all playable segments in one row or column
     */
    private SegmentResult processLine(
            int lineIndex,
            Direction direction
    ) {
        boolean moved = false;
        int scoreGained = 0;
        int offset = 0;
        int size = board.getSize();

        while (offset < size) {

            // Skip obstacle cells.
            while (offset < size
                    && isObstacleAt(
                    lineIndex,
                    offset,
                    direction
            )) {

                offset++;
            }

            if (offset >= size) {
                break;
            }

            int segmentStart = offset;

            // Locate the end of this playable segment.
            while (offset < size
                    && !isObstacleAt(
                    lineIndex,
                    offset,
                    direction
            )) {

                offset++;
            }

            int segmentEnd = offset;

            int[] originalValues =
                    readSegment(
                            lineIndex,
                            segmentStart,
                            segmentEnd,
                            direction
                    );

            LineResult mergedResult =
                    mergeLine(originalValues);

            if (!Arrays.equals(
                    originalValues,
                    mergedResult.values()
            )) {
                moved = true;
            }

            writeSegment(
                    lineIndex,
                    segmentStart,
                    direction,
                    mergedResult.values()
            );

            scoreGained +=
                    mergedResult.scoreGained();
        }

        return new SegmentResult(
                moved,
                scoreGained
        );
    }

    /**
     * Reads one playable segment in movement order
     */
    private int[] readSegment(
            int lineIndex,
            int segmentStart,
            int segmentEnd,
            Direction direction
    ) {
        int[] values =
                new int[segmentEnd - segmentStart];

        for (int index = 0;
             index < values.length;
             index++) {

            Position position =
                    positionAt(
                            lineIndex,
                            segmentStart + index,
                            direction
                    );

            values[index] = board.getValue(
                    position.row(),
                    position.column()
            );
        }

        return values;
    }

    /**
     * Writes a processed segment back without touching obstacles
     */
    private void writeSegment(
            int lineIndex,
            int segmentStart,
            Direction direction,
            int[] values
    ) {
        for (int index = 0;
             index < values.length;
             index++) {

            Position position =
                    positionAt(
                            lineIndex,
                            segmentStart + index,
                            direction
                    );

            board.setValue(
                    position.row(),
                    position.column(),
                    values[index]
            );
        }
    }

    private boolean isObstacleAt(
            int lineIndex,
            int offset,
            Direction direction
    ) {
        Position position =
                positionAt(
                        lineIndex,
                        offset,
                        direction
                );

        return board.isObstacle(
                position.row(),
                position.column()
        );
    }

    /**
     * Converts a line and offset into a board position
     *
     * <p>The returned positions are ordered from the destination edge
     * of the movement toward the opposite edge
     */
    private Position positionAt(
            int lineIndex,
            int offset,
            Direction direction
    ) {
        int size = board.getSize();

        return switch (direction) {
            case LEFT -> new Position(lineIndex, offset);

            case RIGHT -> new Position(
                    lineIndex,
                    size - 1 - offset
            );

            case UP -> new Position(offset, lineIndex);

            case DOWN -> new Position(
                    size - 1 - offset,
                    lineIndex
            );
        };
    }

    /**
     * Compacts and merges the values in one playable segment
     */
    private LineResult mergeLine(
            int[] originalValues
    ) {
        int[] compactedValues =
                new int[originalValues.length];

        int valueCount = 0;

        for (int value : originalValues) {
            if (value != 0) {
                compactedValues[valueCount] = value;
                valueCount++;
            }
        }

        int[] mergedValues =
                new int[originalValues.length];

        int outputIndex = 0;
        int scoreGained = 0;

        for (int inputIndex = 0;
             inputIndex < valueCount;
             inputIndex++) {

            int value =
                    compactedValues[inputIndex];

            if (inputIndex + 1 < valueCount
                    && value
                    == compactedValues[inputIndex + 1]) {

                value *= 2;
                scoreGained += value;
                inputIndex++;
            }

            mergedValues[outputIndex] = value;
            outputIndex++;
        }

        return new LineResult(
                mergedValues,
                scoreGained
        );
    }

    private static void validateTarget(int target) {
        boolean isPowerOfTwo =
                target > 0
                        && (target & (target - 1)) == 0;

        if (!isPowerOfTwo) {
            throw new IllegalArgumentException(
                    "Target must be a positive power of two"
            );
        }
    }

    /**
     * Result from processing one row or column
     */
    private record SegmentResult(
            boolean moved,
            int scoreGained
    ) {
    }

    /**
     * Result from merging one playable segment
     */
    private record LineResult(
            int[] values,
            int scoreGained
    ) {
    }
}
