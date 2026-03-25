# ITSD Card Game 2025-26

A simplified single-player browser-based tactical card game inspired by **Duelyst**, built as part of the IT+ Masters Team Project (COMPSCI5074M) at the University of Glasgow.

The human player (Player 1) competes against an AI opponent (Player 2) on a 9×5 grid board, using a deck of cards to summon units, cast spells, and defeat the enemy avatar.

---

## Team Members

| Name | GitHub Username | Role |
|------|-----------------|------|
| Minghao Yue | @tobbyue | SC#2, #3, #6, #11–#13, #15–#16, #18–#19, #21–#22, #35; Java 21 compat; AI; game animations |
| Scott Vint | @ScottVint | SC#4, #5, #7–#9, #23, #25–#28; core refactor; unit flags |
| Zechao Wu | @ZechaoWu | SC#29, #30, #34; counterattack, move-then-attack, turn timer |
| Pengcheng Wang | @youshanwai | SC#24; Deathwatch ability |
| Zhibin Yao | @YAOZHIBIN622 | SC#20, #31, #32; win condition, card feedback, randomise start |

---

## Requirements

| Tool | Version |
|------|---------|
| Java JDK | 11+ (Java 21 supported via `.sbtopts`) |
| sbt | 1.9.9 |
| Scala | 2.13.12 |
| Play Framework | 2.8.22 (bundled via sbt) |
| A modern web browser | Chrome / Firefox recommended |

---

## How to Run

> **Java 21 users:** The project includes a `.sbtopts` file that automatically passes the required `--add-opens` flags to the JVM. No additional configuration is needed.

1. **Clone the repository**
   ```bash
   git clone https://github.com/uog-cose/MSc-IT-plus-2026-LB02-AL.git
   cd MSc-IT-plus-2026-LB02-AL
   ```

2. **Place frontend assets** — copy the `app/assets/` sprite and JS files into the repo (gitignored; obtain from the project template assets package).

3. **Start the server**
   ```bash
   ./sbt run
   ```
   The first run will download all dependencies automatically.

4. **Open the game in your browser**
   ```
   http://localhost:9000/game
   ```

5. **Run tests**
   ```bash
   ./sbt test
   ```

---

## Project Structure

```
app/
├── actors/
│   └── GameActor.java              # Akka actor: receives events, dispatches to handlers
├── commands/
│   └── BasicCommands.java          # Front-end API: WebSocket render commands
├── events/                         # One handler class per event type
│   ├── Initalize.java              # Game setup: avatars, decks, initial hand
│   ├── TileClicked.java            # Tile click: unit selection, movement, attack, summon
│   ├── CardClicked.java            # Card selection & summon/spell highlighting
│   ├── EndTurnClicked.java         # End turn + mana/card reset
│   ├── UnitStopped.java            # Post-move position update
│   ├── Heartbeat.java              # Game-loop heartbeat (~1 s interval)
│   └── OtherClicked.java           # Background click: clear selection
├── structures/
│   ├── GameState.java              # Central game state
│   ├── basic/
│   │   ├── Board.java              # 9×5 grid of Tile objects
│   │   ├── Card.java               # Card: id, name, mana cost, creature/spell flag
│   │   ├── Tile.java               # Board tile: position + occupying unit
│   │   ├── EffectAnimation.java
│   │   ├── players/
│   │   │   ├── Player.java         # Abstract: health, mana, deck, hand, avatar
│   │   │   ├── HumanPlayer.java    # Player 1 HP/mana display commands
│   │   │   └── AIPlayer.java       # Player 2 HP/mana display commands
│   │   ├── spells/
│   │   │   ├── Spell.java          # Abstract spell base class
│   │   │   ├── Truestrike.java     # 2 damage to any enemy
│   │   │   ├── Beamshock.java      # Stun non-avatar enemy
│   │   │   ├── SundropElixir.java  # Heal friendly unit +4 HP
│   │   │   ├── DarkTerminus.java   # Destroy enemy, summon Wraithling
│   │   │   ├── HornOfTheForsaken.java  # Equip Horn of the Forsaken on avatar
│   │   │   └── WraithlingSwarm.java    # Summon 3 Wraithlings
│   │   └── unittypes/
│   │       ├── Unit.java           # Base unit: id, HP, attack, position, keywords
│   │       ├── BetterUnit.java     # Avatar unit: links HP to player, Horn charges
│   │       └── Wraithling.java     # 1/1 token unit
│   └── logic/
│       ├── BoardLogic.java         # Pathfinding, highlighting, movement
│       ├── CombatLogic.java        # Attack resolution, death handling
│       └── AI.java                 # AI turn: spells → move → attack → end turn
└── utils/
    ├── BasicObjectBuilders.java    # Factory: load Unit/Card/Tile/Effect from JSON
    ├── OrderedCardLoader.java      # Load player decks from JSON configs
    └── StaticConfFiles.java        # Constants for config file paths
```

---

## Game Rules Summary

