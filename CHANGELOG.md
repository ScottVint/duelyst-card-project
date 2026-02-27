# Changelog

All notable changes to this project will be documented in this file.

---

## [Unreleased] — 2026-02-27

### Added

#### `app/structures/Board.java` *(new file)*
- Introduced `Board` class representing the 9×5 game grid.
- Holds a 2D array of `Tile` objects, initialised via `BasicObjectBuilders.loadTile(x, y)`.
- Exposes `getTile(int x, int y)` for safe tile access and `getX()` / `getY()` for board dimensions.
- Author: Minghao

#### `app/events/TileClicked.java` — `clearAllHighlights()`
- Added helper method that resets every tile on the board to mode 0 (no highlight).
- Called before applying any new highlights to prevent stale white/red tiles remaining from a previous selection.
- Author: Minghao

---

### Changed

#### `app/structures/GameState.java`
- Added `Board board` field and initialised it in the constructor.
- Added `Player player1` and `Player player2` fields; both are constructed and given their avatars and decks on startup via `BasicObjectBuilders` and `OrderedCardLoader`.
- Added `Unit selectedUnit` field with `getSelectedUnit()` / `setSelectedUnit()` accessors to track the currently selected unit across events.
- Author: Minghao

#### `app/structures/basic/Player.java`
- Extended base class with `Unit avatar`, `List<Card> deck`, and `List<Card> hand` fields.
- Added `getAvatar()` / `setAvatar()`, `getDeck()` / `setDeck()`, `getHand()` accessors.
- Added `drawCard()`: moves the top card from the deck into the hand; no-ops if the deck is empty.
- Author: Minghao

#### `app/structures/basic/Unit.java`
- Extended base class with `int health`, `int maxHealth`, `int attack`, and `Player owner` fields.
- Added full getters and setters for all new fields.
- `owner` is annotated `@JsonIgnore` to avoid circular serialisation.
- Author: Minghao

#### `app/structures/basic/Tile.java`
- Extended base class with `Unit unit` field (annotated `@JsonIgnore`) to track occupancy.
- Added `getUnit()` / `setUnit()` to place or remove a unit on the tile.
- Author: Minghao

#### `app/events/Initalize.java`
- Added `avatar1.setOwner(player1)` and `avatar2.setOwner(player2)` immediately after avatar retrieval.
  - **Bug fix**: without ownership being set, `TileClicked` could never match a clicked unit to the human player, making unit selection non-functional (Story Card #3).
- Replaced commented-out `BasicCommands.drawHand()` (which does not exist) with a loop over `player1.getHand()` calling `BasicCommands.drawCard(out, card, position, 0)` for each card.
  - Ensures Player 1's 3 starting cards are rendered in the hand on game load (Story Card #18).
- Added imports: `structures.basic.Card`, `java.util.List`.
- Updated Javadoc to reference Story Card #18.
- Author: Minghao

#### `app/events/TileClicked.java`
- **Bug fix — movement range calculation**: replaced incorrect Chebyshev distance formula (`Math.max(|dx|, |dy|) <= 2`) with the correct two-part logic:
  - *Cardinal*: up to 2 steps along each of the four axis-aligned directions.
  - *Diagonal*: exactly 1 step in each of the four diagonal directions.
  - The old formula incorrectly highlighted tiles such as `(±2, ±1)`, `(±1, ±2)`, and `(±2, ±2)` which are not reachable under the game rules.
- **Bug fix — path blocking**: cardinal movement now stops (via `break`) as soon as an occupied tile is encountered; units can no longer highlight tiles behind a blocking unit.
- **Bug fix — stale highlights**: `clearAllHighlights()` is now called at the start of each new selection, preventing highlight build-up across multiple clicks.
- Updated Javadoc on `highlightMovementRange()` to document movement rules and path-blocking behaviour.
- Author: Minghao

#### `README.md`
- Replaced placeholder title with full project documentation.
- Added: team member table, requirements, run instructions, project structure, game rules summary, implemented story cards table, deck descriptions, architecture notes, and license section.
- Author: Minghao

---

### Story Cards Addressed

| # | Title | Changes |
|---|-------|---------|
| #3 | Unit Selection & Highlighting | Fixed owner assignment, movement range formula, path blocking, and highlight clearing |
| #18 | Game Initialization & Setup | Fixed missing avatar ownership; replaced broken hand-render call with correct `drawCard` loop |
