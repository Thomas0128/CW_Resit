package com.example.demo.level;

import com.example.demo.model.Position;

import java.util.List;

/**
 * Provides the predefined levels available to the player
 */
public final class LevelCatalog {

    /**
     * Original 2048 rules using a 4 × 4 board
     */
    public static final LevelConfig CLASSIC =
            new LevelConfig(
                    "Classic",
                    4,
                    2048,
                    2
            );

    /**
     * Larger board with a higher winning target
     */
    public static final LevelConfig LARGE_BOARD =
            new LevelConfig(
                    "Large Board",
                    5,
                    4096,
                    2
            );

    /**
     * A 4 × 4 challenge containing two blocked cells
     */
    public static final LevelConfig OBSTACLE_CHALLENGE =
            new LevelConfig(
                    "Obstacle Challenge",
                    4,
                    1024,
                    2,
                    List.of(
                            new Position(1, 1),
                            new Position(2, 2)
                    )
            );

    private LevelCatalog() {
        // Utility class.
    }

    /**
     * Returns all currently playable levels
     *
     * @return immutable list of level configurations
     */
    public static List<LevelConfig> playableLevels() {
        return List.of(
                CLASSIC,
                LARGE_BOARD,
                OBSTACLE_CHALLENGE
        );
    }
}
