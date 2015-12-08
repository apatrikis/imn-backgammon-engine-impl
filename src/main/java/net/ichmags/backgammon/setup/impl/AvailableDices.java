/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import net.ichmags.backgammon.setup.IAvailableDices;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;

/**
 * Implementation of the {@link IAvailableDices} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class AvailableDices implements IAvailableDices {

	private List<IDice> wrappedDices;
	private int maxPos;
	private int currentPos;
	
	/**
	 * Default Constructor.
	 * 
	 * <b>Call {@link #initialize(IDices, boolean)} to make the instance usable.</b>
	 */
	public AvailableDices() {
		currentPos = -1;
	}
	
	@Override
	public IAvailableDices initialize(IDices dicesToWrap, boolean reverseOrder) {
		wrappedDices = dicesToWrap.get();
		if(reverseOrder == true) {
			wrappedDices = new ArrayList<>(wrappedDices); // // because get() returns unmodifiable
			Collections.reverse(wrappedDices);
		}
		maxPos = wrappedDices.size() - 1;
		
		return this;
	}
	
	@Override
	public boolean hasMoreElements() {
		if(currentPos == maxPos) {
			return false;
		}
		
		for(int pos = currentPos + 1; pos < wrappedDices.size(); pos++) {
			if(IDice.Status.AVAILABLE.equals(wrappedDices.get(pos).getStatus())) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public IDice nextElement()
	throws NoSuchElementException {
		if(currentPos == maxPos) {
			throw new NoSuchElementException("Last available dice already checked");
		}
		
		for(int pos = currentPos + 1; pos < wrappedDices.size(); pos++) {
			if(IDice.Status.AVAILABLE.equals(wrappedDices.get(pos).getStatus())) {
				currentPos = pos;
				return wrappedDices.get(pos);
			}
		}
		
		// if "hasMoreElements" was not used
		throw new NoSuchElementException("No more available dices, use hasMoreElements() to avoid this exception");
	}
	
	@Override
	public IAvailableDices reactivateElement()
	throws NoSuchElementException {
		if(currentPos == -1) {
			throw new NoSuchElementException("No last dice available: use nextElement() first");
		} else {
			wrappedDices.get(currentPos--).setStatus(IDice.Status.AVAILABLE);
			for(; currentPos > -1; currentPos--) {
				if( ! IDice.Status.AVAILABLE.equals(wrappedDices.get(currentPos))) {
					break;
				}
			}
			return this;
		}
	}
	
	@Override
	public String toString() {
		return wrappedDices.toString() + ", current: " + currentPos;
	}
}
