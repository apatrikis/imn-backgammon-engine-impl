/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.Stack;

import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.PositionColor;

/**
 * Implementation of the {@link IPosition} {code interface}.
 *  
 * @author Anastasios Patrikis
 */
public class Position implements IPosition {
	private int index;
	private PositionColor color;
	private Stack<IChecker> checkers;
	private int cloneGeneration; 
	
	/**
	 * Constructor.
	 * 
	 * @param index the absolute index of the {@link Board} that uniquely identifies the {@code Position}.
	 * @param color the {@link PositionColor} to use when the {@code Position} is displayed.
	 */
	public Position(int index, PositionColor color) {
		this.index = index;
		this.color = color;
		checkers = new Stack<IChecker>();
	}
	
	@Override
	public int getCreationIndex() {
		return index;
	}
	
	@Override
	public int getIndexIn(IPositions viewPositions) {
		return viewPositions.indexOf(this);
	}
	
	@Override
	public PositionColor getColor() {
		return color;
	}
	
	@Override
	public boolean hasCheckers() {
		return !checkers.isEmpty();
	}
	
	@Override
	public boolean hasCheckerOfPlayer(IPlayer.ID playerID) {
		if(checkers.empty()) {
			return false;
		} else {
			return (checkers.peek().getOwner().equals(playerID) || checkers.firstElement().getOwner().equals(playerID));
		}
	}
	
	@Override
	public int getNrOfCheckers(){
		return checkers.size();
	}
	
	@Override
	public IChecker readChecker(int index) {
		assert (index >= 0);
		
		if(index < checkers.size()) {
			return checkers.get(index);
		} else {
			return null;
		}
	}
	
	@Override
	public IChecker readTopChecker() {
		return hasCheckers() ? checkers.peek() : null;
	}
	
	@Override
	public void setTopChecker(IChecker newChecker) {
		checkers.push(newChecker);
	}
	
	@Override
	public IChecker removeTopChecker() {
		return checkers.pop();
	}
	
	@Override
	public boolean isClone() {
		return cloneGeneration > 0;
	}
	
	@Override
	public Position clone() {
		Position clone = new Position(index, color);
		clone.cloneGeneration = cloneGeneration + 1;
		
		for(IChecker checker : checkers) {
			clone.setTopChecker(checker);
		}
		
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + index;
		result = prime * result + ((checkers == null) ? 0 : checkers.hashCode());
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
		Position other = (Position) obj;
		if (index != other.index)
			return false;
		if (checkers == null) {
			if (other.checkers != null)
				return false;
		} else if (!checkers.equals(other.checkers))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Position ").append(index).append(": ");
		checkers.forEach(checker -> sb.append(checker));
		return sb.toString();
	}
}
