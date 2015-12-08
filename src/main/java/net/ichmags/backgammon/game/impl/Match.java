/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import java.util.ArrayList;
import java.util.List;

import net.ichmags.backgammon.CommonEngine;
import net.ichmags.backgammon.exception.ExitException;
import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.game.IMatch;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.l10n.LocalizationManager;
import net.ichmags.backgammon.notification.INotificationConsumer;
import net.ichmags.backgammon.notification.impl.StatusEmitter;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.DiceGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link IMatch} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public class Match implements IMatch {
	
	private static Logger LOG = LoggerFactory.getLogger(Match.class);
	
	/**
	 * Constructor.
	 */
	public Match() {
		LocalizationManager.get().addBundle("net.ichmags.backgammon.l10n.backgammon");
	}
	
	@Override
	public void start(IPlayer player1, IPlayer player2,
			int nrOfWins, List<Class<IGame>> gameSequence, boolean winnerStartsNextGame,
			ICommandProvider commandProvider, INotificationConsumer notificationConsumer) {
		
		StatusEmitter se = StatusEmitter.get();
		se.addConsumer(notificationConsumer);
		se.info(winnerStartsNextGame ? "match.winner_start" : "match.random_start", nrOfWins, player1.getName(), player2.getName(), DiceGenerator.get().getSeed());
		
		List<IGame> playedGames = new ArrayList<>();
//		IPlayer currentPlayer = null;
//		DiceGenerator.get().load(new int[]{5, 1, 1, 1, 6, 2});
		IPlayer currentPlayer = player1; // TODO: remove after testing
		for(int nrOfGame = 0; ; ) {
			IGame game = null;
			try {
				game = gameSequence.get(nrOfGame % gameSequence.size()).newInstance();
			} catch (Exception e) {
				LOG.error("Error invoking game constructor", e);
				return;
			}
			game.initialize(player1, player2, commandProvider);
			
			se.info("match.game_number", ++nrOfGame);
			playedGames.add(game);
			try {
				currentPlayer = game.play((winnerStartsNextGame) ? currentPlayer : null);
			} catch (ExitException e) {
				// set the premature winner
				currentPlayer = CommonEngine.getOponent(currentPlayer, player1, player2);
				break;
			}
			
			if(currentPlayer.getStatistics().getGameVictories() >= nrOfWins) {
				break;
			}
		}
		se.info("match.end", currentPlayer.getName());
		currentPlayer.getStatistics().addMatchVictory();
		CommonEngine.getOponent(currentPlayer, player1, player2).getStatistics().addMatchDefeat();
		
		printStatistics(player1, player2, playedGames);
	}
	
	/**
	 * Print the collected statistics for the {@link IMatch}, the {@link IGame}s and the
	 * {@link IPlayer}s.
	 * 
	 * @param player1 the {@link IPlayer} #1.
	 * @param player2 the {@link IPlayer} #2.
	 * @param playedGames the {@link List} of the played {@link IGame}s.
	 */
	private void printStatistics(IPlayer player1, IPlayer player2, List<IGame> playedGames) {
		StatusEmitter se = StatusEmitter.get();
		
		for(int pos = 0; pos < playedGames.size(); pos++) {
			se.info("match.game_statistics", pos+1);
			se.info(playedGames.get(pos).getStatistics().toString());
		}
		
		se.info("match.payer_statistics", player1.getName());
		se.info(player1.getStatistics().toString());
		se.info("match.payer_statistics", player2.getName());
		se.info(player2.getStatistics().toString());
	}
}
