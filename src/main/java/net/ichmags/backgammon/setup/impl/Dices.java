/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ichmags.backgammon.notification.impl.StatusEmitter;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDice.Status;
import net.ichmags.backgammon.setup.IDices;

/**
 * Implementation of the {@link IDices} {code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Dices implements IDices {

	/**
	 * A {@code RollableDice} is an implementation of the {@link IDice} interface.
	 * It is using the {@link DiceGenerator} to obtain values when the {@code Dice} is thrown.
	 * 
	 * The class is implemented as {@code private} class within the {@link Dices} class to
	 * make it impossible to change the {@link IDice} value.
	 * This is much like the {@code C++} concept of a {@code friend} class, which is the only one
	 * that can call private methods. If the {@code RollableDice} would not be nested in
	 * {@link Dices}, any class within the {@code package} could change the dice value.
	 * 
	 * @author Anastasios Patrikis
	 */
	private class RollableDice implements IDice {
		private int value;
		private Status status;
		private int cloneGeneration;
		
		/**
		 * Constructor.
		 */
		private RollableDice() {
			value = 0;
			status = Status.AVAILABLE;
			cloneGeneration = 0;
		}
		
		/**
		 * Roll the {@link IDice} to generate a new value.
		 * A {@link DiceGenerator} is used to obtain a value.
		 * 
		 * @return The {@code Dice} object.
		 */
		private RollableDice roll() {
			value = DiceGenerator.get().roll();
			return this;
		}
		
		@Override
		public int getValue() {
			return value;
		}
		
		@Override
		public boolean isClone() {
			return cloneGeneration > 0;
		}
		
		@Override
		public Status getStatus() {
			return status;
		}
		
		@Override
		public IDice setStatus(Status newStatus) {
			this.status = newStatus;
			return this;
		}
		
		/**
		 * This is the {@link Object} {@code clone()} method with reduced visibility
		 * 
		 * @return the {@code clone} of the current object.
		 */
		private RollableDice privateClone() {
			RollableDice clone = new RollableDice();
			clone.value = value;
			clone.status = status;
			clone.cloneGeneration = cloneGeneration + 1;
			
			return clone;
		}
		
		@Override
		public String toString() {
			switch (status) {
				case USED:
					return "(" + Integer.toString(getValue()) + ")";
				case BLOCKED:
					return "/" + Integer.toString(getValue()) + "/";
				default:
					return Integer.toString(getValue());
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + value;
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
			RollableDice other = (RollableDice) obj;
			if ((value != other.value) || (status != other.status))
				return false;
			return true;
		}

		@Override
		public int compareTo(IDice o) {
			if (this == o)
				return 0;
			if (o == null)
				return -1;
			
			return (getValue() - o.getValue());
		}
	}
	
	private List<RollableDice> dices;
	private int cloneGeneration;
	
	/**
	 * Constructor.
	 */
	public Dices() {
		dices = new ArrayList<>(4);
		dices.add(new RollableDice());
		dices.add(new RollableDice());
		cloneGeneration = 0;
	}
	
	@Override
	public IDices roll() {
		if(!isClone()) {
			dices.removeIf(dice -> dice.isClone());
			assert (dices.size() == 2);
			dices.forEach(dice -> dice.roll().setStatus(Status.AVAILABLE));
			
			if(isDoubleDices()) {
				dices.add(dices.get(0).privateClone());
				dices.add(dices.get(0).privateClone());
			}
			return this;
		} else {
			StatusEmitter.get().info("dices.no_roll");
			return null;
		}
	}
	
	@Override
	public boolean isDoubleDices() {
		return (dices.get(0).getValue() == dices.get(1).getValue());
	}
	
	@Override
	public List<IDice> get() {
		return Collections.unmodifiableList(dices);
	}
	
	@Override
	public IDice get(int diceIndex)
	throws IndexOutOfBoundsException {
		return dices.get(diceIndex);
	}
	
	@Override
	public IDice getUnused(int diceValue) {
		for(IDice dice : dices) {
			if((dice.getValue() == diceValue) && Status.AVAILABLE.equals(dice.getStatus()) ){
				return dice;
			}
		}
		return null;
	}
	
	@Override
	public boolean allUsed() {
		for(IDice dice : dices) {
			if(Status.AVAILABLE.equals(dice.getStatus())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int usedCount() {
		int count = 0;
		for(IDice dice : dices) {
			if( ! Status.AVAILABLE.equals(dice.getStatus())) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public IDices inheritStatus(IDices masterDices) {
		if(masterDices.get().size() != dices.size()) {
			throw new RuntimeException("Dice count mismatch");
		}
		
		for(int pos = 0; pos < dices.size(); pos++) {
			if(masterDices.get(pos).getValue() != dices.get(pos).getValue()) {
				throw new RuntimeException("Dice values mismatch");
			}
		}
		
		for(int pos = 0; pos < dices.size(); pos++) {
			masterDices.get(pos).setStatus(dices.get(pos).getStatus());
		}
		return this;
	}
	
	@Override
	public boolean isClone() {
		return cloneGeneration > 0;
	}
	
	@Override
	public Dices clone() {
		Dices clone = new Dices();
		clone.dices.clear();
		this.dices.forEach(dice -> clone.dices.add(dice.privateClone()));
		clone.cloneGeneration = cloneGeneration + 1;
		
		return clone;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dices == null) ? 0 : dices.hashCode());
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
		Dices other = (Dices) obj;
		if (dices == null) {
			if (other.dices != null)
				return false;
		} else if (!dices.equals(other.dices))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ("Dices: " + dices);
	}
}