- **Board**: 9 columns × 5 rows. Both players start with an avatar on the board.
- **Avatars**: Player 1 at [2,3]; Player 2 at [8,3]. Each avatar starts with 20 HP.
- **Turn structure**: Draw a card → spend mana to move / attack / play cards → end turn.
- **Mana**: Starts at 2 on Player 1's first turn; increases by 1 each turn (max 9). All unspent mana is lost at end of turn.
- **Movement**: Up to 2 tiles in cardinal directions, or 1 tile diagonally. Cannot pass through other units.
- **Attack**: Adjacent tiles only (8-directional). Surviving defenders counter-attack once.
- **Summoning**: Unit cards must be placed on a tile adjacent to a friendly unit.
- **Win condition**: Reduce the enemy avatar to 0 HP.

---

## Implemented Story Cards

### ✅ Merged to master

| # | Story Card | Author |
|---|------------|--------|
| #2 | HP Display | Minghao |
| #3 | Unit Selection & Movement Highlighting | Minghao |
| #4 | Unit Movement | Scott |
| #5 | Unit Attack | Scott |
| #6 | Mana Display & Replenishment | Minghao |
| #7 | Unit Summoning | Scott |
| #8 | Player End Turn | Scott |
| #9 / #9a–e | General Spell Usage (Direct Damage, Heal, Destroy, Stun, Summon) | Scott |
| #11 | AI Attacking | Minghao |
| #12 | Cancel Selection | Minghao |
| #13 | AI End Turn | Minghao |
| #15 | AI Movement | Minghao |
| #16 | Link Avatar to Player HP | Minghao |
| #17 | AI Spell Usage | Minghao |
| #18 | Game Initialization & Setup | Minghao |
| #19 | Mana Increase | Minghao |
| #21 | Card Draw & Hand Limit | Minghao |
| #22 | Summon Tile Highlighting | Minghao |
| #23 | Opening Gambit | Scott |
| #24 | Deathwatch | Pengcheng |
| #25 | Provoke | Scott |
| #26 | Zeal | Scott |
| #27 | Rush | Scott |
| #28 | Flying | Scott |
| #35 | Unit Death (custom SC) | Minghao |

> **SC#14** has been repurposed to cover missing game animations. See [`Project Assignment/SC14-Game-Animations.md`](Project%20Assignment/SC14-Game-Animations.md).

### 🚧 In open PRs (pending review / merge)

| # | Story Card | Author | PR |
|---|------------|--------|----|
| #14 | Game Animations (walk, idle, summon effect, spell effects, channel) | Minghao | #25 |
| #10 | Turn Ownership Indicator | Zechao | #24 |
| #20 | Win Condition | Zhibin | #20 |
| #29 | Counterattacking | Zechao | #24 |
| #30 | Attacking and Moving Simultaneously | Zechao | #24 |
| #31 | Card Unplayable Feedback | Zhibin | #20 |
| #32 | Randomise Start | Zhibin | #20 |
| #34 | Optional Turn Time Limit | Zechao | #24 |

### ❌ Not yet started

| # | Story Card | Priority |
|---|------------|----------|
| #1 | Seeing a Card's Details | 2 |
| #33 | Unit Usability (exhausted unit indicator) | 9 |

---

## Test Coverage

| Test File | Story Card(s) | Tests |
|-----------|---------------|-------|
| `BetterUnitTest` | #2 (HP Display), #16 (Avatar–Player HP link) | 2 |
| `InitalizationTest` | #18 (Game Initialization) | 1 |
| `TileClickedTest` | #3 (Unit Selection & Movement) | 8 |
| `EndTurnClickedTest` | #6 (Mana Display), #8 (End Turn) | 5 |
| `CardClickedTest` | #22 (Summon Tile Highlighting) | 5 |
| `OtherClickedTest` | #12 (Cancel Selection) | 5 |
| `ManaIncreaseTest` | #19 (Mana Increase) | 5 |
| `PlayerTest` | #21 (Card Draw & Hand Limit) | 4 |
| **Total** | | **35 — all passing** |

---

## Decks

### Player 1 — Abyssian Swarm
Theme: **Deathwatch** synergies with Wraithling (1/1) token generation.

Key cards: Bad Omen, Gloom Chaser, Shadow Watcher, Bloodmoon Priestess, Shadowdancer,
Wraithling Swarm, Dark Terminus, Horn of the Forsaken, Sundrop Elixir.

### Player 2 (AI) — Lyonar Generalist
Theme: High-statted creatures with **Provoke**, **Rush**, **Flying**, and **Zeal**.

Key cards: Swamp Entangler, Silverguard Knight, Saberspine Tiger, Young Flamewing,
Ironcliffe Guardian, Beam Shock, True Strike.

---

## Architecture Notes

- Communication between the Java back-end and the browser front-end is **asynchronous** over WebSocket. Never block the event thread waiting for a front-end response.
- All game state is held in `GameState`, which is passed into every event handler.
- Use `BasicCommands` to send render commands to the front-end; use `BasicObjectBuilders` to construct game objects from their JSON config files.
- The card and unit configs in `conf/gameconfs/` are read-only template files and must not be modified.
- Frontend assets (`app/assets/js/`, `app/assets/game/`) are gitignored and must be placed manually.
- Java 21 compatibility is provided via `.sbtopts` (`--add-opens` flags) and `build.sbt` (`fork := true`, matching `javaOptions`).

---

## License

Duelyst assets used in this project entered the public domain in January 2023.
Template project provided by Dr. Richard McCreadie, University of Glasgow.
