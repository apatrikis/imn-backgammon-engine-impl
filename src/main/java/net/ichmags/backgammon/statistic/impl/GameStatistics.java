/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.statistic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.impl.Dices;
import net.ichmags.backgammon.statistic.IGameStatistics;

/**
 * Implementation of the {@link IGameStatistics} {code interface}.
 * 
 * {@code GameStatistics} keep track of informations of the played game:
 * <ul>
 * <li>count of played {@link Dices}</li>
 * <li>recording of the order of {@link Dices}</li>
 * <li>count of the rotations between the two players</li>
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class GameStatistics extends DiceStatistics implements IGameStatistics {
	private int rotations;
	private List<Integer> diceRecording;
	
	/**
	 * Default constructor.
	 */
	public GameStatistics() {
		super("Game");
		
		rotations = 0;
		diceRecording = new ArrayList<>();
	}

	@Override
	public int getRotations() {
		return rotations;
	}

	@Override
	public void addDices(IDices dices) {
		super.addDices(dices);
		rotations++;
		
		dices.get().forEach(dice -> diceRecording.add(dice.getValue()));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()).append(Common.NEWLINE);
		sb.append("Rotations: ").append(rotations).append(Common.NEWLINE);
		sb.append("Dice recording: ").append(Arrays.toString(diceRecording.toArray())).append(Common.NEWLINE);
		return sb.toString();
	};
}
