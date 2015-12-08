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
import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.impl.Board;
import net.ichmags.backgammon.setup.impl.Checker;
import net.ichmags.backgammon.setup.impl.Player;
import net.ichmags.backgammon.setup.impl.Position;

/**
 * {@code Fevga} is the {@link Game} with the following special features:
 * <ul>
 * <li>both {@link Player} move in the same direction over the {@link Board} shifted for a half {@link Board}:
 * {@link Player} 1 starts at {@link Position} 1 and sees {@link Player} 2 starting at {@link Position} 13.</li>
 * <li>the first {@link Checker} has to move beyond position 12 before another can be played</li>
 * <li>it is not allowed to block all {@link Position}s from 1 to 6, a gap must exists for the opponent</li>
 * <li>a {@link Position} is blocked with a single {@link Checker}</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class Fevga extends Game {
	
	/**
	 * Default constructor.
	 * 
	 * <b>Call {@link #initialize(IPlayer, IPlayer, ICommandProvider)} to make the instance usable.</b>
	 */
	public Fevga() {
		// nothing to do
	}
	
	@Override
	public String getName() {
		return "Fevga";
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
				Arrays.asList(26, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 27, /* just append opponent */ 0, 25),
				getCheckerPositionsPlayer2());
	}
	
	@Override
	public boolean isValidMoveTarget(IPlayer player, IBoard board, int fromPosition, int toPosition) {
		boolean checkFirstCheckerBeyond12OK = false;
		boolean checkBlockFirst6OK = true;
		boolean checkTargetPositionOK = false;
		
		IPositions playerPositions = board.createPlayerView(player);
		IPosition targtePosition = playerPositions.get(toPosition);
		
		// check if the first checker is played beyond position 12 or this is the first one 
		// before playing another checker
		for(int index = 24; index > 0; index--) {
			IPosition pos = playerPositions.get(index);
			IChecker posChecker = pos.readTopChecker();
			if((posChecker != null) && posChecker.getOwner().equals(player.getID())) {
				if(index > 12) {
					checkFirstCheckerBeyond12OK = true;
				} else if (index == fromPosition){
					checkFirstCheckerBeyond12OK = true;
				}
				break;
			}
		}
		
		// check blocking of the first 6 positions is not allowed
		if(toPosition < 7) {
			for(int index = 1; index < 7; index++) {
				IPosition pos = playerPositions.get(index);
				IChecker posChecker = pos.readTopChecker();
				if((posChecker == null) || (posChecker.getOwner().equals(player.getID()) == false)) {
					if(index == toPosition) {
						checkBlockFirst6OK = false;
					} else {
						checkBlockFirst6OK = true;
						break;
					}
				} else if ( (fromPosition == index) && (pos.getNrOfCheckers() == 1)
						&& (posChecker != null) && posChecker.getOwner().equals(player.getID()) ) {
					// the starting position will be empty after the move
					checkBlockFirst6OK = true;
					break;
				}
			}
		}
		
		if(targtePosition.hasCheckers() == false) {
			checkTargetPositionOK = true;
		} else {
			if(targtePosition.readTopChecker().getOwner().equals(player.getID())) {
				checkTargetPositionOK = true;
			}
		}
		
		return (checkFirstCheckerBeyond12OK && checkBlockFirst6OK && checkTargetPositionOK);
	}
	
	@Override
	public void beforeMoveChecker(IPlayer player, IBoard board, IMove move) {
		// nothing to do: a single checker blocks the position
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
		return BoardView.START_TOP_RIGHT;
	}
	
	@Override
	public BoardView getInverseBoardView(BoardView opponentView) {
		BoardView matchingView = null;
		
		switch(opponentView) {
		case START_TOP_RIGHT:
			matchingView = BoardView.START_BOTTOM_LEFT;
			break;
		case START_TOP_LEFT:
			matchingView = BoardView.START_BOTTOM_RIGHT;
			break;
		case START_BOTTOM_LEFT:
			matchingView = BoardView.START_TOP_RIGHT;
			break;
		case START_BOTTOM_RIGHT:
			matchingView = BoardView.START_TOP_LEFT;
			break;
		default:
			matchingView = opponentView;
			break;
		}
		
		return matchingView;
	}
}
