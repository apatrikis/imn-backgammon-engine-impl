/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;

/**
 * Implementation of the {@link IPositions} {code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Positions implements IPositions {

	private List<IPosition> positionSequence;
	private int cloneGeneration;
	
	/**
	 * Default constructor.
	 */
	public Positions() {
		positionSequence = new LinkedList<>();
	}
	
	@Override
	public IPositions add(IPosition position) {
		positionSequence.add(position);
		return this;
	}
	
	@Override
	public IPosition get(int index)
	throws IndexOutOfBoundsException {
		return positionSequence.get(index);
	}
	
	@Override
	public List<IPosition> get() {
		return Collections.unmodifiableList(positionSequence);
	}
	
	@Override
	public int indexOf(IPosition position) {
		return positionSequence.indexOf(position);
	}
	
	@Override
	public boolean isClone() {
		return cloneGeneration > 0;
	}
	
	@Override
	public Positions clone() {
		Positions clone = new Positions();
		
		this.positionSequence.forEach(position -> clone.positionSequence.add(position.clone()));
		clone.cloneGeneration = this.cloneGeneration + 1;
		
		return clone;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((positionSequence == null) ? 0 : positionSequence.hashCode());
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
		Positions other = (Positions) obj;
		if (positionSequence == null) {
			if (other.positionSequence != null)
				return false;
		} else if (!positionSequence.equals(other.positionSequence))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder dump = new StringBuilder();
		
		for(IPosition position : positionSequence) {
			int index = position.getCreationIndex();
			int height = position.getNrOfCheckers();
			dump.append((index < 10) ? "0" : "").append(index).append("|");
			for(int level = 0; level < height; level++) {
				dump.append(position.readChecker(level));
			}
			dump.append(Common.NEWLINE);
		}
		
		return dump.toString();
	}
}
