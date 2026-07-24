package com.example.demo.ai;

import com.example.demo.engine.GameEngine;
import com.example.demo.model.Board;
import com.example.demo.model.Direction;
import com.example.demo.model.MoveResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the heuristic AI hint service
 */
class HintServiceTest {

    private final HintService hintService =
            new HintService();

    @Test
    void usesStableDirectionWhenTwoMovesHaveEqualScores() {
        Board board = new Board(new int[][]{
                {2, 2, 4, 8},
                {16, 32, 64, 128},
                {256, 512, 1024, 2},
                {4, 8, 16, 32}
        });

        Direction suggestion =
                hintService.suggestMove(board)
                        .orElseThrow();

        /*
         * LEFT and RIGHT both perform the same merge
         * The fixed search order selects LEFT consistently
         */
        assertEquals(Direction.LEFT, suggestion);
    }

    @Test
    void returnsEmptyWhenNoValidMoveExists() {
        Board board = new Board(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        Optional<Direction> suggestion =
                hintService.suggestMove(board);

        assertTrue(suggestion.isEmpty());
    }

    @Test
    void analysingHintDoesNotModifyOriginalBoard() {
        int[][] originalValues = {
                {2, 0, 2, 4},
                {4, 8, 16, 32},
                {0, 0, 0, 0},
                {2, 4, 8, 16}
        };

        Board board = new Board(originalValues);

        hintService.suggestMove(board);

        assertTrue(
                Arrays.deepEquals(
                        originalValues,
                        board.toArray()
                )
        );
    }

    @Test
    void suggestedDirectionAlwaysProducesValidMove() {
        Board board = new Board(new int[][]{
                {2, 0, 0, 0},
                {4, 4, 0, 0},
                {8, 16, 32, 64},
                {0, 0, 0, 0}
        });

        Direction suggestion =
                hintService.suggestMove(board)
                        .orElseThrow();

        Board simulation = board.copy();

        MoveResult result =
                new GameEngine(simulation)
                        .move(suggestion);

        assertTrue(result.moved());
    }
}
