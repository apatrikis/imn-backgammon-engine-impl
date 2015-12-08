/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import net.ichmags.backgammon.game.IMove;
import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.Position;

/**
 * Implementation of the {@link IMove} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Move implements IMove {
	private IPlayer.ID playerID;
	private int fromPosition;
	private IDice moveDistance;
	private boolean success;
	private IChecker takeOutCheker;
	private IChecker opponentHitChecker;
	private int cloneGeneration; 
	
	/**
	 * Constructor.
	 * 
	 * @param playerID the {@link IPlayer.ID} who wants to play the {@code Move} 
	 * @param fromPosition the start {@link Position} of the move.
	 * @param moveDistance the {@link IDice} with the value to move.
	 */
	public Move(IPlayer.ID playerID, int fromPosition, IDice moveDistance) {
		this.playerID = playerID;
		this.fromPosition = fromPosition;
		this.moveDistance = moveDistance;
		
		this.success = false;
	}
	
	@Override
	public IPlayer.ID getPlayerID() {
		return playerID;
	}
	
	@Override
	public boolean isSuccess() {
		return success;
	}
	
	@Override
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	@Override
	public IChecker getTakeOutCheker() {
		return takeOutCheker;
	}
	
	@Override
	public void setTakeOutCheker(IChecker takeOutCheker) {
		this.takeOutCheker = takeOutCheker;
	}
	
	@Override
	public IChecker getOpponentHitChecker() {
		return opponentHitChecker;
	}
	
	@Override
	public void setOpponentHitChecker(IChecker opponentHitChecker) {
		this.opponentHitChecker = opponentHitChecker;
	}
	
	@Override
	public int getFromPosition() {
		return fromPosition;
	}
	
	@Override
	public IDice getMoveDistance() {
		return moveDistance;
	}
	
	@Override
	public int getToPosition() {
		return fromPosition + moveDistance.getValue();
	}
	
	@Override
	public boolean isClone() {
		return cloneGeneration > 0;
	}
	
	@Override
	public Move clone() {
		Move clone = new Move(this.playerID, this.fromPosition, this.moveDistance);
		
		clone.success = this.success;
		clone.takeOutCheker = this.takeOutCheker;
		clone.opponentHitChecker = this.opponentHitChecker;
		clone.cloneGeneration = cloneGeneration + 1;

		return clone;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fromPosition;
		result = prime * result
				+ ((moveDistance == null) ? 0 : moveDistance.hashCode());
		result = prime
				* result
				+ ((opponentHitChecker == null) ? 0 : opponentHitChecker
						.hashCode());
		result = prime * result
				+ ((playerID == null) ? 0 : playerID.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		result = prime * result
				+ ((takeOutCheker == null) ? 0 : takeOutCheker.hashCode());
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
		Move other = (Move) obj;
		if (fromPosition != other.fromPosition)
			return false;
		if (moveDistance == null) {
			if (other.moveDistance != null)
				return false;
		} else if (!moveDistance.equals(other.moveDistance))
			return false;
		if (opponentHitChecker == null) {
			if (other.opponentHitChecker != null)
				return false;
		} else if (!opponentHitChecker.equals(other.opponentHitChecker))
			return false;
		if (playerID != other.playerID)
			return false;
		if (success != other.success)
			return false;
		if (takeOutCheker == null) {
			if (other.takeOutCheker != null)
				return false;
		} else if (!takeOutCheker.equals(other.takeOutCheker))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Move from %d to %d (dice: %d), success: %b, take out: %b, hit: %b",
				getFromPosition(), getToPosition(), getMoveDistance().getValue(),
				isSuccess(), getTakeOutCheker() != null, getOpponentHitChecker() != null);
	}
}
