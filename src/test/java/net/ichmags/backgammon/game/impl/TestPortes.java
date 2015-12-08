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

public class TestPortes {

	private IPlayer player1;
	private IPlayer player2;
	
	@Before
	public void setUp() {
		player1 = new Player().initialize("Tester 1", IPlayer.ID.ONE, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.WHITE);
		player2 = new Player().initialize("Tester 2", IPlayer.ID.TWO, IPlayer.Type.COMPUTER, Player.Level.AVERAGE, CheckerColor.BLACK);
	}
	
	@Test
	public void testTwoChekersHitOfComuputer() {
		Game testPortes = new Portes() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(3, 7, 12, 12, 17, 17, 18, 19, 19, 22);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(0, 0, 1, 12, 12, 17, 17, 19, 19);
			}
		};
		testPortes.initialize(player1, player2, null);
		
		DiceGenerator.get().load(new int[]{1, 3});
		testPortes.dices.roll();
		
		boolean check = testPortes.checkIfAnyMoveIsPossible(player2, testPortes.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testPortes.findPlayableDices(player2, testPortes.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		Assert.assertEquals("No marked dices (all can be played)", 0, playableDices.usedCount());
	}
	
	@Test
	public void testOneCheckerHit() {
		Game testPortes = new Portes() {
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(0, 1, 2);
			};
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(19, 19);
			}
		};
		testPortes.initialize(player1, player2, null);
		
		DiceGenerator.get().load(new int[]{1, 6});
		testPortes.dices.roll();
		
		boolean check = testPortes.checkIfAnyMoveIsPossible(player1, testPortes.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testPortes.findPlayableDices(player1, testPortes.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		Assert.assertEquals("No marked dices (all can be played)", 0, playableDices.usedCount());
	}
}
