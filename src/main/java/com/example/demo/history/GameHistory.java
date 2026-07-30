package com.example.demo.history;

import com.example.demo.model.GameState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores game snapshots used by the undo feature.
 *
 * <p>States are restored in last-in, first-out order.</p>
 */
public final class GameHistory {

    private final Deque<GameState> states =
            new ArrayDeque<>();

    /**
     * Creates an empty game history.
     */
    public GameHistory() {
    }

    /**
     * Saves a state before a valid move is performed.
     *
     * @param state game state to store
     */
    public void save(GameState state) {
        states.addLast(
                Objects.requireNonNull(
                        state,
                        "Game state cannot be null"
                )
        );
    }

    /**
     * Removes and returns the latest saved state.
     *
     * @return latest state, or an empty Optional when undo is unavailable
     */
    public Optional<GameState> undo() {
        return Optional.ofNullable(states.pollLast());
    }

    /**
     * Checks whether at least one saved state is available.
     *
     * @return {@code true} when undo can be performed
     */
    public boolean canUndo() {
        return !states.isEmpty();
    }

    /**
     * Removes all saved states.
     */
    public void clear() {
        states.clear();
    }

    /**
     * Returns the number of stored states.
     *
     * @return history size
     */
    public int size() {
        return states.size();
    }
}
