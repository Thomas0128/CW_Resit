package com.example.demo.engine;

import com.example.demo.model.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


    public final class TileSpawner {

    private final Random random;

    public TileSpawner() {
        this(new Random());
    }


    public TileSpawner(Random random) {
        this.random = Objects.requireNonNull(
                random,
                "Random generator cannot be null"
        );
    }


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

    private List<Position> findEmptyPositions(Board board) {
        List<Position> emptyPositions = new ArrayList<>();

        for (int row = 0; row < board.getSize(); row++) {
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


    private record Position(int row, int column) {
    }
}
