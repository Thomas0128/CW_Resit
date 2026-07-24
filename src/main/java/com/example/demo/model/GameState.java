package com.example.demo.model;

import java.util.Objects;

/**
 * Immutable snapshot of the information required to restore a game
 *
 * <p>The board is defensively copied so later changes to the active
 * game cannot modify a previously saved state.</p>
 *
 * @param board saved board state
 * @param score saved score
 */
public record GameState(Board board, long score) {

    /**
     * Creates a protected snapshot of a game
     *
     * @throws NullPointerException if the board is null
     * @throws IllegalArgumentException if the score is negative
     */
    public GameState {
        Objects.requireNonNull(
                board,
                "Board cannot be null"
        );

        if (score < 0) {
            throw new IllegalArgumentException(
                    "Score cannot be negative"
            );
        }

        board = board.copy();
    }

    /**
     * Returns an independent copy of the saved board
     *
     * @return copied board
     */
    @Override
    public Board board() {
        return board.copy();
    }
}
