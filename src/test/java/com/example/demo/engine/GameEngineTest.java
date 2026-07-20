package com.example.demo.engine;

import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the movement and state rules of {@link GameEngine}.
 */
class GameEngineTest {

    @Test
    void movingLeftMergesFourEqualTilesIntoTwoPairs() {
        Board board = new Board(new int[][]{
                {2, 2, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameEngine engine = new GameEngine(board);

        MoveResult result = engine.move(Direction.LEFT);

        assertTrue(result.moved());
        assertEquals(8, result.scoreGained());

        assertBoardEquals(new int[][]{
                {4, 4, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, board);
    }

    @Test
    void movingLeftAllowsEachTileToMergeOnlyOnce() {
        Board board = new Board(new int[][]{
                {4, 4, 4, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameEngine engine = new GameEngine(board);

        MoveResult result = engine.move(Direction.LEFT);

        assertTrue(result.moved());
        assertEquals(8, result.scoreGained());

        assertBoardEquals(new int[][]{
                {8, 4, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, board);
    }

    @Test
    void movingLeftCompactsTilesBeforeMerging() {
        Board board = new Board(new int[][]{
                {2, 0, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameEngine engine = new GameEngine(board);

        MoveResult result = engine.move(Direction.LEFT);

        assertTrue(result.moved());
        assertEquals(4, result.scoreGained());

        assertBoardEquals(new int[][]{
                {4, 2, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, board);
    }

    @Test
    void unchangedMoveReturnsNoMovementAndNoScore() {
        Board board = new Board(new int[][]{
                {2, 4, 8, 16},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameEngine engine = new GameEngine(board);

        MoveResult result = engine.move(Direction.LEFT);

        assertFalse(result.moved());
        assertEquals(0, result.scoreGained());

        assertBoardEquals(new int[][]{
                {2, 4, 8, 16},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, board);
    }

    @Test
    void fullBoardWithoutMatchingNeighboursIsGameOver() {
        Board board = new Board(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        GameEngine engine = new GameEngine(board);

        assertFalse(engine.canMove());
        assertTrue(engine.isGameOver());
    }

    @Test
    void matchingTilesInFinalRowStillAllowMovement() {
        Board board = new Board(new int[][]{
                {2, 4, 8, 16},
                {4, 8, 16, 32},
                {8, 16, 32, 128},
                {16, 32, 64, 64}
        });

        GameEngine engine = new GameEngine(board);

        assertTrue(engine.canMove());
        assertFalse(engine.isGameOver());
    }

    @Test
    void matchingTilesInFinalColumnStillAllowMovement() {
        Board board = new Board(new int[][]{
                {2, 4, 8, 16},
                {4, 8, 16, 32},
                {8, 16, 32, 64},
                {16, 32, 128, 64}
        });

        GameEngine engine = new GameEngine(board);

        assertTrue(engine.canMove());
        assertFalse(engine.isGameOver());
    }

    @Test
    void engineDetectsWhenTargetTileHasBeenReached() {
        Board board = new Board(new int[][]{
                {2048, 4, 0, 0},
                {8, 16, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameEngine engine = new GameEngine(board);

        assertTrue(engine.hasReachedTarget(2048));
        assertFalse(engine.hasReachedTarget(4096));
    }

    private static void assertBoardEquals(
            int[][] expected,
            Board actualBoard
    ) {
        int[][] actual = actualBoard.toArray();

        assertEquals(
                expected.length,
                actual.length,
                "Board row count differs"
        );

        for (int row = 0; row < expected.length; row++) {
            assertArrayEquals(
                    expected[row],
                    actual[row],
                    "Different values in row " + row
            );
        }
    }
}
