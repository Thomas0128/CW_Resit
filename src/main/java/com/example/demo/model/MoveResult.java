package com.example.demo.model;

/**
 * Describes the result of attempting to move the board
 *
 * @param moved       whether the board changed
 * @param scoreGained score gained from tile merges
 */
public record MoveResult(boolean moved, int scoreGained) {

    /**
     * Validates  the gained score cannot be negative
     */
    public MoveResult {
        if (scoreGained < 0) {
            throw new IllegalArgumentException(
                    "Score gained cannot be negative"
            );
        }
    }

    /**
     * Creates a result representing a invalid or unchanged move
     *
     * @return a result with no movement and no score
     */
    public static MoveResult noMove() {
        return new MoveResult(false, 0);
    }
}
