package com.example.demo.level;

import com.example.demo.model.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Stores the rules used to configure a playable 2048 level
 *
 * @param name human-readable level name
 * @param boardSize number of rows and columns
 * @param targetTile tile required to complete the level
 * @param startingTileCount number of tiles placed at the start
 * @param obstacles positions that cannot contain or move tiles
 */
public record LevelConfig(
        String name,
        int boardSize,
        int targetTile,
        int startingTileCount,
        List<Position> obstacles
) {

    /**
     * Creates a level without obstacle positions.
     *
     * @param name human-readable level name
     * @param boardSize number of rows and columns
     * @param targetTile tile required to complete the level
     * @param startingTileCount number of tiles placed at the start
     */
    public LevelConfig(
            String name,
            int boardSize,
            int targetTile,
            int startingTileCount
    ) {
        this(
                name,
                boardSize,
                targetTile,
                startingTileCount,
                List.of()
        );
    }

    /**
     * Validates the supplied level configuration
     */
    public LevelConfig {
        Objects.requireNonNull(
                name,
                "Level name cannot be null"
        );

        name = name.trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Level name cannot be blank"
            );
        }

        if (boardSize < 2) {
            throw new IllegalArgumentException(
                    "Board size must be at least 2"
            );
        }

        if (!isPositivePowerOfTwo(targetTile)) {
            throw new IllegalArgumentException(
                    "Target tile must be a positive power of two"
            );
        }

        Objects.requireNonNull(
                obstacles,
                "Obstacle list cannot be null"
        );

        if (obstacles.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Obstacle positions cannot be null"
            );
        }

        obstacles = List.copyOf(obstacles);

        if (new HashSet<>(obstacles).size()
                != obstacles.size()) {

            throw new IllegalArgumentException(
                    "Obstacle positions cannot be duplicated"
            );
        }

        for (Position obstacle : obstacles) {
            if (obstacle.row() >= boardSize
                    || obstacle.column() >= boardSize) {

                throw new IllegalArgumentException(
                        "Obstacle position must fit on the board"
                );
            }
        }

        int playableCellCount =
                boardSize * boardSize - obstacles.size();

        if (startingTileCount < 1
                || startingTileCount > playableCellCount) {

            throw new IllegalArgumentException(
                    "Starting tile count must fit "
                            + "on playable board cells"
            );
        }
    }

    /**
     * Checks whether this level contains obstacles
     *
     * @return true when at least one obstacle is configured
     */
    public boolean hasObstacles() {
        return !obstacles.isEmpty();
    }

    private static boolean isPositivePowerOfTwo(int value) {
        return value > 0
                && (value & (value - 1)) == 0;
    }
}
