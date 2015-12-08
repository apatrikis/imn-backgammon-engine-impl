/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.statistic.impl;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.impl.Dices;
import net.ichmags.backgammon.statistic.IDiceStatistics;

/**
 * Implementation of the {@link IDiceStatistics} {code interface}.
 * 
 * The {@code DiceStatistics} keep track of the played dices by counting the occurence of
 * the values 1 to 6, differentiating <i>doubles</i>.
 * 
 * @author Anastasios Patrikis
 */
public class DiceStatistics implements IDiceStatistics {
	private String display;
	private int[] simpleDices;
	private int[] doubleDices;
	
	/**
	 * Constructor.
	 * 
	 * @param display A {@link String} that will be displayed in the {@link #toString()} method.
	 */
	public DiceStatistics(String display) {
		this.display = display;
		simpleDices = new int[]{0, 0, 0, 0, 0, 0, 0};
		doubleDices = new int[]{0, 0, 0, 0, 0, 0, 0};
	}

	/**
	 * Count the occurences of the {@link IDice} values (1 to 6).
	 * If the {@link Dices} are <i>doubles</i> they will be counted separately.
	 * 
	 * @param dices The {@link Dices} values to count.
	 */
	@Override
	public void addDices(IDices dices) {
		if(dices.get().size() == 2) {
			simpleDices[dices.get(0).getValue()]++;
			simpleDices[dices.get(1).getValue()]++;
		} else {
			doubleDices[dices.get(0).getValue()]++;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Dice Statistics (").append(display).append(")").append(Common.NEWLINE);
		sb.append("[Simple");
		for(int pos = 1; pos <= 6; pos++) {
			sb.append(", ").append(pos).append("'s:").append(simpleDices[pos]);
		}
		sb.append("]").append(Common.NEWLINE);
		sb.append("[Double");
		for(int pos = 1; pos <= 6; pos++) {
			sb.append(", ").append(pos).append("'s:").append(doubleDices[pos]);
		}
		return sb.append("]").toString();
	}
}
