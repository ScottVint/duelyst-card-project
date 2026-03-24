//package structures;
//
//import akka.actor.ActorRef;
//import commands.BasicCommands;
//import structures.basic.Board;
//import structures.basic.Tile;
//import structures.basic.players.AIPlayer;
//import structures.basic.players.HumanPlayer;
//import structures.basic.players.Player;
//import structures.basic.unittypes.BetterUnit;
//import structures.basic.unittypes.Unit;
//import structures.logic.AI;
//import structures.logic.BoardLogic;
//import structures.logic.CombatLogic;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * This class can be used to hold information about the on-going game.
// * Its created with the GameActor.
// */
//public class GameState {
//
//	public boolean gameInitalised = false;
//	public boolean something = false;
//
//	public HumanPlayer player1 =  new HumanPlayer();
//	public AIPlayer player2 = new AIPlayer();
//	public Board board = new Board();
//	public Unit selectedUnit = null;
//	public boolean player1Turn = true;
//
//	public int turnCount = 1;
//
//	/** 1-indexed hand position of the selected card, or null if none selected */
//	public Integer selectedHandPosition = null;
//
//	/** Next unique unit id for summoned units. */
//	private int nextUnitId = 0;
//
//	/** List of currently highlighted tiles. Used for validation.*/
//	public Set<Tile> highlightedTiles = new HashSet<Tile>();
//
//	/** Movement state */
//	public Unit movingUnit = null;
//	public Tile moveTargetTile = null;
//	public boolean unitMoving = false;
//
//	public Player getPlayer1() { return player1; }
//
//	public Player getPlayer2() { return player2; }
//
//	public Board getBoard() { return board; }
//
//	public Unit getSelectedUnit() { return selectedUnit; }
//
//	public int getNextUnitId() { return nextUnitId++; }
//
//
//	public void placeAvatar(ActorRef out, BetterUnit avatar, int x, int y) {
//		Tile tile = this.board.getTile(x, y);
//		tile.setUnit(avatar);
//		avatar.setPositionByTile(tile);
//
//		BasicCommands.drawUnit(out, avatar, tile);
//		for (int i = 0; i < 30; i++) {
//			BoardLogic.blink();
//		}
//		BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
//		for (int i = 0; i < 30; i++) {
//			BoardLogic.blink();
//		}
//		BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());
//	}
//
//	/**
//	 * Combat damage based on attacker attack stat.
//	 */
//	public void dealDamage(ActorRef out, Unit attacker, Unit target) {
//		if (attacker == null || target == null) return;
//
//		int damage = attacker.getAttack();
//
//		if (damage <= 0) {
//			BasicCommands.addPlayer1Notification(out, "Attacker has 0 attack.", 2);
//			return;
//		}
//
//		dealDirectDamage(out, target, damage);
//	}
//
//	/**
//	 * Direct spell / combat damage.
//	 */
//	public void dealDirectDamage(ActorRef out, Unit target, int damage) {
//		if (target == null || damage <= 0) return;
//
//		int newHealth = target.getHealth() - damage;
//		target.setHealth(out, newHealth);
//
//		BasicCommands.setUnitHealth(out, target, target.getHealth());
//
//		if (target == player1.getAvatar()) {
//			player1.setHealth(target.getHealth());
//			BasicCommands.setPlayer1Health(out, player1);
//		} else if (target == player2.getAvatar()) {
//			player2.setHealth(target.getHealth());
//			BasicCommands.setPlayer2Health(out, player2);
//		}
//
//		if (target.isDead()) {
//			target.die(out);
//		}
//	}
//
//
//	public void endTurn(ActorRef out, Player playerEndingTurn, Player playerStartingTurn) {
//		if (!player1Turn) {
//			turnCount++;
//		}
//		player1Turn = !player1Turn;
//		// Refresh mana
//		int startingMana = Math.min(turnCount + 1, Player.getMaxMana());
//		playerStartingTurn.setMana(out, startingMana);
//		playerEndingTurn.setMana(out, 0);
//
//		// Draw card: the ending player draws 1 card at the end of their turn (for next turn)
//		playerEndingTurn.drawCardIntoHand();
//
//		// Reset flags
//		playerEndingTurn.getAvatar().hasAttacked = false; playerEndingTurn.getAvatar().hasMoved = false;
//		for (Unit unit : playerEndingTurn.getUnitList().values()) {
//			unit.hasAttacked = false; unit.hasMoved = false;
//		}
//
//		// Run AI on AI turn
//		if (playerEndingTurn instanceof HumanPlayer) {
//			AI.AILogic.runAI(out, this, player1, player2);
//		}
//	}
//}
package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.Unit;
import structures.logic.AI;
import structures.logic.BoardLogic;

