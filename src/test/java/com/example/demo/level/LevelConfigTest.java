package com.example.demo.level;

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
    }

    @Test
    void largeBoardLevelUsesFiveByFiveBoard() {
        LevelConfig level = LevelCatalog.LARGE_BOARD;

        assertEquals("Large Board", level.name());
        assertEquals(5, level.boardSize());
        assertEquals(4096, level.targetTile());
        assertEquals(2, level.startingTileCount());
    }

    @Test
    void catalogReturnsBothPlayableLevels() {
        List<LevelConfig> levels =
                LevelCatalog.playableLevels();

        assertEquals(2, levels.size());
        assertTrue(levels.contains(LevelCatalog.CLASSIC));
        assertTrue(levels.contains(LevelCatalog.LARGE_BOARD));
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
}
