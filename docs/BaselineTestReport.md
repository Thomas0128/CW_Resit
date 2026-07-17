\# Baseline Test Report



\## Environment



\- Operating System: Windows 11

\- IDE: IntelliJ IDEA

\- Java: Temurin JDK 21

\- JavaFX: 21.0.6

\- Build Tool: Maven Wrapper



\## Build Result



Command:



```powershell

.\\mvnw.cmd clean javafx:run

Result: The original application compiled and launched successfully.



Confirmed Defects

BUG-01: Incorrect score calculation

After every keyboard event, the application adds the value of every

tile on the board to the score.



Expected behaviour: Only the values created by tile merges should be

added to the score.



Location: GameScene.sumCellNumbersToScore().



BUG-02: Tiles spawn after invalid input

A new tile may be generated even when the board did not change.

Non-direction keys may also trigger score calculation and tile spawning.



Expected behaviour: A new tile should only appear after a valid move

that changes the board.



Location: Keyboard event handler in GameScene.game().



BUG-03: Incorrect multiple-merge handling

The application marks the wrong cell as already merged.



For example:



\[2, 2, 2, 2] LEFT



may produce:



\[4, 2, 2, 0]



instead of:



\[4, 4, 0, 0].



Location: GameScene.moveHorizontally() and

GameScene.moveVertically().



BUG-04: Biased random tile placement

The empty-cell selection algorithm does not allow every empty position

to be selected fairly. Some positions may never be selected.



Expected behaviour: Every empty cell should have an equal chance of

receiving the new tile.



Location: GameScene.randomFillNumber().



BUG-05: Incorrect game-over detection

The final row and final column are not checked correctly for adjacent

matching tiles.



Expected behaviour: The game should end only when the board is full

and no horizontal or vertical merge is possible.



Location: GameScene.haveSameNumberNearly().



BUG-06: Incomplete winning-state handling

The search may return after finding an empty cell before checking for a

2048 tile. The returned winning state is also not handled by the user

interface.



Expected behaviour: Reaching the target tile should display a victory

message and allow the player to continue or finish.



Location: GameScene.haveEmptyCell() and GameScene.game().



Maintainability Problems

Game logic is tightly coupled to JavaFX components.



GameScene has too many responsibilities.



Tile values are stored inside JavaFX Text objects.



Several scenes, classes, imports and FXML resources are unused.



No automated tests currently exist.



Package name com.example.demo is not meaningful.



Baseline Conclusion

The original application runs, but its game logic, scoring, tile

generation, merge handling and end-state detection require maintenance.



The core logic should be separated from JavaFX before substantive

extensions are implemented.

