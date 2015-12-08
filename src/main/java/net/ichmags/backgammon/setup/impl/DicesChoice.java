/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IDicesChoice;

/**
 * Implementation of the {@link IDicesChoice} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class DicesChoice implements IDicesChoice {
	
	private static final Logger LOG = LoggerFactory.getLogger(DicesChoice.class);
	
	private IDices option1;
	private IDices option2;
	private boolean conversionDone;
	
	/**
	 * Default constructor.
	 */
	public DicesChoice() {
		conversionDone = false;
	}
	
	/**
	 * Get the first {@link IDices} option.
	 * 
	 * @return the first {@link IDices} option, or {@code null}.
	 */
	public IDices getOption1() {
		return option1;
	}
	
	/**
	 * Get the second {@link IDices} option.
	 * 
	 * @return the second {@link IDices} option, or {@code null}.
	 * This will <b>always</b> be {@code null} in case {@link #getOption1()} returns
	 * fully playable {@link IDices}.
	 */
	public IDices getOption2() {
		return option2;
	}
	
	/**
	 * Check if only {@link #getOption1()} returns a {@link IDices} object.
	 * 
	 * @return {@code true} if only {@link #getOption1()} returns a {@link IDices} object.
	 */
	public boolean isSingleOption() {
		return ((option1 != null) && (option2 == null));
	}
	
	/**
	 * <p>
	 * <b>The {@link IDices} must be used during test, otherwise they will never be stored.</b>
	 * Before using them after the testing phase, {@link #convertStatusFromTestToPlay()} must be called.
	 * </p>
	 * 
	 * Stores the {@link IDices} option if:
	 * <ul>
	 * <li>fully playable {@link IDices} are passed for first time ({@link IDices#allUsed()})</li>
	 * <li>{@link #getOption1()} or {@link #getOption2()} is empty</li>
	 * </ul>
	 * Does no stores the {@link IDices} option if:
	 * <ul>
	 * <li>{@link IDices#usedCount()} is {@code 0}, because this means no dice was played during testing</li>
	 * <li>fully playable {@link IDices} are already stored ({@link IDices#allUsed()})</li>
	 * <li>{@link IDices} with the same {@link IDice#getStatus()} are already stored</li>
	 * <li>if {@link #getOption1()} or {@link #getOption2()} are occupied a {@link RuntimeException}
	 * is thrown</li>
	 * </ul>
	 * 
	 * @param newOption the potentially new {@link IDices} to store.
	 * @return {@code true} id the provided {@link IDices} have been stored.
	 */
	public boolean addOption(IDices newOption) {
		boolean success = true;
		
		if(newOption.usedCount() == 0) {
			LOG.debug("DicesChoice.add(): no dices used, not added: " + newOption.toString());
			success = false;
		} else if((option1 != null) && (option1.allUsed())) {
			LOG.debug("DicesChoice.add(): fully usable version already set, not added: " + newOption.toString());
			success = false;
		} else if (newOption.allUsed()) {
			LOG.debug("DicesChoice.add(): all dices used, setting single option: " + newOption.toString());
			option1 = newOption;
			option2 = null;
		} else if(contains(newOption)) {
			LOG.debug("DicesChoice.add(): the dice values are already stored: " + newOption.toString());
			success = false;
		} else {
			if(option1 == null) {
				option1 = newOption;
			} else if (option2 == null) {
				option2 = newOption;
			} else {
				throw new RuntimeException("Can not add another dices option: " + newOption.toString());
			}
		}
		
		return success;
	}
	
	/**
	 * Because all {@link IDices} are added with a testing status (see {@link #addOption(IDices)}),
	 * they need to inverted before they can be used for <i>real</i> moving during game play.
	 * 
	 * <ul>
	 * <li>{@link IDice.Status#AVAILABLE} becomes {@link IDice.Status#BLOCKED} since it could
	 * not be used for moving</li>
	 * <li>{@link IDice.Status#USED} becomes {@link IDice.Status#AVAILABLE} since it could
	 * be used.</li>
	 * </ul>
	 * 
	 * <b>The operation is not symetric, so it can be called only once.
	 * Therefore, it is important to call all {@link #addOption(IDices)} first.</b>
	 * 
	 * @return {@code true} if the conversion was performed, {@code false} after the first call.
	 */
	public boolean convertStatusFromTestToPlay() {
		if( ! conversionDone) {
			for(IDices choice : getAsList()) {
				for(IDice dice : choice.get()) {
					switch (dice.getStatus()) {
						case AVAILABLE:
							dice.setStatus(IDice.Status.BLOCKED);
							break;
						case USED:
							dice.setStatus(IDice.Status.AVAILABLE);
							break;
						case BLOCKED:
							throw new RuntimeException("The dice status blocked is invalid");
					}
				}
			}
			
			return (conversionDone = true); // assign and return (looks dirty, but is not)
		} else {
			return false;
		}
	}
	
	@Override
	public List<IDices> getAsList() {
		List<IDices> retVal = new ArrayList<IDices>(2);
		if(option1 != null) {
			retVal.add(option1);
		}
		if(option2 != null) {
			retVal.add(option2);
		}
		return retVal;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Option 1: ").append( (option1 == null) ? "-" : option1).append(Common.NEWLINE);
		sb.append("Option 2: ").append( (option2 == null) ? "-" : option2);
		
		return sb.toString();
	}
	
	/**
	 * Check if the provided {@link IDices} are already stored.
	 * This will check only against {@link #getOption1()}.
	 * The check is sorting the internal {@link IDice} objects, so the semantic identical
	 * {@code 1, (2)} and {@code (2), 1} will be detected.
	 * 
	 * @param newDices the potentially new {@link IDices} to check.
	 * @return {@code true} if the provide {@link IDices} are already stored.
	 */
	private boolean contains(IDices newDices) {
		if(option1 != null) {
			List<IDice> list1 = new ArrayList<>(option1.get());
			List<IDice> newList = new ArrayList<>(newDices.get());
			
			Collections.sort(list1);
			Collections.sort(newList);
			
			return list1.equals(newList);
		} else {
			return false;
		}
	}
}
