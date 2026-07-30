# CW_Resit — Maintained and Extended 2048

This repository contains a maintained and extended version of the original JavaFX 2048 coursework project. The original game was first analysed as a baseline, then its core rules were separated from the JavaFX interface, defects were corrected, automated tests were added, and several new gameplay features were introduced.

---

## GitHub

Repository:

**https://github.com/Thomas0128/CW_Resit**

The repository uses `master` as the original baseline and `develop` as the integration branch. Maintenance, refactoring, testing, and feature work were completed on separate branches and merged through pull requests so that the development history remains clear.

---

## Compilation Instructions

### Requirements

- **JDK 21** — a full JDK is required, not only a JRE.
- **JavaFX 21.0.6** — downloaded automatically by Maven.
- **JUnit 5.12.1** — downloaded automatically by Maven for testing.
- **Maven Wrapper** — included in the repository, so a separate Maven installation is not required.
- Internet access is required the first time Maven downloads dependencies.
- Development and final verification were performed on **Windows 11** using **IntelliJ IDEA** and **Temurin JDK 21.0.11**.

### 1. Clone the repository

```powershell
git clone https://github.com/Thomas0128/CW_Resit.git
cd CW_Resit
```

Alternatively, extract the submitted project ZIP and open a terminal in the extracted project directory.

### 2. Verify Java

```powershell
java -version
javac -version
```

Both commands should report Java 21.

### 3. Configure `JAVA_HOME` when required

The Maven Wrapper requires `JAVA_HOME` to point to the JDK root directory.

For the current PowerShell session:

```powershell
$javaExe = (Get-Command java).Source
$jdkHome = Split-Path (Split-Path $javaExe -Parent) -Parent
$env:JAVA_HOME = $jdkHome
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

To save it for the current Windows user:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", $jdkHome, "User")
```

After saving the variable, reopen PowerShell and confirm:

```powershell
$env:JAVA_HOME
.\mvnw.cmd --version
```

### 4. Run the automated tests

```powershell
.\mvnw.cmd clean test
```

Expected final result:

