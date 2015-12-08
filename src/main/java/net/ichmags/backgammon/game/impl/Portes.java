/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import java.util.Arrays;
import java.util.List;

import net.ichmags.backgammon.CommonEngine;
import net.ichmags.backgammon.exception.InvalidMoveException;
import net.ichmags.backgammon.game.IMove;
import net.ichmags.backgammon.game.pojo.GamePlayerConfig;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.notification.INotification.Level;
import net.ichmags.backgammon.notification.impl.StatusEmitter;
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
 * {@code Portes} is the {@link Game} with the following special features:
 * <ul>
 * <li>the {@link Player} move towards each other</li>
 * <li>a {@link Position} is blocked if it has 2 or more {@link Checker}, which will be from the same {@link Player}</li>
 * <li>a single opponent {@link Checker} on a {@link Position} may be <i>hit</i> out the {@link Game}</li>
 * <li>any number of {@link Checker} can be <i>hit</i></li>
 * <li>a <i>hit</i> {@link Checker} will be out of the {@link Board} and need to come back into the {@link Game}
 * by entering on a {@link Position} 1 to 6; in case the {@link Player} cannot enter the {@link Board} because
 * the {@link Position}s are blocked the opponent will continue the {@link Game}</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class Portes extends Game {
	
	/**
	 * Default constructor.
	 * 
	 * <b>Call {@link #initialize(IPlayer, IPlayer, ICommandProvider)} to make the instance usable.</b>
	 */
	public Portes() {
		// nothing to do
	}
	
	@Override
	public String getName() {
		return "Portes";
	}
	
	@Override
	protected List<Integer> getCheckerPositionsPlayer1() {
		return Arrays.asList(1, 1, 12, 12, 12, 12, 12, 17, 17, 17, 19, 19, 19, 19, 19);
	}
	
	@Override
	protected List<Integer> getCheckerPositionsPlayer2() {
		return Arrays.asList(1, 1, 12, 12, 12, 12, 12, 17, 17, 17, 19, 19, 19, 19, 19);
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
		IPosition outPosition = playerPositions.get(0);
		
		if((fromPosition > 0) && outPosition.hasCheckers()) {
			StatusEmitter.get().emit(board.isClone() ? Level.TRACE : Level.INFO, "game.play_mandatory_moves");
		} else if(targetPos.getNrOfCheckers() < 2) {
			canMove = true;
		} else {
			if(targetPos.hasCheckers() && targetPos.readTopChecker().getOwner().equals(player.getID())) {
				canMove =  true;
			}
		}
		
		return canMove;
	}
	
	@Override
	public void beforeMoveChecker(IPlayer player, IBoard board, IMove move)
	throws InvalidMoveException {
		if(isValidMoveTarget(player, board, move.getFromPosition(), move.getToPosition())) {
			
			IPositions playerPositions = board.createPlayerView(player);
			IPosition targetPos = playerPositions.get(move.getToPosition());
			
			if((targetPos.getNrOfCheckers() == 1) && (targetPos.readTopChecker().getOwner().equals(player.getID()) == false)) {
				IChecker kickedChecker = targetPos.removeTopChecker();
				StatusEmitter.get().emit(board.isClone() ? Level.TRACE : Level.INFO, "game.portes.kick_opponent");
				move.setOpponentHitChecker(kickedChecker);
				
				IPlayer opponent = CommonEngine.getOponent(player, player1, player2);
				IPosition opponentPosition0 = board.createPlayerView(opponent).get(0);
				opponentPosition0.setTopChecker(kickedChecker);
			} 
		} else {
			throw new InvalidMoveException("Move not possible: call canMove() first!");
		}
	}
	
	@Override
	public boolean hasPosition0() {
		return true;
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
