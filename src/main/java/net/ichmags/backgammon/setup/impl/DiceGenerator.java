/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.ichmags.backgammon.game.impl.Game;
import net.ichmags.backgammon.notification.impl.StatusEmitter;
import net.ichmags.backgammon.setup.IDice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code DiceGenerator} is producing {@link Random} values for a {@link IDice}.
 * This standard behavior can be overridden:
 * <ul>
 * <li>by specifying the {@link Random} {@code seed}</li>
 * <li>a series of {@link Integer} values to use</li>
 * <li>a {@link InputStream} to acquire the values</li>
 * </ul>
 * It may be useful to use predictable values, so a {@link Game} can be replayed. 
 * 
 * @author Anastasios Patrikis
 */
public class DiceGenerator {
	
	private static Logger LOG = LoggerFactory.getLogger(DiceGenerator.class);

	/**
	 * {@code Singleton} pattern.
	 */
	private static DiceGenerator INSTANCE = new DiceGenerator();
	
	private Random rnd;
	private List<Integer> predefinedValues;
	private long randomSeed;
	
	/**
	 * {@code private} default {@link Constructor} to enforce the {@code singleton} pattern.
	 */
	private DiceGenerator() {
		rnd = new Random() { // because there is no "getSeed": retrieve the used seed at startup time 
			private static final long serialVersionUID = 1L;

			@Override
			public synchronized void setSeed(long seed) {
				randomSeed = seed;
				super.setSeed(seed);
			}
		};
		predefinedValues = new LinkedList<>();
	}
	
	/**
	 * Get a reference to the {@code singleton} instance.
	 * 
	 * @return the reference to the sole {@link Object} of this class.
	 */
	public static DiceGenerator get() {
		return INSTANCE;
	}
	
	/**
	 * Get the next value.
	 * 
	 * @return a new {@link Random} value or a predefined value. In case predefined values were used
	 * and all of them were consumed, {@link Random} values will be generated.
	 */
	public int roll() {
		if(predefinedValues.isEmpty()) {
			return (rnd.nextInt(6)+1);
		} else {
			synchronized (rnd) {
				int next = predefinedValues.remove(0).intValue();
				if(predefinedValues.isEmpty()) {
					StatusEmitter.get().info("dicegenerator.loaded_values_exhausted");
				}
				return next;
			}
		}
	}
	
	/**
	 * Load values to return when {@link #roll()} is called.
	 * A value is loaded when it is between 1 and 6.
	 * 
	 * @param values the values to load.
	 */
	public void load(int[] values) {
		boolean loadErrors = false;
		
		if((values != null) && (values.length > 0)) {
			for(int pos = 0; pos < values.length; pos++) {
				int val = values[pos];
				if((val > 0) && (val < 7)) {
					predefinedValues.add(new Integer(val));
				} else {
					loadErrors = true;
				}
			}
			StatusEmitter.get().info(loadErrors ? "dicegenerator.load_finish_errors" : "dicegenerator.load_finish_ok");
		} else {
			StatusEmitter.get().info("dicegenerator.nothing_to_do");
		}
	}
	
	/**
	 * Load values to return when {@link #roll()} is called.
	 * A value is loaded when it is between 1 and 6.
	 * 
	 * @param diceValues the values to load.
	 */
	public void load(InputStream diceValues) {
		try {
			Reader diceReader = new InputStreamReader(diceValues);
			int charValue = 0;
			while((charValue = diceReader.read()) != -1) {
				int dice = Character.getNumericValue(charValue);
				if((dice > 0) && (dice < 7)) {
					predefinedValues.add(new Integer(dice));
				}					
			}
		} catch (IOException e) {
			LOG.error("Error loading dice values from input stream", e);
		}
	}
	
	/**
	 * Set the {@link Random} {@code seed} value to use.
	 * Knowing the seed means knowing the values {@link Random} will generate.
	 * 
	 * @param seed the {@code seed} value to use for {@link Random} value generation. 
	 */
	public void setSeed(long seed) {
		rnd.setSeed(seed);
		StatusEmitter.get().info("dicegenerator.seed_set");
	}
	
	/**
	 * Get the used {@link Random} {@code seed} value.
	 * It can be used for reproducing the sequence of generated {@link Random} values.
	 * 
	 * @return the current {@link Random} {@code seed} value.
	 */
	public long getSeed() { 
		return randomSeed;
	}
}
