/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.statistic.impl;

import net.ichmags.backgammon.setup.impl.Dices;

/**
 * {@code MatchStatistics} keep track of informations of the played match, like:
 * <ul>
 * <li>count of played {@link Dices}</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class MatchStatistics extends DiceStatistics {

	public MatchStatistics() {
		super("Match");
	}
}
