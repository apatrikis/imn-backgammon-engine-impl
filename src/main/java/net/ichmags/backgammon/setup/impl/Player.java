/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.Hashtable;
import java.util.Map;

import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.setup.BoardView;
import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.statistic.IPlayerStatistics;
import net.ichmags.backgammon.statistic.impl.PlayerStatistics;

/**
 * Implementation of the {@link IPlayer} {code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Player implements IPlayer {
	
	private String name;
	private IPlayer.ID id;
	private IPlayer.Type type;
	private IPlayer.Level level;
	private IPlayer.PlayStyle playStyle;
	private CheckerColor checkerColor;
	private Map<Class<? extends IGame>, BoardView> displayPreferences;
	private IPlayerStatistics statistics;
	
	/**
	 * Constructor.
	 */
	public Player() {
		this.playStyle = IPlayer.PlayStyle.SECURE;
		this.displayPreferences = new Hashtable<>(10, 0.9f);
		this.statistics = new PlayerStatistics(name);
	}
	
	@Override
	public IPlayer initialize(String name, IPlayer.ID id, IPlayer.Type type, IPlayer.Level level, CheckerColor checkerColor) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.checkerColor = checkerColor;
		this.level = level;
		
		return this;
	}
	
	@Override
	public BoardView getBoardView() {
		return (id.equals(IPlayer.ID.ONE)) ? BoardView.PLAYER1 : BoardView.PLAYER2;
	}
	
	@Override
	public BoardView getDisplayPreference(IGame gameType) {
		BoardView preference = displayPreferences.get(gameType.getClass());
		return (preference != null) ? preference : gameType.getDefaultBoardView();
	}
	
	@Override
	public BoardView setDisplayPreference(IGame game, BoardView boardView) {
		return displayPreferences.put(game.getClass(), boardView);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public IPlayer.ID getID() {
		return id;
	}
	
	@Override
	public IPlayer.Type getType() {
		return type;
	}
	
	@Override
	public IPlayer.Level getLevel() {
		return level;
	}
	
	@Override
	public CheckerColor getCheckerColor() {
		return checkerColor;
	}
	
	@Override
	public IPlayer.PlayStyle getPlayStyle() {
		return playStyle;
	}
	
	@Override
	public void setPlayStyle(IPlayer.PlayStyle playStyle) {
		this.playStyle = playStyle;
	}
	
	@Override
	public IPlayerStatistics getStatistics() {
		return statistics;
	}
	
	@Override
	public String toString() {
		return String.format("#%s: %s (%s)", id, getName(), checkerColor);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ getID().hashCode();
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
		Player other = (Player) obj;
		if (getID() != other.getID())
			return false;
		return true;
	}
}
