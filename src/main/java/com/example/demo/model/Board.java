package com.example.demo.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the numerical state of a 2048 board
 *
 * <p>The board may contain obstacle positions that cannot store tiles
 * This class contains no JavaFX code, allowing it to be tested
 * independently from the graphical interface.</p>
 */
public final class Board {

    public static final int DEFAULT_SIZE = 4;

    private final int size;
    private final int[][] tiles;
    private final Set<Position> obstacles;

    /**
     * Creates an empty classic 4 × 4 board.
     */
    public Board() {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates an empty square board without obstacles
     *
     * @param size number of rows and columns
     */
    public Board(int size) {
        this(size, Set.of());
    }

    /**
     * Creates an empty square board with obstacle positions
     *
     * @param size number of rows and columns
     * @param obstacles positions that cannot contain tiles
     */
    public Board(
            int size,
            Collection<Position> obstacles
    ) {
        validateSize(size);

        this.size = size;
        this.tiles = new int[size][size];
        this.obstacles =
                validateObstacles(obstacles, size);
    }

    /**
     * Creates a board containing the supplied values
     *
     * @param initialValues initial tile values
     */
    public Board(int[][] initialValues) {
        this(initialValues, Set.of());
    }

    /**
     * Creates a board containing supplied values and obstacles
     *
     * @param initialValues initial tile values
     * @param obstacles positions that cannot contain tiles
     */
    public Board(
            int[][] initialValues,
            Collection<Position> obstacles
    ) {
        validateInitialValues(initialValues);

        this.size = initialValues.length;
        this.obstacles =
                validateObstacles(obstacles, size);
        this.tiles = copyArray(initialValues);

        for (Position obstacle : this.obstacles) {
            if (tiles[obstacle.row()]
                    [obstacle.column()] != 0) {

                throw new IllegalArgumentException(
                        "Obstacle positions cannot contain tiles"
                );
            }
        }
    }

    /**
     * Returns the board siz.
     *
     * @return number of rows and columns
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the immutable obstacle positions
     *
     * @return obstacle positions
     */
    public Set<Position> getObstacles() {
        return obstacles;
    }

    /**
     * Checks whether a position is an obstacle
     *
     * @param row row index
     * @param column column index
     * @return true when the position is blocked
     */
    public boolean isObstacle(int row, int column) {
        checkPosition(row, column);

        return obstacles.contains(
                new Position(row, column)
        );
    }

    /**
     * Returns the value at a board position
     *
     * @param row row index
     * @param column column index
     * @return tile value, or zero when no tile is present
     */
    public int getValue(int row, int column) {
        checkPosition(row, column);
        return tiles[row][column];
    }

    /**
     * Changes the value at a playable board position
     *
     * @param row row index
     * @param column column index
     * @param value zero or a positive power of two
     */
    public void setValue(
            int row,
            int column,
            int value
    ) {
        checkPosition(row, column);
        validateTileValue(value);

        if (value != 0 && isObstacle(row, column)) {
            throw new IllegalStateException(
                    "Cannot place a tile on an obstacle"
            );
        }

        tiles[row][column] = value;
    }

    /**
     * Checks whether a playable position is empty
     *
     * <p>Obstacle positions are not considered empty cells.</p>
     *
     * @param row row index
     * @param column column index
     * @return true when the position is playable and contains zero
     */
    public boolean isEmpty(int row, int column) {
        checkPosition(row, column);

        return !isObstacle(row, column)
                && tiles[row][column] == 0;
    }

    /**
     * Counts empty playable cells
     *
     * @return number of available empty positions
     */
    public int countEmptyCells() {
        int count = 0;

        for (int row = 0; row < size; row++) {
            for (int column = 0;
                 column < size;
                 column++) {

                if (isEmpty(row, column)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Returns the total number of playable cells
     *
     * @return board cells excluding obstacles
     */
    public int getPlayableCellCount() {
        return size * size - obstacles.size();
    }

    /**
     * Checks whether every playable cell contains a tile
     *
     * @return true when no playable empty cell remains
     */
    public boolean isFull() {
        return countEmptyCells() == 0;
    }

    /**
     * Checks whether the board contains a tile value
     *
     * @param value value to locate
     * @return true when the value exists
     */
    public boolean contains(int value) {
        for (int row = 0; row < size; row++) {
            for (int column = 0;
                 column < size;
                 column++) {

                if (!isObstacle(row, column)
                        && tiles[row][column] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes all tiles while preserving obstacles
     */
    public void clear() {
        for (int[] row : tiles) {
            Arrays.fill(row, 0);
        }
    }

    /**
     * Returns a defensive copy of the numerical values
     *
     * @return copied two-dimensional array
     */
    public int[][] toArray() {
        return copyArray(tiles);
    }

    /**
     * Creates an independent copy of this board
     *
     * @return copied board including its obstacles
     */
    public Board copy() {
        return new Board(tiles, obstacles);
    }

    private void checkPosition(
            int row,
            int column
    ) {
        if (row < 0 || row >= size
                || column < 0 || column >= size) {

            throw new IndexOutOfBoundsException(
                    "Invalid board position: ("
                            + row + ", " + column + ")"
            );
        }
    }

    private static void validateSize(int size) {
        if (size < 2) {
            throw new IllegalArgumentException(
                    "Board size must be at least 2"
            );
        }
    }

    private static void validateInitialValues(
            int[][] values
    ) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException(
                    "Initial board must contain at least two rows"
            );
        }

        int expectedSize = values.length;

        for (int[] row : values) {
            if (row == null
                    || row.length != expectedSize) {

                throw new IllegalArgumentException(
                        "Initial board must be square"
                );
            }

            for (int value : row) {
                validateTileValue(value);
            }
        }
    }

    private static Set<Position> validateObstacles(
            Collection<Position> obstaclePositions,
            int size
    ) {
        Objects.requireNonNull(
                obstaclePositions,
                "Obstacle positions cannot be null"
        );

        Set<Position> validated =
                new HashSet<>();

        for (Position obstacle : obstaclePositions) {
            Objects.requireNonNull(
                    obstacle,
                    "Obstacle position cannot be null"
            );

            if (obstacle.row() >= size
                    || obstacle.column() >= size) {

                throw new IllegalArgumentException(
                        "Obstacle position must fit on the board"
                );
            }

            if (!validated.add(obstacle)) {
                throw new IllegalArgumentException(
                        "Obstacle positions cannot be duplicated"
                );
            }
        }

        return Set.copyOf(validated);
    }

    private static void validateTileValue(int value) {
        boolean isPowerOfTwo =
                value > 0
                        && (value & (value - 1)) == 0;

        if (value != 0 && !isPowerOfTwo) {
            throw new IllegalArgumentException(
                    "Tile value must be zero "
                            + "or a positive power of two"
            );
        }
    }

    private static int[][] copyArray(
            int[][] source
    ) {
        int[][] copy =
                new int[source.length][];

        for (int row = 0;
             row < source.length;
             row++) {

            copy[row] = Arrays.copyOf(
                    source[row],
                    source[row].length
            );
        }

        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Board other)) {
            return false;
        }

        return Arrays.deepEquals(
                tiles,
                other.tiles
        ) && obstacles.equals(other.obstacles);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(tiles);
        return 31 * result + obstacles.hashCode();
    }

    @Override
    public String toString() {
        return "Board{tiles="
                + Arrays.deepToString(tiles)
                + ", obstacles="
                + obstacles
                + '}';
    }
}
