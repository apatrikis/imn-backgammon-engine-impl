/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.statistic.impl;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.setup.impl.Dices;
import net.ichmags.backgammon.statistic.IPlayerStatistics;

/**
 * Implementation of the {@link IPlayerStatistics} {code interface}.
 * 
 * {@code PlayerStatistics} keep track of informations of the played match:
 * <ul>
 * <li>count of played {@link Dices}</li>
 * <li>count of games won</li>
 * <li>count of games lost</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class PlayerStatistics extends DiceStatistics implements IPlayerStatistics {
	
	private int[] timesWon;
	private int[] timesLost; 
	
	/**
	 * Constructor.
	 * 
	 * @param id A {@link String} that will be displayed in the {@link #toString()} method.
	 */
	public PlayerStatistics(String id) {
		super("Player " + id);
		
		timesWon = new int[]{0, 0};
		timesLost = new int[]{0, 0};
	}
	
	/**
	 * Count a lost match.
	 */
	@Override
	public void addMatchDefeat() {
		timesLost[1]++;
	}
	
	/**
	 * Count a winning match.
	 */
	@Override
	public void addMatchVictory() {
		timesWon[1]++;		
	}
	
	@Override
	public void addGameDefeat(int value) {
		timesLost[0]+=value;
	}
	
	@Override
	public void addGameVictory(int value) {
		timesWon[0]+=value;
	}
	
	@Override
	public int getGameVictories() {
		return timesWon[0];
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()).append(Common.NEWLINE);
		sb.append("Games won/lost  : ").append(timesWon[0]).append("/").append(timesLost[0]).append(Common.NEWLINE);
		sb.append("Matches won/lost: ").append(timesWon[1]).append("/").append(timesLost[1]).append(Common.NEWLINE);
		return sb.toString();
	}
}
