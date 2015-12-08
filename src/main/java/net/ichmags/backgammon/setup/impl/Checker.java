/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IPlayer;

/**
 * Implementation of the {@link IChecker} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Checker implements IChecker {
	private CheckerColor color;
	private IPlayer.ID owner;
	
	/**
	 * Constructor.
	 * 
	 * @param owner the owner of the {@code Checker}; the color is derived from the {@link Player}'s choice. 
	 */
	public Checker(IPlayer owner) {
		this.color = owner.getCheckerColor();
		this.owner = owner.getID();
	}
	
	@Override
	public CheckerColor getColor() {
		return color;
	}
	
	@Override
	public IPlayer.ID getOwner() {
		return owner;
	}
	
	@Override
	public String toString() {
		return color.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		Checker other = (Checker) obj;
		if (color != other.color)
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
}
