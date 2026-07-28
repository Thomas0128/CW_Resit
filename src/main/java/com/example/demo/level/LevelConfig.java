package com.example.demo.level;

import java.util.Objects;

/**
 * Stores the rules used to configure a playable 2048 level
 *
 * @param name human-readable level name
 * @param boardSize number of rows and columns
 * @param targetTile tile required to complete the level
 * @param startingTileCount number of tiles placed at the start
 */
public record LevelConfig(
        String name,
        int boardSize,
        int targetTile,
        int startingTileCount
) {

    /**
     * Validates the supplied level configuration
     */
    public LevelConfig {
        Objects.requireNonNull(name, "Level name cannot be null");

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

        int maximumTileCount = boardSize * boardSize;

        if (startingTileCount < 1
                || startingTileCount > maximumTileCount) {
            throw new IllegalArgumentException(
                    "Starting tile count must fit on the board"
            );
        }
    }

    private static boolean isPositivePowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }
}
