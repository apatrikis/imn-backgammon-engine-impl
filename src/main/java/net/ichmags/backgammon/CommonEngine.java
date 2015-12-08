/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon;

import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.Player;

/**
 * A helper class to easily share common definitions and functionalities.
 * 
 * @author Anastasios Patrikis
 */
public class CommonEngine {

	/**
	 * Get the {@link Player} identified by his {@link IPlayer.ID}.
	 * 
	 * @param currentID The {@link IPlayer.ID} to use for lookup.
	 * @param player1 The candidate number <i>1</i>.
	 * @param player2 The candidate number <i>2</i>.
	 * @return The {@link Player}.
	 */
	public static IPlayer getPlayer(IPlayer.ID currentID, IPlayer player1, IPlayer player2) {
		return (player1.getID().equals(currentID))
				? player1 : player2;
	}
	
	/**
	 * Get the opponent of a {@link Player}.
	 * 
	 * @param current The current {@link Player}.
	 * @param player1 The candidate number <i>1</i>.
	 * @param player2 The candidate number <i>2</i>.
	 * @return The opponent {@link Player}.
	 */
	public static IPlayer getOponent(IPlayer current, IPlayer player1, IPlayer player2) {
		return (player1.equals(current))
				? player2 : player1;
	}
	
	/**
	 * Get the opponent of a {@link Player}.
	 * 
	 * @param currentID The current {@link IPlayer.ID}.
	 * @param player1 The candidate number <i>1</i>.
	 * @param player2 The candidate number <i>2</i>.
	 * @return The opponent {@link Player}.
	 */
	public static IPlayer getOponent(IPlayer.ID currentID, IPlayer player1, IPlayer player2) {
		return (player1.getID().equals(currentID))
				? player2 : player1;
	}
}
