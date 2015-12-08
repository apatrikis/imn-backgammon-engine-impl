/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.setup.BoardView;
import net.ichmags.backgammon.setup.IBoard;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.PositionColor;
import net.ichmags.backgammon.setup.tool.BoardVisualizer;

/**
 * Implementation of the {@link IBoard} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Board implements IBoard {

	private IGame game;
	private Positions absolutePositions;
	private Map<BoardView, List<Integer>> boardViews;
	private int cloneGeneration;
	
	/**
	 * Default constructor.
	 * {@code private} because only to be used by {@link #clone()}.
	 */
	private Board() {
	}
	
	/**
	 * Constructor.
	 * 
	 * @param game the {@link IGame} to play on the {@code Board}.
	 */
	public Board(IGame game) {
		this.game = game;
		setup();
	}
	
	@Override
	public IBoard setInitialCheker(IPlayer player) {
		IPositions playerPositions = createPlayerView(player);
		List<Integer> initPositions = game.getGamePlayerConfig(player.getID()).getRelativeInitialCheckerPositions();
		for(Integer pos : initPositions) {
			playerPositions.get(pos).setTopChecker(new Checker(player));
		}
		return this;
	}
	
	@Override
	public IPositions createPlayerView(IPlayer player) {
		BoardView playerView = player.getBoardView();
		return createMappedView(playerView, playerView);
	}
	
	/**
	 * <b>This method is essential because it sets up the logic for moving the {@link Player} {@link Checker}
	 * for a {@link IGame} and displaying the {@link Board}</b>.
	 * 
	 * The {@link Positions} will be set up like described in the {@link Board} class documentation:
	 * <ul>
	 * <li>create absolute {@link Positions}</li>
	 * <li>create {@link IGame} and {@link Player} depending {@link Positions}</li>
	 * <li>create {@link BoardView} specific {@link Positions}</li>
	 * </ul>
	 */
	private void setup() {
		boardViews = new HashMap<>(10, 0.9f);
		
		// Step 1: create the absolute positions
		absolutePositions = new Positions();
		
		// special positions: out (for all games) / hit (only for Backgammon)
		// player 1: 0 / 25
		// player 2: 26 / 27
		for(int pos = 0; pos < 28; pos++) {
			absolutePositions.add(new Position(pos, PositionColor.values()[pos%2]));
		}
		
		// Step 2: create a view on the absolute board positions
		boardViews.put(BoardView.ABSOLUTE, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27));
		
		// Step 3: create the game depending view of the players on top of the absolute board
		boardViews.put(BoardView.PLAYER1, game.getGamePlayerConfig(IPlayer.ID.ONE).getAbsolutePlayPositions());
		boardViews.put(BoardView.PLAYER2, game.getGamePlayerConfig(IPlayer.ID.TWO).getAbsolutePlayPositions());
		
		// Step 3: create the visualizing views on top of a player's start position, without the "out" positions
		// Goes from left to right and top top bottom, out positions are always on the same position
		boardViews.put(BoardView.START_TOP_RIGHT, Arrays.asList(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 27, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 , 24, 25));
		boardViews.put(BoardView.START_TOP_LEFT, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 27, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 25));
		boardViews.put(BoardView.START_BOTTOM_LEFT, Arrays.asList(24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 27, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 25));
		boardViews.put(BoardView.START_BOTTOM_RIGHT, Arrays.asList(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 ,24, 27, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 25));
	}
	
	/**
	 * Create the {@link Positions}; the order is a transformation from a {@link BoardView} to another.
	 * This is useful when a the {@link Positions} should be prepared for output.
	 * 
	 * The source {@link BoardView} is a mapping from the absolute {@link Board} positions, for example for
	 * generating the positions for a {@link Player}.
	 * 
	 * The target {@link BoardView} maps the {@link Player} {@link Positions} into a {@link Positions} order
	 * suitable for generating output.
	 * 
	 * @param sourceView the source {@link BoardView} on the {@link Board}
	 * @param targetView the target {@link BoardView} in which to map the source.
	 * In case the source and target {@link BoardView} is identical no mapping happens.
	 * @return the {@link Positions} mapped from a {@link BoardView} into another.
	 */
	public IPositions createMappedView(BoardView sourceView, BoardView targetView) {
		List<Integer> sourcePositions = getAbsolutePositions(sourceView);
		List<Integer> targetPositions = getAbsolutePositions(targetView);
		
		IPositions mappedView = new Positions();
		for(Integer targetIndex : targetPositions) {
			if(sourceView.equals(targetView)) {
				mappedView.add(absolutePositions.get(targetIndex));
			} else {
				Integer sourceIndex = sourcePositions.get(targetIndex);
				mappedView.add(absolutePositions.get(sourceIndex));
			}
		}
		
		return mappedView;
	}
	
	public List<Integer> getAbsolutePositions(BoardView view) {
		return Collections.unmodifiableList(boardViews.get(view));
	}
	
	@Override
	public boolean isClone() {
		return cloneGeneration > 0;
	}
	
	@Override
	public Board clone() {
		Board clone = new Board();
		clone.game = this.game;
		clone.boardViews = this.boardViews; // no cloning needed
		clone.absolutePositions = this.absolutePositions.clone();
		clone.cloneGeneration = this.cloneGeneration + 1;
		
		return clone;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((absolutePositions == null) ? 0 : absolutePositions
						.hashCode());
		result = prime * result
				+ ((boardViews == null) ? 0 : boardViews.hashCode());
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (absolutePositions == null) {
			if (other.absolutePositions != null)
				return false;
		} else if (!absolutePositions.equals(other.absolutePositions))
			return false;
		if (boardViews == null) {
			if (other.boardViews != null)
				return false;
		} else if (!boardViews.equals(other.boardViews))
			return false;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new BoardVisualizer(null, game, this).toString();
	}
	
	@Override
	public String toString(IPlayer player, IGame game) {
		return new BoardVisualizer(player, game, this).toString();
	}
}