```text
Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 5. Compile and package the application

```powershell
.\mvnw.cmd clean package
```

The compiled output is generated under:

```text
target/
```

### 6. Run the JavaFX application

```powershell
.\mvnw.cmd javafx:run
```

The level-selection menu should appear. The player can then choose Classic, Large Board, or Obstacle Challenge.

### macOS or Linux equivalents

Use the included Unix Maven Wrapper:

```bash
./mvnw clean test
./mvnw clean package
./mvnw javafx:run
```

### IntelliJ IDEA setup

1. Open the repository root as an IntelliJ project.
2. Set **Project SDK** to JDK 21.
3. Set the Maven importer and runner JDK to **Project SDK**.
4. Reload the Maven project after changing `pom.xml`.
5. Run through Maven using the `javafx:run` goal, or use the terminal command above.

---

## Controls

- **Arrow keys** — move all tiles.
- **Undo** — restore the board and score from before the most recent valid move.
- **Hint** — recommend a valid direction using a deterministic heuristic.
- **Restart** — restart the currently selected level.
- **Level Menu** — return to the level-selection screen.

A new tile is created only after a valid move that changes the board. Non-direction keys do not alter the game.

---

## Implemented and Working

### 1. Java 21 Maven build configuration

**Location:** `pom.xml`

The original project requested Java 25 even though the available development environment used Java 21. The compiler configuration was changed to:

```xml
<release>21</release>
```

The Maven Surefire plugin was also configured for JUnit 5 execution with the module path disabled during tests. This change was necessary to produce a repeatable build using the selected JDK and to run the automated test suite reliably.

### 2. Independent board model

**Location:** `src/main/java/com/example/demo/model/Board.java`

Tile values and obstacle positions are now stored in a pure Java model instead of being read from JavaFX `Text` nodes. The class validates board dimensions and tile values, provides defensive copies, counts playable empty cells, preserves obstacles, and supports equality for testing.

**Reason:** The original game data was tightly coupled to the visual interface, which made the rules difficult to test and maintain.

### 3. Correct movement, merging, and scoring

**Location:** `src/main/java/com/example/demo/engine/GameEngine.java`

The movement algorithm now:

- Supports all four directions.
- Compacts tiles before merging.
- Allows each tile to merge only once per move.
- Returns whether the board actually changed.
- Returns only the score gained from newly merged tiles.
- Handles final-row and final-column moves correctly.
- Uses the same movement simulation for game-over detection.
- Prevents tiles from crossing obstacle cells.

**Reason:** This replaces the original JavaFX-dependent movement code and fixes incorrect multiple merges, incorrect score accumulation, and boundary errors.

### 4. Valid-move detection

**Locations:**

- `src/main/java/com/example/demo/engine/GameEngine.java`
- `src/main/java/com/example/demo/GameScene.java`

`MoveResult.moved()` is checked before saving history, adding score, or spawning a tile. Non-direction keys are ignored.

**Reason:** The original program could generate a new tile after an invalid direction or unrelated keyboard input.

### 5. Fair random tile spawning

**Location:** `src/main/java/com/example/demo/engine/TileSpawner.java`

The spawner collects every empty playable position and selects one uniformly. It creates a `2` in 90% of spawns and a `4` in 10% of spawns. It returns `false` when no playable empty position exists and never places a tile on an obstacle.

**Reason:** The original two-dimensional temporary-array algorithm excluded or biased some empty cells.

### 6. Correct game-over and target detection

**Locations:**

- `src/main/java/com/example/demo/engine/GameEngine.java`
- `src/main/java/com/example/demo/GameScene.java`

The game is over only when none of the four directions can change the board. A level target is detected by searching the complete model board. When the target tile is reached, the interface displays a completion message and allows the player to continue.

**Reason:** The original logic could miss valid merges on the board boundaries and could return before checking the complete board for a 2048 tile.

### 7. Level-selection system

**Locations:**

- `src/main/java/com/example/demo/level/LevelConfig.java`
- `src/main/java/com/example/demo/level/LevelCatalog.java`
- `src/main/java/com/example/demo/Main.java`
- `src/main/java/com/example/demo/GameScene.java`

Three playable configurations are available:

| Level | Board | Target | Special rules |
|---|---:|---:|---|
| Classic | 4 × 4 | 2048 | Standard 2048 |
| Large Board | 5 × 5 | 4096 | Larger board and higher target |
| Obstacle Challenge | 4 × 4 | 1024 | Two blocked cells at `(1,1)` and `(2,2)` |

`LevelConfig` validates names, sizes, targets, starting tile counts, duplicate obstacles, and out-of-range obstacle positions.

**Reason:** A configuration object avoids duplicating level-specific logic and makes future levels easier to add.

### 8. Obstacle-aware gameplay

**Locations:**

- `src/main/java/com/example/demo/model/Board.java`
- `src/main/java/com/example/demo/engine/GameEngine.java`
- `src/main/java/com/example/demo/engine/TileSpawner.java`
- `src/main/java/com/example/demo/ai/HintService.java`
- `src/main/java/com/example/demo/GameScene.java`

Obstacle cells are not counted as empty, cannot receive tiles, divide rows and columns into independent movement segments, and are rendered as dark cells containing `X`.

**Reason:** This adds a genuinely different playable challenge while keeping obstacle behaviour consistent across the model, engine, spawner, hint system, and UI.

### 9. Undo feature

**Locations:**

- `src/main/java/com/example/demo/model/GameState.java`
- `src/main/java/com/example/demo/history/GameHistory.java`
- `src/main/java/com/example/demo/GameScene.java`

Before each valid move, an immutable snapshot of the board and score is stored. Undo restores snapshots in last-in, first-out order. Invalid moves are not added to the history, and restart or level changes clear the history.

**Reason:** This improves the player experience and demonstrates snapshot-based state restoration similar to the Memento design pattern.

### 10. AI Hint feature

**Locations:**

- `src/main/java/com/example/demo/ai/HintService.java`
- `src/main/java/com/example/demo/GameScene.java`

The service simulates each valid direction on a copied board and evaluates the result using:

- Number of playable empty cells.
- Score gained by the simulated merge.
- Maximum tile value.
- A bonus when the maximum tile is in a corner.

A fixed search order makes equal-scoring results deterministic. The service is obstacle-aware and never changes the active board while analysing moves.

**Reason:** This adds an explainable AI-related feature suitable for the project while remaining testable and maintainable.

### 11. Updated JavaFX interface

**Locations:**

- `src/main/java/com/example/demo/Main.java`
- `src/main/java/com/example/demo/GameScene.java`

The application now begins with a level-selection menu. The game screen displays the selected level, board size, target, score, hint text, target status, and controls for Undo, Hint, Restart, and Level Menu. Board rendering is rebuilt from the model after each state change.

**Reason:** The original application created several scenes but immediately entered the game and did not provide a usable menu or access to extended features.

### 12. Automated testing

**Location:** `src/test/java/com/example/demo/`

The final suite contains **45 JUnit 5 tests**:

| Test class | Tests | Main coverage |
|---|---:|---|
| `GameEngineTest` | 13 | Movement, merging, scoring, game over, targets, obstacle segments |
| `TileSpawnerTest` | 6 | Empty-cell selection, values 2/4, full boards, obstacles |
| `GameHistoryTest` | 6 | Snapshot order, restoration, clearing, defensive copies |
| `HintServiceTest` | 6 | Deterministic hints, valid moves, board safety, obstacles |
| `LevelConfigTest` | 10 | Level definitions and validation |
| `BoardObstacleTest` | 4 | Obstacle behaviour and board copying |

Recorded result:

```text
Tests run: 45
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

