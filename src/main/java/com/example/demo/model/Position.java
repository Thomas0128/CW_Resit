package com.example.demo.model;

/**
 * Identifies one row and column on a game board
 *
 * @param row row index
 * @param column column index
 */
public record Position(int row, int column) {

    /**
     * Validates that the coordinates are non-negative
     */
    public Position {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException(
                    "Position coordinates cannot be negative"
            );
        }
    }
}
