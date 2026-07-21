package com.example.demo.engine;

import com.example.demo.model.Board;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TileSpawner}.
 */
class TileSpawnerTest {

    @Test
    void spawnPlacesTileInOnlyAvailablePosition() {
        Board board = new Board(new int[][]{
                {2, 4, 8, 16},
                {4, 8, 16, 32},
                {8, 16, 32, 64},
                {16, 32, 64, 0}
        });

        TileSpawner spawner =
                new TileSpawner(new SequenceRandom(0, 5));

        boolean spawned = spawner.spawn(board);

        assertTrue(spawned);
        assertEquals(2, board.getValue(3, 3));
        assertTrue(board.isFull());
    }

    @Test
    void spawnReturnsFalseWhenBoardIsFull() {
        Board board = new Board(new int[][]{
                {2, 4, 8, 16},
                {4, 8, 16, 32},
                {8, 16, 32, 64},
                {16, 32, 64, 128}
        });

        TileSpawner spawner = new TileSpawner();

        boolean spawned = spawner.spawn(board);

        assertFalse(spawned);
    }

    @Test
    void spawnAddsExactlyOneTile() {
        Board board = new Board();

        int emptyCellsBefore = board.countEmptyCells();

        TileSpawner spawner =
                new TileSpawner(new SequenceRandom(4, 5));

        boolean spawned = spawner.spawn(board);

        assertTrue(spawned);
        assertEquals(
                emptyCellsBefore - 1,
                board.countEmptyCells()
        );
    }

    @Test
    void randomRollOfZeroCreatesFour() {
        Board board = new Board();

        TileSpawner spawner =
                new TileSpawner(new SequenceRandom(0, 0));

        spawner.spawn(board);

        assertEquals(4, board.getValue(0, 0));
    }

    @Test
    void nonZeroRandomRollCreatesTwo() {
        Board board = new Board();

        TileSpawner spawner =
                new TileSpawner(new SequenceRandom(0, 9));

        spawner.spawn(board);

        assertEquals(2, board.getValue(0, 0));
    }

    /**
     * Predictable random-number generator used only by these tests.
     */
    private static final class SequenceRandom extends Random {

        private final int[] values;
        private int currentIndex;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            if (currentIndex >= values.length) {
                throw new IllegalStateException(
                        "No configured random value remains"
                );
            }

            int value = values[currentIndex];
            currentIndex++;

            if (value < 0 || value >= bound) {
                throw new IllegalArgumentException(
                        "Configured value must be below " + bound
                );
            }

            return value;
        }
    }
}