---

## Implemented but Not Working Correctly

### Legacy game-over `QUIT` action

**Location:** `src/main/java/com/example/demo/EndGame.java`, method `endGameShow(...)`

The confirmation dialog opens correctly, but choosing **OK** only clears the end-game scene's nodes. It does not close the application or return to the level menu.

**Steps taken:** The maintained game correctly detects game over, disables further board input, changes to the end-game scene, and displays the final score. The legacy button action itself was not redesigned during the core refactoring.

**Suggested future fix:** Replace `root.getChildren().clear()` with either `primaryStage.close()` for a true quit action, or navigate back to the level-selection scene.

No other implemented gameplay feature is currently known to fail. The automated core test suite passes all 45 tests.

---

## Unimplemented Functionality

### 1. Account and leaderboard integration

**Existing legacy location:** `src/main/java/com/example/demo/Account.java`

The original repository contained an `Account` class, but the account and ranking flow was not connected to the running application and no persistent storage was provided. This functionality remains outside the final gameplay flow.

**Reason:** Development effort was prioritised toward correcting the core game, separating responsibilities, adding automated tests, and delivering the three major extensions: levels, Undo, and AI Hint.

### 2. Persistent save and resume

Game states are stored only in memory for Undo. Closing the application removes the current game and history.

**Reason:** File or database persistence was not required for the selected maintenance and extension scope and would have added additional failure modes near final integration.

### 3. Legacy FXML demonstration screen

**Locations:**

- `src/main/resources/com/example/demo/hello-view.fxml`
- `src/main/java/com/example/demo/sample.fxml`
- `src/main/java/com/example/demo/Controller.java`

The submitted game uses programmatically constructed JavaFX scenes. The legacy `hello-view.fxml` references a `HelloController` class that does not exist and is not loaded by the application.

**Reason:** The original FXML files were unrelated to the maintained game flow. They were retained as legacy project resources rather than being presented as completed functionality.

---

## New Java Classes

### Production classes

