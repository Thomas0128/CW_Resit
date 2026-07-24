package com.example.demo.history;

import com.example.demo.model.Board;
import com.example.demo.model.GameState;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the undo history
 */
class GameHistoryTest {

    @Test
    void savedStateCanBeRestored() {
        Board board = new Board(new int[][]{
                {2, 4, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        GameHistory history = new GameHistory();
        history.save(new GameState(board, 20));

        Optional<GameState> restoredState = history.undo();

        assertTrue(restoredState.isPresent());
        assertEquals(20, restoredState.get().score());
        assertEquals(board, restoredState.get().board());
    }

    @Test
    void undoRestoresMostRecentStateFirst() {
        GameHistory history = new GameHistory();

        Board firstBoard = new Board();
        firstBoard.setValue(0, 0, 2);

        Board secondBoard = new Board();
        secondBoard.setValue(0, 0, 4);

        history.save(new GameState(firstBoard, 0));
        history.save(new GameState(secondBoard, 4));

        GameState firstUndo = history.undo().orElseThrow();
        GameState secondUndo = history.undo().orElseThrow();

        assertEquals(4, firstUndo.board().getValue(0, 0));
        assertEquals(4, firstUndo.score());

        assertEquals(2, secondUndo.board().getValue(0, 0));
        assertEquals(0, secondUndo.score());
    }

    @Test
    void undoReturnsEmptyWhenNoStateExists() {
        GameHistory history = new GameHistory();

        assertFalse(history.canUndo());
        assertTrue(history.undo().isEmpty());
    }

    @Test
    void clearRemovesAllSavedStates() {
        GameHistory history = new GameHistory();

        history.save(new GameState(new Board(), 0));
        history.save(new GameState(new Board(), 10));

        assertEquals(2, history.size());

        history.clear();

        assertEquals(0, history.size());
        assertFalse(history.canUndo());
    }

    @Test
    void savedStateIsNotChangedByActiveBoardModification() {
        Board activeBoard = new Board();
        activeBoard.setValue(0, 0, 2);

        GameState savedState = new GameState(activeBoard, 0);

        activeBoard.setValue(0, 0, 8);

        assertEquals(2, savedState.board().getValue(0, 0));
        assertEquals(8, activeBoard.getValue(0, 0));
    }

    @Test
    void modifyingReturnedBoardDoesNotChangeSavedState() {
        Board board = new Board();
        board.setValue(0, 0, 2);

        GameState state = new GameState(board, 0);

        Board returnedBoard = state.board();
        returnedBoard.setValue(0, 0, 16);

        assertEquals(2, state.board().getValue(0, 0));
    }
}
