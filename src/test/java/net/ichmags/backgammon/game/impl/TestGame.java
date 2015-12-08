package net.ichmags.backgammon.game.impl;

import java.util.Arrays;

import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.DiceGenerator;
import net.ichmags.backgammon.setup.impl.DicesChoice;
import net.ichmags.backgammon.setup.impl.Player;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestGame {

	private IPlayer player1;
	private IPlayer player2;
	
	@Before
	public void setUp() {
		player1 = new Player().initialize("Tester 1", IPlayer.ID.ONE, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.WHITE);
		player2 = new Player().initialize("Tester 2", IPlayer.ID.TWO, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.BLACK);
	}
	
	@Test
	public void testOnePointGame() {
		Fevga testGame = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(19, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25);
			}
		};
		testGame.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 1});
		
		boolean check = testGame.dices.roll().isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		check = testGame.checkIfAnyMoveIsPossible(player1, testGame.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testGame.findPlayableDices(player1, testGame.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testGame.moveChecker(player1, testGame.board, 24, playableDices.getUnused(1)).isSuccess();
		Assert.assertTrue("Move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All posible dices are played", check);
		
		int gameValue = testGame.getGameValue(player1);
		Assert.assertEquals("Winning game points mismatch", 1, gameValue);
	}
	
	@Test
	public void testTwoPointGame() {
		Fevga testGame = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(19, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24);
			}
		};
		testGame.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 1});
		
		boolean check = testGame.dices.roll().isDoubleDices();
		Assert.assertTrue("Double dices", check);
		
		check = testGame.checkIfAnyMoveIsPossible(player1, testGame.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testGame.findPlayableDices(player1, testGame.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		check = playableDices.allUsed();
		Assert.assertFalse("Not all dices are played", check);
		
		check = testGame.moveChecker(player1, testGame.board, 24, playableDices.getUnused(1)).isSuccess();
		Assert.assertTrue("Move is possible", check);
		
		check = playableDices.allUsed();
		Assert.assertTrue("All posible dices are played", check);
		
		int gameValue = testGame.getGameValue(player1);
		Assert.assertEquals("Winning game points mismatch", 2, gameValue);
	}
	
	@Test
	public void testFindDicesChoiceForLastMove() {
		Fevga testGame = new Fevga() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(19, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24);
			}
		};
		testGame.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 2});
		
		boolean check = testGame.dices.roll().isDoubleDices();
		Assert.assertFalse("No double dices", check);
		
		check = testGame.checkIfAnyMoveIsPossible(player1, testGame.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testGame.findPlayableDices(player1, testGame.dices);
		Assert.assertFalse("Not single dices option", dicesList.isSingleOption());
		
		IDices playableDices1 = dicesList.getOption1();
		Assert.assertTrue("Has used dices", playableDices1.usedCount() > 0);
		Assert.assertNotNull("Dice can be used", playableDices1.getUnused(1));
		Assert.assertNull("Dice can not be used", playableDices1.getUnused(2));
		
		IDices playableDices2 = dicesList.getOption2();
		Assert.assertTrue("Has used dices", playableDices2.usedCount() > 0);
		Assert.assertNotNull("Dice can be used", playableDices2.getUnused(2));
		Assert.assertNull("Dice can not be used", playableDices2.getUnused(1));
	}
}