| Class | Location | Purpose |
|---|---|---|
| `Board` | `src/main/java/com/example/demo/model/Board.java` | Stores validated tile values and obstacle positions independently of JavaFX. |
| `Direction` | `src/main/java/com/example/demo/model/Direction.java` | Represents `UP`, `DOWN`, `LEFT`, and `RIGHT` without character flags. |
| `Position` | `src/main/java/com/example/demo/model/Position.java` | Immutable row/column coordinate used for obstacles and tile positions. |
| `MoveResult` | `src/main/java/com/example/demo/model/MoveResult.java` | Reports whether a move changed the board and how much score it gained. |
| `GameState` | `src/main/java/com/example/demo/model/GameState.java` | Immutable defensive snapshot of a board and score for Undo. |
| `GameEngine` | `src/main/java/com/example/demo/engine/GameEngine.java` | Implements movement, merging, scoring, valid-move checks, targets, and obstacle boundaries. |
| `TileSpawner` | `src/main/java/com/example/demo/engine/TileSpawner.java` | Selects a playable empty cell and generates a 2 or 4. |
| `GameHistory` | `src/main/java/com/example/demo/history/GameHistory.java` | Stores and restores game snapshots in last-in, first-out order. |
| `HintService` | `src/main/java/com/example/demo/ai/HintService.java` | Simulates and scores valid moves to recommend a direction. |
| `LevelConfig` | `src/main/java/com/example/demo/level/LevelConfig.java` | Immutable, validated definition of one playable level. |
| `LevelCatalog` | `src/main/java/com/example/demo/level/LevelCatalog.java` | Supplies the Classic, Large Board, and Obstacle Challenge configurations. |

### Test classes

| Class | Location | Purpose |
|---|---|---|
| `GameEngineTest` | `src/test/java/com/example/demo/engine/GameEngineTest.java` | Verifies movement, merging, score, game-over, target, and obstacle rules. |
| `TileSpawnerTest` | `src/test/java/com/example/demo/engine/TileSpawnerTest.java` | Verifies spawn location, spawn value, full-board behaviour, and obstacles. |
| `GameHistoryTest` | `src/test/java/com/example/demo/history/GameHistoryTest.java` | Verifies Undo ordering, clearing, restoration, and snapshot independence. |
| `HintServiceTest` | `src/test/java/com/example/demo/ai/HintServiceTest.java` | Verifies deterministic, valid, non-mutating, obstacle-aware suggestions. |
| `LevelConfigTest` | `src/test/java/com/example/demo/level/LevelConfigTest.java` | Verifies predefined levels and rejects invalid configurations. |
| `BoardObstacleTest` | `src/test/java/com/example/demo/model/BoardObstacleTest.java` | Verifies blocked cells, empty-cell counting, validation, and copying. |

---

## Modified Java Classes

### `Main`

**Location:** `src/main/java/com/example/demo/Main.java`

**Changes:**

- Replaced the original direct launch into one fixed game with a level-selection menu.
- Added buttons and descriptions for Classic, Large Board, and Obstacle Challenge.
- Added instructions for movement, Undo, and Hint.
- Added `createLevelButton(...)` to centralise level-start behaviour.
- Reused one game scene while passing the selected `LevelConfig`.

**Why necessary:** The original class created multiple unused scenes and then immediately opened the game. The revised class provides a clear application entry point and exposes the new playable levels.

### `GameScene`

**Location:** `src/main/java/com/example/demo/GameScene.java`

**Changes:**

- Removed movement, merge, score, random generation, and end-state rules from the JavaFX cell array.
- Connected the interface to `Board`, `GameEngine`, and `TileSpawner`.
- Added model-based board rendering.
- Added valid-input filtering and changed-board checking.
- Added score updates based only on `MoveResult.scoreGained()`.
- Added Undo and game-state restoration.
- Added heuristic Hint integration.
- Added Restart and Level Menu controls.
- Added level name, board details, target status, and target-completion alert.
- Added dynamic board sizing for 4 × 4 and 5 × 5 levels.
- Added obstacle rendering and obstacle-aware gameplay integration.
- Disabled board input after game over.

**Why necessary:** The original class combined game data, rules, random generation, scoring, JavaFX rendering, keyboard handling, and scene switching. Moving rules into independent classes reduces responsibilities and makes the most error-prone behaviour testable.

### Module descriptor

**Location:** `src/main/java/module-info.java`