import java.util.HashSet;
import java.util.Set;

public class GameState {

	public boolean gameInitalised = false;
	public HumanPlayer player1 = new HumanPlayer();
	public AIPlayer player2 = new AIPlayer();
	public Board board = new Board();
	public Unit selectedUnit = null;

	// 回合控制核心变量
	public boolean player1Turn = true;
	public int turnCount = 1;
	public Integer selectedHandPosition = null;
	private int nextUnitId = 0;
	public Set<Tile> highlightedTiles = new HashSet<Tile>();

	// 移动状态
	public Unit movingUnit = null;
	public Tile moveTargetTile = null;
	public boolean unitMoving = false;

	public Player getPlayer1() { return player1; }
	public Player getPlayer2() { return player2; }
	public Board getBoard() { return board; }
	public Unit getSelectedUnit() { return selectedUnit; }
	public int getNextUnitId() { return nextUnitId++; }

	public void placeAvatar(ActorRef out, BetterUnit avatar, int x, int y) {
		Tile tile = this.board.getTile(x, y);
		tile.setUnit(avatar);
		avatar.setPositionByTile(tile);

		BasicCommands.drawUnit(out, avatar, tile);
		for (int i = 0; i < 30; i++) { BoardLogic.blink(); }
		BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
		for (int i = 0; i < 30; i++) { BoardLogic.blink(); }
		BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());
		// 👇👇👇 前端可视化测试 Demo 代码 (测试完可以删掉这段) 👇👇👇
		if (avatar == player1.getAvatar()) {
			// 1. 在你主将旁边 (x=1, y=3) 直接召唤一个你的【暗影守望者 Shadow Watcher】
			// 1. 召唤保镖：【血月祭司 Bloodmoon Priestess】
			Tile watcherTile = this.board.getTile(1, 3);
			Unit watcher = utils.BasicObjectBuilders.loadUnit("conf/gameconfs/units/bloodmoon_priestess.json", this.getNextUnitId(), structures.basic.unittypes.BloodmoonPriestess.class);
			watcher.setOwner(player1);
			watcher.setPositionByTile(watcherTile);
			watcherTile.setUnit(watcher);
			watcher.setMaxHealth(3);
			watcher.setHealth(out, 3);
			watcher.setAttack(out, 3);
			player1.getUnitList().put(watcher.getId(), watcher);
			BasicCommands.drawUnit(out, watcher, watcherTile);
			BasicCommands.setUnitHealth(out, watcher, 3);
			BasicCommands.setUnitAttack(out, watcher, 3);

			// 2. 在你主将正前方 (x=2, y=2) 召唤一个敌方的【炮灰小幽灵】作为沙包
			Tile dummyTile = this.board.getTile(2, 2);
			Unit dummy = utils.BasicObjectBuilders.loadUnit("conf/gameconfs/units/wraithling.json", this.getNextUnitId(), structures.basic.unittypes.Wraithling.class);
			dummy.setOwner(player2);
			dummy.setPositionByTile(dummyTile);
			dummyTile.setUnit(dummy);
			dummy.setMaxHealth(1);
			dummy.setHealth(out, 1);
			dummy.setAttack(out, 1);
			player2.getUnitList().put(dummy.getId(), dummy);
			BasicCommands.drawUnit(out, dummy, dummyTile);
			BasicCommands.setUnitHealth(out, dummy, 1);
			BasicCommands.setUnitAttack(out, dummy, 1);
		}
		// 👆👆👆 Demo 代码结束 👆👆👆
	}

	/**
	 * 统一的伤害处理中心
	 */

	public void dealDamage(ActorRef out, Unit attacker, Unit target) {
		if (attacker == null || target == null) return;
		int damage = attacker.getAttack();
		if (damage <= 0) return;
		dealDirectDamage(out, target, damage);
	}

	public void dealDirectDamage(ActorRef out, Unit target, int damage) {
		if (target == null || damage <= 0) return;

		int newHealth = target.getHealth() - damage;
		target.setHealth(out, newHealth);

		if (target == player1.getAvatar()) {
			player1.setHealth(target.getHealth());
			BasicCommands.setPlayer1Health(out, player1);
		} else if (target == player2.getAvatar()) {
			player2.setHealth(target.getHealth());
			BasicCommands.setPlayer2Health(out, player2);
		}

		// 修改点：如果死亡，传入 GameState 以便触发 Deathwatch
		if (target.isDead()) {
			target.die(out, this);
		}
	}

	/**
	 * 死亡广播中心 (大喇叭)
	 * 当任何单位死亡时，GameState 扫描全场，触发所有存活单位的 deathwatch 技能
	 */
	public void triggerDeathwatch(ActorRef out, Unit deadUnit) {
		// 扫描玩家 1 的场上单位
		if (!player1.getAvatar().isDead() && player1.getAvatar() != deadUnit) {
			player1.getAvatar().deathwatch(out, this);
		}
		for (Unit u : player1.getUnitList().values()) {
			if (!u.isDead() && u != deadUnit) u.deathwatch(out, this);
		}

		// 扫描玩家 2 的场上单位
		if (!player2.getAvatar().isDead() && player2.getAvatar() != deadUnit) {
			player2.getAvatar().deathwatch(out, this);
		}
		for (Unit u : player2.getUnitList().values()) {
			if (!u.isDead() && u != deadUnit) u.deathwatch(out, this);
		}
	}

	/**
	 * 优化后的回合控制
	 * 明确区分了“结束回合的玩家”和“即将开始回合的玩家”
	 */
	public void endTurn(ActorRef out, Player playerEndingTurn, Player playerStartingTurn) {
		// 1. 结束回合的玩家抽一张卡（为下一回合做准备）
		playerEndingTurn.drawCardIntoHand();

		// 2. 切换回合标志
		if (!player1Turn) {
			turnCount++; // 只有当 AI 回合结束，转回玩家1时，全局回合数才+1
		}
		player1Turn = !player1Turn;

		// 3. 处理法力值 (Mana)
		int startingMana = Math.min(turnCount + 1, Player.getMaxMana());
		playerStartingTurn.setMana(out, startingMana); // 新回合玩家回满蓝
		playerEndingTurn.setMana(out, 0);              // 结束回合玩家蓝清零

		// 4. 重置单位行动状态
		// 只有即将开始回合的玩家，他们的单位才能恢复行动力
		playerStartingTurn.getAvatar().hasAttacked = false;
		playerStartingTurn.getAvatar().hasMoved = false;
		for (Unit unit : playerStartingTurn.getUnitList().values()) {
			unit.hasAttacked = false;
			unit.hasMoved = false;
		}

		// 5. 将控制权交给 AI
		if (!player1Turn && playerEndingTurn instanceof HumanPlayer) {
			// 如果你想测试 AI，以后把这里的注释解开
			 AI.AILogic.runAI(out, this, player1, player2);
		}
	}
}