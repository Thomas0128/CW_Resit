package com.example.demo.level;

import com.example.demo.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelConfigTest {

    @Test
    void classicLevelUsesOriginalGameRules() {
        LevelConfig level = LevelCatalog.CLASSIC;

        assertEquals("Classic", level.name());
        assertEquals(4, level.boardSize());
        assertEquals(2048, level.targetTile());
        assertEquals(2, level.startingTileCount());
        assertTrue(level.obstacles().isEmpty());
        assertFalse(level.hasObstacles());
    }

    @Test
    void largeBoardLevelUsesFiveByFiveBoard() {
        LevelConfig level = LevelCatalog.LARGE_BOARD;

        assertEquals("Large Board", level.name());
        assertEquals(5, level.boardSize());
        assertEquals(4096, level.targetTile());
        assertEquals(2, level.startingTileCount());
        assertTrue(level.obstacles().isEmpty());
    }

    @Test
    void obstacleChallengeContainsBlockedPositions() {
        LevelConfig level =
                LevelCatalog.OBSTACLE_CHALLENGE;

        assertEquals(
                "Obstacle Challenge",
                level.name()
        );

        assertEquals(4, level.boardSize());
        assertEquals(1024, level.targetTile());
        assertEquals(2, level.startingTileCount());
        assertTrue(level.hasObstacles());

        assertEquals(
                List.of(
                        new Position(1, 1),
                        new Position(2, 2)
                ),
                level.obstacles()
        );
    }

    @Test
    void catalogReturnsAllThreePlayableLevels() {
        List<LevelConfig> levels =
                LevelCatalog.playableLevels();

        assertEquals(3, levels.size());
        assertTrue(levels.contains(LevelCatalog.CLASSIC));
        assertTrue(levels.contains(LevelCatalog.LARGE_BOARD));

        assertTrue(
                levels.contains(
                        LevelCatalog.OBSTACLE_CHALLENGE
                )
        );
    }

    @Test
    void rejectsBoardSmallerThanTwoByTwo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        1,
                        2048,
                        2
                )
        );
    }

    @Test
    void rejectsTargetThatIsNotPowerOfTwo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        4,
                        3000,
                        2
                )
        );
    }

    @Test
    void rejectsStartingTileCountLargerThanBoard() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        2,
                        2048,
                        5
                )
        );
    }

    @Test
    void rejectsObstacleOutsideBoard() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        4,
                        2048,
                        2,
                        List.of(
                                new Position(4, 1)
                        )
                )
        );
    }

    @Test
    void rejectsDuplicateObstaclePositions() {
        Position obstacle =
                new Position(1, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        4,
                        2048,
                        2,
                        List.of(
                                obstacle,
                                obstacle
                        )
                )
        );
    }

    @Test
    void startingTilesMustFitOnPlayableCells() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LevelConfig(
                        "Invalid",
                        2,
                        2048,
                        2,
                        List.of(
                                new Position(0, 0),
                                new Position(0, 1),
                                new Position(1, 0)
                        )
                )
        );
    }
}
