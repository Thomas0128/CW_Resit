package com.example.demo.engine;

import com.example.demo.model.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import com.example.demo.model.Position;

/**
 * Places randomly generated tiles into empty playable board positions
 *
 * <p>A generated tile has a value of 2 in most cases and   a value of 4
 * with a ten-percent probability.</p>
 */
public final class TileSpawner {

    private final Random random;

    /**
     * Creates a tile spawner using a new random-number generator
     */
    public TileSpawner() {
        this(new Random());
    }

    /**
     * Creates a tile spawner using the supplied random-number generator
     *
     * <p>This constructor allows tests to provide deterministic random
     * values.</p>
     *
     * @param random random-number generator used for placement and value selection
     * @throws NullPointerException when {@code random} is {@code null}
     */
    public TileSpawner(Random random) {
        this.random = Objects.requireNonNull(
                random,
                "Random generator cannot be null"
        );
    }

    /**
     * Places one tile in a randomly selected empty playable position
     *
     * @param board board that receives the generated tile
     * @return {@code true} when a tile was placed, or {@code false}
     *         when no empty playable position remains
     * @throws NullPointerException when {@code board} is {@code null}
     */
    public boolean spawn(Board board) {
        Objects.requireNonNull(board, "Board cannot be null");

        List<Position> emptyPositions = findEmptyPositions(board);

        if (emptyPositions.isEmpty()) {
            return false;
        }

        Position selectedPosition = emptyPositions.get(
                random.nextInt(emptyPositions.size())
        );

        int value = random.nextInt(10) == 0 ? 4 : 2;

        board.setValue(
                selectedPosition.row(),
                selectedPosition.column(),
                value
        );

        return true;
    }

        private List<Position> findEmptyPositions(
                Board board
        ) {
            List<Position> emptyPositions =
                    new ArrayList<>();

            for (int row = 0;
                 row < board.getSize();
                 row++) {

                for (int column = 0;
                     column < board.getSize();
                     column++) {

                    if (board.isEmpty(row, column)) {
                        emptyPositions.add(
                                new Position(row, column)
                        );
                    }
                }
            }

            return emptyPositions;
        }



}
