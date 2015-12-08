/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import java.util.Arrays;

import net.ichmags.backgammon.game.impl.Fevga;
import net.ichmags.backgammon.game.impl.Game;
import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.DiceGenerator;
import net.ichmags.backgammon.setup.impl.DicesChoice;
import net.ichmags.backgammon.setup.impl.Player;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anastasios Patrikis
 */
public class TestFevga {
	
	private IPlayer player1;
	private IPlayer player2;
	
	@Before
	public void setUp() {
		player1 = new Player().initialize("Tester 1", IPlayer.ID.ONE, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.WHITE);
		player2 = new Player().initialize("Tester 2", IPlayer.ID.TWO, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.BLACK);
	}
	
	/**
	 * Test a 6-6 as first dice: only one move possible.
	 */
	@Test
	public void testMoveStart66() {
		Game testFevga = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1, 1, 1);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 1, 1);
			}
		};
		testFevga.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{6, 6});
		
		boolean check = false;
		// First player starts with 6-6: only 1 move possible
		check = testFevga.dices.roll().isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		check = testFevga.checkIfAnyMoveIsPossible(player1, testFevga.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testFevga.findPlayableDices(player1, testFevga.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 1, playableDices.getUnused(6)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All posible dices are played", check);
	}
	
	/**
	 * Test a 5-5 as first dice: all moves possible.
	 */
	@Test
	public void testMoveStart55() {
		Game testFevga = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1, 1, 1);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 1, 1);
			}
		};
		testFevga.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{5, 5});
		
		boolean check = false;
		// First player starts with 5-5: all moves possible
		check = testFevga.dices.roll().isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		check = testFevga.checkIfAnyMoveIsPossible(player1, testFevga.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testFevga.findPlayableDices(player1, testFevga.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 1, playableDices.getUnused(5)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 6, playableDices.getUnused(5)).isSuccess();
		Assert.assertTrue("Second move is possible", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 1, playableDices.getUnused(5)).isSuccess();
		Assert.assertFalse("Cannot move second token: first token not moved >= 12", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 11, playableDices.getUnused(5)).isSuccess();
		Assert.assertTrue("Third move is possible", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 1, playableDices.getUnused(5)).isSuccess();
		Assert.assertTrue("Forth move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All dices played", check);
	}
	
	/**
	 * Test final take out: cannot finish due to opponent blocking a move
	 */
	@Test
	public void testMoveEndTakout() {
		Game testFevga = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(18, 19);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(12);
			}
		};
		testFevga.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{5, 5});
		
		boolean check = false;
		// cannot finish: position 1 is blocked
		check = testFevga.dices.roll().isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		check = testFevga.checkIfAnyMoveIsPossible(player1, testFevga.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testFevga.findPlayableDices(player1, testFevga.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 19, playableDices.getUnused(5)).isSuccess();
		Assert.assertFalse("Position 24 is blocked by opponent", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 18, playableDices.getUnused(5)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All posible dices are played", check);
	}
	
	@Test
	public void testMandatoryMove() {
		Game testFevga = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 6, 14);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7);
			}
		};
		testFevga.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{6, 3});
		testFevga.dices.roll();
		
		boolean check = false;
		check = testFevga.checkIfAnyMoveIsPossible(player2, testFevga.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testFevga.findPlayableDices(player2, testFevga.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testFevga.moveChecker(player2, testFevga.board, 7, playableDices.getUnused(6)).isSuccess();
		Assert.assertFalse("Position is blocked by opponent", check);
		
		check = testFevga.moveChecker(player2, testFevga.board, 7, playableDices.getUnused(3)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = testFevga.moveChecker(player2, testFevga.board, 10, playableDices.getUnused(6)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All posible dices are played", check);
	}
	
	@Test
	public void testBlockingFirst6() {
		Game testFevga = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1, 1, 2, 3, 3, 4, 6, 14);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 1);
			}
		};
		testFevga.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{2, 3});
		testFevga.dices.roll();
		
		boolean check = testFevga.checkIfAnyMoveIsPossible(player1, testFevga.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testFevga.findPlayableDices(player1, testFevga.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		
		IDices playableDices = dicesList.getOption1();
		Assert.assertEquals("No dices blocked (all dices can be played)", 0, playableDices.usedCount());
		
		check = testFevga.moveChecker(player1, testFevga.board, 3, playableDices.getUnused(2)).isSuccess();
		Assert.assertFalse("Blocking first 6 is not allowed", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 2, playableDices.getUnused(3)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		
		check = testFevga.moveChecker(player1, testFevga.board, 3, playableDices.getUnused(2)).isSuccess();
		Assert.assertTrue("Second move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All dices are played", check);
	}
}
