/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import java.util.Arrays;
import java.util.List;

import net.ichmags.backgammon.game.IMove;
import net.ichmags.backgammon.game.pojo.GamePlayerConfig;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.setup.BoardView;
import net.ichmags.backgammon.setup.IBoard;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.impl.Checker;
import net.ichmags.backgammon.setup.impl.Player;
import net.ichmags.backgammon.setup.impl.Position;

/**
 * {@code Plakoto} is the {@link Game} with the following special features:
 * <ul>
 * <li>the {@link Player} move towards each other</li>
 * <li>a {@link Position} is blocked if it has 2 or more {@link Checker}, which may be from different
 * {@link Player}s</li>
 * <li>a single opponent {@link Checker} on a {@link Position} may be captured, and will be always the
 * <i>bottom {@link Checker}</i> of the {@link Position}</li>
 * <li>a captured {@link Checker} cannot be moved until it is released: the opponent has no more
 * {@link Checker} on that position</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class Plakoto extends Game {

	/**
	 * Default constructor.
	 * 
	 * <b>Call {@link #initialize(IPlayer, IPlayer, ICommandProvider)} to make the instance usable.</b>
	 */
	public Plakoto() {
		// nothing to do
	}
	
	@Override
	public String getName() {
		return "Plakoto";
	}
	
	@Override
	protected List<Integer> getCheckerPositionsPlayer1() {
		return Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
	}
	
	@Override
	protected List<Integer> getCheckerPositionsPlayer2() {
		return Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
	}
	
	@Override
	public void setupGamePlayerConfig() {
		player1Config = new GamePlayerConfig(IPlayer.ID.ONE,
				Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, /* just append opponent */ 26, 27),
				getCheckerPositionsPlayer1());
		player2Config = new GamePlayerConfig(IPlayer.ID.TWO,
				Arrays.asList(26, 24, 23 ,22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 27, /* just append opponent */ 0, 25),
				getCheckerPositionsPlayer2());
	}
	
	@Override
	public boolean isValidMoveTarget(IPlayer player, IBoard board, int fromPosition, int toPosition) {
		boolean canMove = false;
		
		IPositions playerPositions = board.createPlayerView(player);
		IPosition targetPos = playerPositions.get(toPosition);
		
		if(targetPos.getNrOfCheckers() < 2) {
			canMove = true;
		} else {
			if(targetPos.hasCheckers() && targetPos.readTopChecker().getOwner().equals(player.getID())) {
				canMove =  true;
			}
		}
		
		return canMove;
	}
	
	@Override
	public void beforeMoveChecker(IPlayer playerID, IBoard board, IMove move) {
		// nothing to do: a opponent's checker on this position will be captured
	}
	
	@Override
	public boolean hasPosition0() {
		return false;
	}
	
	@Override
	public boolean hasDoublingBoost(IDices dices) {
		return false;
	}
	
	@Override
	public BoardView getDefaultBoardView() {
		return BoardView.START_BOTTOM_RIGHT;
	}
	
	@Override
	public BoardView getInverseBoardView(BoardView opponentView) {
		BoardView matchingView = null;
		
		switch(opponentView) {
		case START_TOP_RIGHT:
			matchingView = BoardView.START_BOTTOM_RIGHT;
			break;
		case START_TOP_LEFT:
			matchingView = BoardView.START_BOTTOM_LEFT;
			break;
		case START_BOTTOM_LEFT:
			matchingView = BoardView.START_TOP_LEFT;
			break;
		case START_BOTTOM_RIGHT:
			matchingView = BoardView.START_TOP_RIGHT;
			break;
		default:
			matchingView = opponentView;
			break;
		}
		
		return matchingView;
	}
}
