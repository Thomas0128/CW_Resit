package com.example.demo.model;

import java.util.Arrays;


public final class Board {

    public static final int DEFAULT_SIZE = 4;

    private final int size;
    private final int[][] tiles;

    /**
     * Creates an empty 4*4 board
     */
    public Board() {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates an empty square board
     *
     * @param size number of rows and columns
     */
    public Board(int size) {
        if (size < 2) {
            throw new IllegalArgumentException(
                    "Board size must be at least 2"
            );
        }

        this.size = size;
        this.tiles = new int[size][size];
    }

    /**
     * Creates a board containing supplied values
     *
     * @param initialValues initial tile values
     */
    public Board(int[][] initialValues) {
        validateInitialValues(initialValues);

        this.size = initialValues.length;
        this.tiles = copyArray(initialValues);
    }

    /**
     * Returns board size
     *
     * @return number of rows and columns
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns value at a board position
     *
     * @param row row index
     * @param column column index
     * @return tile value, or zero when the cell is empty
     */
    public int getValue(int row, int column) {
        checkPosition(row, column);
        return tiles[row][column];
    }

    /**
     * Changes value at a board position
     *
     * @param row row index
     * @param column column index
     * @param value zero or a positive power of two
     */
    public void setValue(int row, int column, int value) {
        checkPosition(row, column);
        validateTileValue(value);

        tiles[row][column] = value;
    }

    /**
     * Checks whether a position is empty
     *
     * @param row row index
     * @param column column index
     * @return true when the value is zero
     */
    public boolean isEmpty(int row, int column) {
        return getValue(row, column) == 0;
    }

    /**
     * Counts the empty cells on the board
     *
     * @return number of empty cells
     */
    public int countEmptyCells() {
        int count = 0;

        for (int[] row : tiles) {
            for (int value : row) {
                if (value == 0) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Checks whether the board is full
     *
     * @return true when no empty cells remain
     */
    public boolean isFull() {
        return countEmptyCells() == 0;
    }

    /**
     * Checks whether the board contains a particular tile value
     *
     * @param value tile value to find
     * @return true when the value exists
     */
    public boolean contains(int value) {
        for (int[] row : tiles) {
            for (int tile : row) {
                if (tile == value) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes every tile from the board
     */
    public void clear() {
        for (int[] row : tiles) {
            Arrays.fill(row, 0);
        }
    }

    /**
     * Returns a defensive copy of the board values
     *
     * @return copied two-dimensional array
     */
    public int[][] toArray() {
        return copyArray(tiles);
    }


    public Board copy() {
        return new Board(tiles);
    }

    private void checkPosition(int row, int column) {
        if (row < 0 || row >= size || column < 0 || column >= size) {
            throw new IndexOutOfBoundsException(
                    "Invalid board position: (" + row + ", " + column + ")"
            );
        }
    }

    private static void validateInitialValues(int[][] values) {
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException(
                    "Initial board must contain at least two rows"
            );
        }

        int expectedSize = values.length;

        for (int[] row : values) {
            if (row == null || row.length != expectedSize) {
                throw new IllegalArgumentException(
                        "Initial board must be square"
                );
            }

            for (int value : row) {
                validateTileValue(value);
            }
        }
    }

    private static void validateTileValue(int value) {
        boolean isPowerOfTwo =
                value > 0 && (value & (value - 1)) == 0;

        if (value != 0 && !isPowerOfTwo) {
            throw new IllegalArgumentException(
                    "Tile value must be zero or a positive power of two"
            );
        }
    }

    private static int[][] copyArray(int[][] source) {
        int[][] copy = new int[source.length][];

        for (int row = 0; row < source.length; row++) {
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

        return Arrays.deepEquals(tiles, other.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(tiles);
    }
}