The module now exports the new `model`, `engine`, `history`, `ai`, and `level` packages.

**Why necessary:** Other project code and tests require access to the new packages in the modular JavaFX project.

---

## Unexpected Issues

### 1. Java compiler version mismatch

The original `pom.xml` compiled for Java 25, but the configured development JDK was Java 21. Maven therefore failed with a fatal compiler error.

**Resolution:** Replaced the original source/target 25 settings with `<release>21</release>` and verified compilation using Temurin JDK 21.

### 2. Missing `JAVA_HOME`

The `java` command worked, but the Maven Wrapper initially failed because `JAVA_HOME` was not defined.

**Resolution:** Located the active JDK, set `JAVA_HOME` for the session, added its `bin` directory to `Path`, and saved the variable for the Windows user.

### 3. Initial downloaded directory was not a Git working copy

The first project folder was extracted from a ZIP and did not contain repository metadata, so Git branch and commit commands failed.

**Resolution:** Forked the course repository, cloned the fork into a new directory, created `develop` and task-specific branches, and merged work through pull requests.

### 4. Game rules were coupled to JavaFX text nodes

The original game used visible `Text` content as the source of truth for tile values. This made isolated rule tests impractical and allowed UI concerns to affect game logic.

**Resolution:** Introduced `Board` and `GameEngine` as JavaFX-independent components, then changed `GameScene` to render the model instead of owning the rules.

### 5. Obstacle levels created additional edge cases

Obstacles affected movement, game-over detection, empty-cell counting, random spawning, Undo snapshots, Hint evaluation, and rendering. A change in only one component would have produced inconsistent behaviour.

**Resolution:** Obstacles were represented in `Board` and respected by every dependent service. The engine processes each row or column as separate playable segments divided by obstacles, and dedicated tests cover isolated tiles and obstacle boundaries.

### 6. Testing a modular JavaFX project

JUnit execution in a modular JavaFX project can be sensitive to module-path configuration.

**Resolution:** Added Maven Surefire 3.5.5 and set `<useModulePath>false</useModulePath>` for the test phase. The final suite runs 45 tests successfully.

### 7. Maintaining compatibility with legacy UI classes

The repository still contains original classes such as `Cell`, `TextMaker`, and `EndGame`. Removing all legacy code during the same refactoring would have increased risk.

**Resolution:** `Cell` and `TextMaker` were retained as rendering helpers, while game state and rules were moved out of them. `EndGame` was retained for final-score display, and its incomplete quit action is documented above.

---

## Project Structure

```text
src/main/java/com/example/demo/
├── Main.java                  # JavaFX entry point and level menu
├── GameScene.java             # UI controller and model rendering
├── Cell.java                  # Legacy visual cell helper
├── TextMaker.java             # Legacy text rendering helper
├── EndGame.java               # Legacy game-over view
├── Account.java               # Legacy, not integrated
├── Controller.java            # Legacy FXML controller
├── ai/
│   └── HintService.java
├── engine/
│   ├── GameEngine.java
│   └── TileSpawner.java
├── history/
│   └── GameHistory.java
├── level/
│   ├── LevelCatalog.java
│   └── LevelConfig.java
└── model/
    ├── Board.java
    ├── Direction.java
    ├── GameState.java
    ├── MoveResult.java
    └── Position.java

src/test/java/com/example/demo/
├── ai/HintServiceTest.java
├── engine/GameEngineTest.java
├── engine/TileSpawnerTest.java
├── history/GameHistoryTest.java
├── level/LevelConfigTest.java
└── model/BoardObstacleTest.java
```

---

## Final Verification Summary

- JavaFX application launches successfully.
- Classic, Large Board, and Obstacle Challenge are accessible from the menu.
- Core movement and score rules are separated from JavaFX.
- Undo, Hint, Restart, and Level Menu are integrated.
- Obstacle behaviour is applied consistently.
- **45 automated tests pass with no failures or errors.**
- One legacy limitation remains documented: the game-over `QUIT` confirmation clears the scene instead of closing or navigating.
