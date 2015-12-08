/*
 *  www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.Arrays;

import net.ichmags.backgammon.game.impl.Fevga;
import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IBoard;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPositions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anastasios Patrikis
 */
public class TestBoard {

	private IPlayer player1;
	private IPlayer player2;
	
	@Before
	public void setUp() {
		player1 = new Player().initialize("Tester 1", IPlayer.ID.ONE, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.WHITE);
		player2 = new Player().initialize("Tester 2", IPlayer.ID.TWO, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.BLACK);
	}
	
	/**
	 * Test clone() method.
	 */
	@Test
	public void testEquals() {
		class FevgaLocal extends Fevga{
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1);
			};
			
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1);
			}
			
			public IBoard getBoard() {
				return this.board;
			}
		};
		FevgaLocal testFevga = new FevgaLocal();
		testFevga.initialize(player1, player2, null);
		
		Board testBoard = new Board(testFevga);
		Assert.assertNotEquals("Different board after constructor", testBoard, testFevga.getBoard());
		
		testBoard.setInitialCheker(player1);
		Assert.assertNotEquals("Different board after setup of player 1", testBoard, testFevga.getBoard());
		
		testBoard.setInitialCheker(player2);
		Assert.assertEquals("Equivalent board after setup of player 1 and 2", testBoard, testFevga.getBoard());
	}
	
	/**
	 * Test clone() method.
	 */
	@Test
	public void testClone() {
		class FevgaLocal extends Fevga{
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(1);
			};
			
			@Override
			protected java.util.List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1);
			}
			
			public IBoard getBoard() {
				return this.board;
			}
		};
		FevgaLocal testFevga = new FevgaLocal();
		testFevga.initialize(player1, player2, null);
		
		DiceGenerator.get().load(new int[]{1, 2});
		IDices dices = new Dices().roll();
		
		IBoard testBoard = testFevga.getBoard().clone();
		Assert.assertFalse("Original has no flag", testFevga.getBoard().isClone());
		Assert.assertTrue("Clone has flag", testBoard.isClone());
		Assert.assertEquals("Clone is initial identical to original", testBoard, testFevga.getBoard());
		
		boolean check = testFevga.moveChecker(player1, testBoard, 1, dices.getUnused(1)).isSuccess();
		Assert.assertTrue("First move is possible", check);
		Assert.assertNotEquals("After move, clone is different", testBoard, testFevga.getBoard());
		
		IPositions playerPositions = testBoard.createPlayerView(player1);
		Assert.assertEquals("Clone has no checker on position 1", playerPositions.get(1).getNrOfCheckers(), 0);
		Assert.assertEquals("Clone has checker on position 2", playerPositions.get(2).getNrOfCheckers(), 1);
		
		playerPositions = testFevga.getBoard().createPlayerView(player1);
		Assert.assertEquals("Original has checker on position 1", playerPositions.get(1).getNrOfCheckers(), 1);
		Assert.assertEquals("Original has no checker on position 2", playerPositions.get(2).getNrOfCheckers(), 0);
	}
}
