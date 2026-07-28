package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardObstacleTest {

    @Test
    void obstaclesAreNotCountedAsEmptyCells() {
        Board board = new Board(
                4,
                List.of(
                        new Position(1, 1),
                        new Position(2, 2)
                )
        );

        assertEquals(14, board.countEmptyCells());
        assertEquals(14, board.getPlayableCellCount());

        assertTrue(board.isObstacle(1, 1));
        assertFalse(board.isEmpty(1, 1));
    }

    @Test
    void tileCannotBePlacedOnObstacle() {
        Board board = new Board(
                4,
                List.of(new Position(1, 1))
        );

        assertThrows(
                IllegalStateException.class,
                () -> board.setValue(1, 1, 2)
        );

        assertEquals(0, board.getValue(1, 1));
    }

    @Test
    void rejectsObstacleOutsideBoard() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Board(
                        4,
                        List.of(new Position(4, 0))
                )
        );
    }

    @Test
    void copyPreservesTilesAndObstacles() {
        Board board = new Board(
                4,
                List.of(new Position(1, 1))
        );

        board.setValue(0, 0, 2);

        Board copy = board.copy();

        assertEquals(board, copy);
        assertNotSame(board, copy);
        assertTrue(copy.isObstacle(1, 1));

        copy.setValue(0, 0, 4);

        assertEquals(2, board.getValue(0, 0));
        assertEquals(4, copy.getValue(0, 0));
    }
}
