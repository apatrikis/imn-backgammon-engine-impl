/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import java.util.Iterator;
import java.util.List;

import net.ichmags.backgammon.CommonEngine;
import net.ichmags.backgammon.exception.ExitException;
import net.ichmags.backgammon.exception.InvalidMoveException;
import net.ichmags.backgammon.game.ExitLevel;
import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.game.IMove;
import net.ichmags.backgammon.game.IPlay;
import net.ichmags.backgammon.game.pojo.GamePlayerConfig;
import net.ichmags.backgammon.interaction.ICommand;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.interaction.pojo.LoadDiceValuesCommand;
import net.ichmags.backgammon.interaction.pojo.MoveCommand;
import net.ichmags.backgammon.interaction.pojo.PrintBoardCommand;
import net.ichmags.backgammon.interaction.pojo.PrintDiceCommand;
import net.ichmags.backgammon.interaction.pojo.RulesCommand;
import net.ichmags.backgammon.interaction.pojo.TurnBoardViewCommand;
import net.ichmags.backgammon.interaction.pojo.UndoCommand;
import net.ichmags.backgammon.l10n.LocalizationManager;
import net.ichmags.backgammon.notification.INotification.Level;
import net.ichmags.backgammon.notification.impl.StatusEmitter;
import net.ichmags.backgammon.notification.pojo.BoardChangedNotification;
import net.ichmags.backgammon.notification.pojo.DicesChangedNotification;
import net.ichmags.backgammon.reflection.ClassByTypeFinder;
import net.ichmags.backgammon.setup.IAvailableDices;
import net.ichmags.backgammon.setup.IBoard;
import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDice.Status;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IDicesChoice;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.impl.AvailableDices;
import net.ichmags.backgammon.setup.impl.Board;
import net.ichmags.backgammon.setup.impl.DiceGenerator;
import net.ichmags.backgammon.setup.impl.Dices;
import net.ichmags.backgammon.setup.impl.DicesChoice;
import net.ichmags.backgammon.setup.impl.Positions;
import net.ichmags.backgammon.statistic.IGameStatistics;
import net.ichmags.backgammon.statistic.IPlayerStatistics;
import net.ichmags.backgammon.statistic.impl.GameStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link IGame} {@code interface}.
 * 
 * @author Anastasios Patrikis
 */
public abstract class Game implements IGame {
	
	private static Logger LOG = LoggerFactory.getLogger(Game.class);
	private static StatusEmitter SE = StatusEmitter.get();
	
	protected static final int RELATIVE_HIT_POS = 0;
	protected static final int RELATIVE_OUT_POS = 25;
	
	protected IPlayer player1;
	protected IPlayer player2;
	protected IBoard board;
	protected IDices dices;
	protected IGameStatistics statistics;

	protected GamePlayerConfig player1Config;
	protected GamePlayerConfig player2Config;
	
	private ICommandProvider commandProvider;
	
	/**
	 * Default constructor.
	 * 
	 * <b>Call {@link #initialize(IPlayer, IPlayer, ICommandProvider)} to make the instance usable.</b>
	 */
	public Game() {
	}
	
	@Override
	public IGame initialize(IPlayer player1, IPlayer player2, ICommandProvider commandProvider) {
		this.player1 = player1;
		this.player2 = player2;
		this.commandProvider = commandProvider;
		
		setupGamePlayerConfig();
		
		this.dices = new Dices();
		this.board = new Board(this);
		this.statistics = new GameStatistics();
		
		this.board.setInitialCheker(player1);
		this.board.setInitialCheker(player2);
		
		return this;
	}
	
	/**
	 * {@code abstract method}, so each implementation can provide the {@link IGame} specific
	 * positions.
	 * 
	 * @return the {@link List} of the {@link IChecker} {@link IPositions} of {@link IPlayer.ID#ONE} 
	 */
	protected abstract List<Integer> getCheckerPositionsPlayer1();
	
	/**
	 * {@code abstract method}, so each implementation can provide the {@link IGame} specific
	 * positions.
	 * 
	 * @return the {@link List} of the {@link IChecker} {@link IPositions} of {@link IPlayer.ID#TWO} 
	 */
	protected abstract List<Integer> getCheckerPositionsPlayer2();
	
	@Override
	public String getRules() {
		LocalizationManager.get().addBundle(this.getClass().getName());
		return LocalizationManager.get().get(this.getClass().getName());
	};
	
	@Override
	public IPlayer getPlayerOne() {
		return player1;
	}
	
	@Override
	public IPlayer getPlayerTwo() {
		return player2;
	}
	
	@Override
	public IPlayer play(IPlayer player)
	throws ExitException {
		SE.info("game.start", getName());
		
		if(player == null) {
			player = getStartingPlayer();
		}
		SE.info("game.player_starts", player.getName());
		
		try {
			while(nextMoves(player)) {
				player = CommonEngine.getOponent(player, player1, player2);
			}
		} catch (ExitException exit) {
			SE.info("game.player_give_up", player.getName());
			// set the premature winner
			player = CommonEngine.getOponent(player, player1, player2);
			
			if(exit.getExitLevel().equals(ExitLevel.MATCH)) {
				gameOver(player);
				throw exit;
			}
		}
		
		gameOver(player);
		return player;
	}
	
	@Override
	public GamePlayerConfig getGamePlayerConfig(IPlayer.ID playerID) {
		return (playerID == player1Config.getPlayerID())
				? player1Config : player2Config;
	}
	
	@Override
	public boolean isAllCheckersCollected(IPlayer player) {
		return (board.createPlayerView(player).get(RELATIVE_OUT_POS).getNrOfCheckers() == 15);
	}

	@Override
	public IGameStatistics getStatistics() {
		return statistics;
	}
	
	@Override
	public IMove moveChecker(IPlayer player, IBoard board, int fromPosition, IDice moveDistance) {
		IMove move = new Move(player.getID(), fromPosition, moveDistance);
		
		if( ! IDice.Status.AVAILABLE.equals(moveDistance.getStatus()) ) {
			SE.info("game.dice_already_used", moveDistance.getValue());
			return move;
		}
		
		int toPosition = fromPosition + moveDistance.getValue();
		IPositions playerPositions = board.createPlayerView(player);
		
		IPosition pos = playerPositions.get(fromPosition);
		if((fromPosition < RELATIVE_OUT_POS) && pos.hasCheckers() && pos.readTopChecker().getOwner().equals(player.getID())) {
			
			boolean isCollecting = isCollectionPhase(player, playerPositions);
			
			try {
				if(isCollecting && (toPosition > 24)) {
					if(isValidCollectionMove(player.getID(), playerPositions, fromPosition, toPosition)) {
						SE.emit(board.isClone() ? Level.TRACE : Level.INFO, "game.take_out");
						IChecker takeOutCheker = playerPositions.get(fromPosition).removeTopChecker();
						playerPositions.get(RELATIVE_OUT_POS).setTopChecker(takeOutCheker);
						moveDistance.setStatus(IDice.Status.USED);
						move.setTakeOutCheker(takeOutCheker);
						move.setSuccess(true);
					} else {
						SE.emit(board.isClone() ? Level.TRACE : Level.INFO, "game.no_takeout_must_move_higher_checker");
					}
				} else if((toPosition < 25) && isValidMoveTarget(player, board, fromPosition, toPosition)){
					SE.emit(board.isClone() ? Level.TRACE : Level.INFO, "game.move", fromPosition, toPosition);
					beforeMoveChecker(player, board, move);
					playerPositions.get(toPosition).setTopChecker(playerPositions.get(fromPosition).removeTopChecker());
					moveDistance.setStatus(IDice.Status.USED);
					move.setSuccess(true);
				} else {
					SE.emit(board.isClone() ? Level.TRACE : Level.INFO, "game.invalid_target_position");
				}
			} catch (Exception e) {
				LOG.error("Exception while moving checker", e);
			}
		} else {
			SE.emit(board.isClone() ? Level.TRACE : Level.INFO, "game.invalid_start_position");
		}
		
		return move;
	}
	
	@Override
	public void undoMoveCheker(IPlayer player, IBoard board, IMove moveToUndo)
	throws InvalidMoveException {
		if(!moveToUndo.isSuccess()) {
			throw new InvalidMoveException("The move was not successful an cannot be undone");			
		}
		
		IPositions playerPositions = board.createPlayerView(player);
		
		// just for convenience
		IChecker takeOutCheker = moveToUndo.getTakeOutCheker();
		IChecker movedChecker = null;
		IChecker opponentChecker = moveToUndo.getOpponentHitChecker();
		IPosition opponentPosition0 = null;
		
		// step 1: check
		if(takeOutCheker != null) {
			if(takeOutCheker.getOwner().equals(player.getID()) == false) {
				throw new InvalidMoveException("The move could not be undone: the checker taken out has a different owner.");
			}
			if( ! takeOutCheker.equals(playerPositions.get(RELATIVE_OUT_POS).readTopChecker()) ) {
				throw new InvalidMoveException("The move could not be undone: the checker taken does not match.");
			}
		} else {
			movedChecker = playerPositions.get(moveToUndo.getToPosition()).readTopChecker();
			if(movedChecker.getOwner().equals(player.getID()) == false) {
				throw new InvalidMoveException("The move could not be undone: the checker to move back has a different owner.");
			}
			
			if(opponentChecker != null) {
				if(this.hasPosition0() == false) {
					throw new InvalidMoveException("The move could not be undone: the game does not support hitting the opponent.");
				}
				
				IPlayer opponent = CommonEngine.getOponent(player, player1, player2);
				opponentPosition0 = board.createPlayerView(opponent).get(RELATIVE_HIT_POS);
				
				IChecker hitChecker = opponentPosition0.readTopChecker();
				if(hitChecker != opponentChecker) {
					throw new InvalidMoveException("The move could not be played: the hit token does not match.");
				}
			}
		}
		
		// step 2: execute
		if(takeOutCheker != null) {
			playerPositions.get(moveToUndo.getFromPosition()).setTopChecker(playerPositions.get(RELATIVE_OUT_POS).removeTopChecker());
			moveToUndo.setTakeOutCheker(null);
		} else {
			movedChecker = playerPositions.get(moveToUndo.getToPosition()).removeTopChecker();
			playerPositions.get(moveToUndo.getFromPosition()).setTopChecker(movedChecker);
			
			if(opponentChecker != null) {
				IChecker hitChecker = opponentPosition0.removeTopChecker();
				playerPositions.get(moveToUndo.getToPosition()).setTopChecker(hitChecker);
				moveToUndo.setOpponentHitChecker(null);
			}
		}
		moveToUndo.getMoveDistance().setStatus(IDice.Status.AVAILABLE);
		moveToUndo.setSuccess(false);
	}
	
	
	/**
	 * Find out which {@link IPlayer} should start the {@code IGame} by using the {@link IDices}.
	 * 
	 * @return the {@link IPlayer} who won the {@link IDices}.
	 */
	private IPlayer getStartingPlayer() {
		int p1dice=0;
		int p2dice=0;
		
		while(p1dice == p2dice) {
			dices.roll();
			p1dice = dices.get(0).getValue();
			p2dice = dices.get(1).getValue();
			SE.info("game.compare_random_start_dice", p1dice, p2dice);
		}
		
		return (p1dice > p2dice) ? player1 : player2;
	}
	
	/**
	 * Play the next moves by rolling the {@link IDices}.
	 * 
	 * @param currentPlayer The active {@link IPlayer}.
	 * @return {@code true} if there are more moves (the {@link IGame} is not over).
	 * @throws ExitException in case a {@link IPlayer} requested to end the {@code IGame}
	 * before it's normal end.
	 */
	protected boolean nextMoves(IPlayer currentPlayer)
	throws ExitException {
		boolean gameFinished = false; // default return value: game continues
		
		SE.info("game.player", currentPlayer);
		if(IPlayer.ID.TWO.equals(currentPlayer.getID())) {
			IPlayer opponent = CommonEngine.getOponent(currentPlayer, player1, player2);
			currentPlayer.setDisplayPreference(this, getInverseBoardView(opponent.getDisplayPreference(this)));
		}
		SE.emitNotification(new BoardChangedNotification(Level.INFO, currentPlayer, this, board));
		
		boolean playerContinues = false; // default assumption: after moving, the next player is the opponent
		do {
			dices.roll();
			SE.emitNotification(new DicesChangedNotification(Level.INFO, dices));
			this.getStatistics().addDices(dices);
			currentPlayer.getStatistics().addDices(dices);
			
			if(checkIfAnyMoveIsPossible(currentPlayer, dices)) {
				DicesChoice dicesChoice = findPlayableDices(currentPlayer, dices);
				
				if(dicesChoice.isSingleOption()) {
					if( ! dicesChoice.getOption1().allUsed()) {
						SE.emitNotification(new DicesChangedNotification(Level.INFO, dicesChoice.getOption1()));
					}
				}
				
				// play
				if(IPlayer.Type.LOCAL.equals(currentPlayer.getType())) {
					gameFinished = localPlayer(currentPlayer, dicesChoice);
				} else if(IPlayer.Type.COMPUTER.equals(currentPlayer.getType())){
					gameFinished = computerPlayer(currentPlayer, dicesChoice);
				} else {
					// TODO: implement a remote player
					throw new RuntimeException("Unexpected player type: " + currentPlayer.getType());
				}
				
				// check for "doubling boost" conditions
				if(!gameFinished && hasDoublingBoost(dices)) {
					SE.info("game.doubling_info");
					int nextDoubleValue = dices.get(3).getValue() + 1;
					DiceGenerator.get().load(new int[]{nextDoubleValue, nextDoubleValue});
					playerContinues = true;
				} else {
					playerContinues = false;
				}
			} else {
				SE.emitNotification(new DicesChangedNotification(Level.INFO, dices));
				playerContinues = false;
			}
		} while(playerContinues);
		
		return (!gameFinished);
	}
	
	/**
	 * Execute the play for a human {@link IPlayer}.
	 * This is a interaction where the move will be the most important one.
	 * 
	 * @param player the active human {@link IPlayer}.
	 * @param dicesChoice the {@link DicesChoice} to play. If this contains more then one item
	 * not all {@link IDices} can be played, in this case the {@link IPlayer} will have choose
	 * which ones to play.
	 * @return {@code true} it the game is finished and the {@link IPlayer} wins the {@code IGame}.
	 * @throws ExitException in case a {@link IPlayer} requested to end the {@code IGame} before
	 * it's normal end.
	 */
	private boolean localPlayer(IPlayer player, DicesChoice dicesChoice)
	throws ExitException {
		IMove lastMove = null;
		
		IDices selectedDices;
		if( ! dicesChoice.isSingleOption()) {
			// finally ... select the dices to play
			selectedDices = commandProvider.chooseDices(dicesChoice);
			SE.emitNotification(new DicesChangedNotification(Level.INFO, selectedDices));
		} else {
			selectedDices = dicesChoice.getOption1();
		}
		
		while(true) {
			ICommand cmd = null;
			try {
				cmd = commandProvider.getCommand();
			} catch (ExitException exit) {
				switch (exit.getExitLevel()) {
					case GAME:
					case MATCH:
						throw new ExitException(exit.getExitLevel());
				}
			}
			
			if(cmd instanceof PrintBoardCommand) {
				SE.emitNotification(new BoardChangedNotification(Level.INFO, player, this, board));
			} else if(cmd instanceof RulesCommand) {
				SE.info(getRules());
			} else if(cmd instanceof PrintDiceCommand) {
				SE.emitNotification(new DicesChangedNotification(Level.INFO, selectedDices));
			} else if(cmd instanceof TurnBoardViewCommand) {
				TurnBoardViewCommand turn = (TurnBoardViewCommand)cmd;
				player.setDisplayPreference(this, turn.getBoardView());
				SE.emitNotification(new BoardChangedNotification(Level.INFO, player, this, board));
			} else if(cmd instanceof LoadDiceValuesCommand) {
				LoadDiceValuesCommand change = (LoadDiceValuesCommand)cmd;
				DiceGenerator.get().load(change.getValues());
			} else if(cmd instanceof UndoCommand) {
				if(lastMove != null) {
					try {
						undoMoveCheker(player, board, lastMove);
						SE.emitNotification(new BoardChangedNotification(Level.INFO, player, this, board));
					} catch (InvalidMoveException e) {
						SE.info("game.undo_error", e.toString());
					}
				} else {
					SE.info("game.no_undo_move");
				}
			} else if(cmd instanceof MoveCommand) {
				MoveCommand move = (MoveCommand)cmd;
				
				IDice moveDice = selectedDices.getUnused(move.getDistance());
				if(moveDice != null) {
					IMove currentMove = moveChecker(player, board, move.getFrom(), moveDice);
					if(currentMove.isSuccess()) {
						SE.emitNotification(new BoardChangedNotification(Level.INFO, player, this, board));
						lastMove = currentMove;
						
						if(isAllCheckersCollected(player)) {
							return true;
						}
						
						if(selectedDices.allUsed()) {
							return false;
						} else {
							// show available dices to play next
							SE.emitNotification(new DicesChangedNotification(Level.INFO, selectedDices));
						}
					}
				} else {
					SE.info("game.cannot_find_dice_for_value", move.getDistance());
				}
			} else {
				SE.info("game.unsupported_command", cmd.getName());
			}
		}
	}
	
	/**
	 * Execute the play for a computer {@link IPlayer}.
	 * This involves to play the moves according to a strategy, which includes the
	 * analysis of the {@link IDices} and the {@link IBoard}.  
	 * 
	 * @param player the active human {@link IPlayer}.
	 * @param dicesChoice the {@link DicesChoice} to play. If this contains more then one item
	 * not all {@link IDices} can be played, in this case the {@link IPlayer} will have choose
	 * which ones to play.
	 * to choose which ones to play.
	 * @return {@code true} it the game is finished and the {@link IPlayer} wins the {@code IGame}.
	 * @throws ExitException in case a calculated move cannot be executed; this should never happen,
	 * and is an implementation error. As a consequence the {@link IGame} will be ended.
	 */
	private boolean computerPlayer(IPlayer player, IDicesChoice dicesChoice)
	throws ExitException {
		// TODO: move to IPLayer.getPlayExecutor() -> return IPlay
		IPlay computerPlayer = getPlayInstance();
		return computerPlayer.play(player, this, board, dicesChoice, SE);
	}
	
	/**
	 * Determines if a {@link IPlayer} is in the phase of collecting his {@link IChecker} from the
	 * {@link IBoard}. This is the case when no {@link IChecker} is on a {@link IPosition} up to 18.
	 * 
	 * @param player the {@link IPlayer} to check.
	 * @param positions the {@link IPositions} to check.
	 * @return {@code true} if the {@link IPlayer} is in collection phase.
	 */
	private boolean isCollectionPhase(IPlayer player, IPositions positions) {
		// fast logic: if a checker is already collected, the player is in the phase
		if((hasPosition0() == false) && (positions.get(RELATIVE_OUT_POS).getNrOfCheckers() > 0)) {
			return true;
		} else {
			boolean allCheckersInEndzone = true;
			
			for(int notCollectedPos = 0; notCollectedPos < 19; notCollectedPos++) {
				IPosition pos = positions.get(notCollectedPos);
				if(pos.hasCheckerOfPlayer(player.getID())) {
					allCheckersInEndzone = false;
					break;
				}
			}
			
			return allCheckersInEndzone;
		}
	}
	
	/**
	 * During collection, a {@link IChecker} can be taken out if:
	 * <dl>
	 * <dt>the {@link IChecker} {@link IPosition} matches the {@link IDice} value</dt>
	 * <dd>e. g. the {@link IDice} has the value of 4 and the {@link IChecker} is on {@link IPosition} 21</dd>
	 * <dt>the {@link IChecker} {@link IPosition} is less than the {@link IDice} value, and no {@link IDice}
	 * at a higher {@link IPosition} exists</dt>
	 * <dd>e. g. the {@link IDice} has the value of 4 and no {@link IChecker} exists on {@link IPosition} 21,
	 * also not on {@link IPosition} 20 (the <i>5</i> {@link IPosition}) or 19 (the <i>6</i> {@link IPosition});
	 * in this case the next lower positioned {@link IChecker} may be removed: 22 (the <i>3</i> {@link IPosition}),
	 * 23 (the <i>2</i> {@link IPosition}) or 24 (the <i>1</i> {@link IPosition}).</dd>
	 * </dl>
	 * 
	 * @param playerID the {@link IPlayer} to check.
	 * @param positions the {@link IPositions} to check.
	 * @param fromPosition the start {@link IPosition} of the move.
	 * @param toPosition the target {@link IPosition} of the move.
	 * @return {@code true} if the move is valid.
	 * @throws InvalidMoveException in case the {@code fromPosition} is not a valid collecting position.
	 */
	private boolean isValidCollectionMove(IPlayer.ID playerID, IPositions positions, int fromPosition, int toPosition)
	throws InvalidMoveException {
		boolean checkerMoveIsValid = true;
		
		if(fromPosition < 19) {
			throw new InvalidMoveException("Checker cannot be collected: call isCollectionPhase() first!");
		}
		
		if(toPosition == 25) {
			// the exact match position is valid 
			return true;
		} else {
			// if a higher position dice exists it must be played
			for(int pos = 19; pos < fromPosition; pos++) {
				IChecker topChecker = positions.get(pos).readTopChecker();
				if(topChecker != null && topChecker.getOwner().equals(playerID)) {
					checkerMoveIsValid = false;
					break;
				}
			}
		}
		
		return checkerMoveIsValid;
	}

	/**
	 * Finalizes the {@code IGame}, which is mainly updating the {@link IPlayerStatistics}.
	 * 
	 * @param winner the winner, who will receive the points as <i>vicrory</i>; the oponent
	 * will receive the <i>looser</i> points.
	 */
	private void gameOver(IPlayer winner) {
		int gameValue = getGameValue(winner);
		gameValue *= 1; // TODO: double dice
		SE.info((gameValue == 1) ? "game.end_1point" : "game.end_Npoint", gameValue, winner.getName());
		winner.getStatistics().addGameVictory(gameValue);
		CommonEngine.getOponent(winner, player1, player2).getStatistics().addGameDefeat(gameValue);
	}
	
	/**
	 * Evaluate the {@link IBoard} to determine how much points this {@code IGame} is worth.
	 * The points depend on the opponent's {@link IChecker} state.
	 * 
	 * @param player the winning {@link IPlayer}, he will receive the points.
	 * @return
	 * <ul>
	 * <li>1 point, in case the opponent was able to take out at least one {@link IChecker}</li>
	 * <li>2 points, in case all opponent {@link IChecker} are on the {@link IBoard}</li>
	 * </ul>
	 */
	protected int getGameValue(IPlayer player) {
		IPlayer opponentPlayer = CommonEngine.getOponent(player, player1, player2);
		return (board.createPlayerView(opponentPlayer).get(RELATIVE_OUT_POS).hasCheckers())
				? 1 : 2;
	}
	
	/**
	 * Check if the {@link IPlayer} can make any move with any {@link IDice}.
	 * 
	 * @param currentPlayer the {@link IPlayer} whose {@link IChecker} will be analyzed.
	 * @param dices the {@link IDices} to evaluate.
	 * @return {@code true} if the {@link IPlayer} can use a {@link IDice} to move a {@link IChecker}.
	 */
	protected boolean checkIfAnyMoveIsPossible(IPlayer currentPlayer, IDices dices) {
		SE.info("game.check_for_possible_move");
		
		// use clones, so the originals will not be changed
		IBoard testBoard = board.clone();
		IDices testDices = dices.clone();
		
		IPositions checkPositions;
		IPosition checkPosition = getMandatoryPosition(currentPlayer, testBoard);
		if(checkPosition == null) {
			checkPositions = testBoard.createPlayerView(currentPlayer);
		} else {
			checkPositions = new Positions().add(checkPosition);
		}
		
		IPositions playerPositions = testBoard.createPlayerView(currentPlayer);
		for(IPosition pos : checkPositions.get()) {
			if((pos.hasCheckers() == false) || (pos.readTopChecker().getOwner().equals(currentPlayer.getID()) == false)) {
				continue; // minimum condition not satisfied
			}
			
			for(IDice dice : testDices.get()) {
				int currentPos = pos.getIndexIn(playerPositions);
				
				SE.trace("game.test_move", currentPos, dice);
				if(moveChecker(currentPlayer, testBoard, currentPos, dice).isSuccess()) {
					return true;
				}
			}
		}
		
		dices.get().forEach(dice -> dice.setStatus(IDice.Status.BLOCKED));
		SE.info("game.no_moves", dices.toString());
		return false;
	}
	
	/**
	 * The {@link IPlayer} may not be able to use all {@link IDices} to move his {@link IChecker}
	 * on the {@link IBoard}. In this case the <i>not playable</i> {@link IDices} need to be identified.
	 * This is important to know when the {@link IPlayer} has finished moving and the opponent will
	 * be the next {@link IPlayer}.
	 * 
	 * The check is performed first for mandatory {@link IPositions}; if all could be satisfied and dices
	 * are still not played then all other {@link IPositions} are checked as well.
	 * 
	 * @param currentPlayer the {@link IPlayer} whose {@link IChecker} will be analyzed.
	 * @param dices the {@link IDices} to evaluate.
	 * @return a {@link List} of {link IDices} to indicate which {@link IDices} can be used to play.
	 * The return may be one of:
	 * <dl>
	 * <dt>all {@link IDices} can be used</dt>
	 * <dd>1 {@link List} item, all returned {@link IDices} are marked as playable,
	 * see {@link IDices#allUsed()}</dd>
	 * <dt>not all of the {@link IDices} could be played</dt>
	 * <dd>1 {@link List} item, some of the returned {@link IDices} are marked as playable,
	 * see {@link IDice#setStatus(Status)}</dd>
	 * <dt>not all of the {@link IDices} could be played, in different constellations</dt>
	 * <dd>2 {@link List} items, the {@link IPlayer} has to choose which version to use for playing.
	 * Example is the case when the {@link IDices} are {@code 6, 5} and the total {@link IPosition} of
	 * {@code 11} is blocked by the opponent. The {@link IPlayer} has to decide whether to play the
	 * {@code 5} or {@code 6} {@link IDice}.</dd>
	 * </dl>
	 */
	protected DicesChoice findPlayableDices(IPlayer currentPlayer, IDices dices) {
		SE.info("game.find_only_possible_moves");
		
		IDices markedDices = dices;
		DicesChoice foundDices = null;
		boolean checkComplete = false;
		
		IPosition mandatoryPosition = getMandatoryPosition(currentPlayer, board);
		if(mandatoryPosition != null) {
			int mandatoryCheckers = mandatoryPosition.getNrOfCheckers();
			foundDices = findDices(true, currentPlayer, markedDices);
			
			assert (foundDices.isSingleOption()); // for mandatory always 1 item
			markedDices = foundDices.getOption1();
			if(markedDices.usedCount() < mandatoryCheckers) {
				SE.info("game.cannot_play_all_mandatory_moves");
				checkComplete = true;
			} else if(markedDices.allUsed()) {
				// all moves are possible
				checkComplete = true;
			} else if(markedDices.usedCount() > mandatoryCheckers) {
				throw new RuntimeException("More moves then mandatory chekcer");
			}
		}
		
		if ( ! checkComplete) {
			foundDices = findDices(false, currentPlayer, markedDices);
		}
		
		// invert the dice marking before returning
		foundDices.convertStatusFromTestToPlay();
		return foundDices;
	}
	
	/**
	 * Reduce the {@link IDices} to the playable ones by marking them. A {@link IDice} that is already
	 * marked as use from a previous check will be ignored.
	 *  
	 * @param mandatoryPosition {@code true} if only the mandatory {@link IPosition} should be checked,
	 * else {@code false} is checking all {@link IPositions}.
	 * @param currentPlayer the {@link IPlayer} whose {@link IChecker} will be analyzed.
	 * @param dices the {@link IDices} to evaluate.
	 * @return the marked {@link IDices} that could be played.
	 * @see IDice#setIsUsedForMove(boolean)
	 */
	private DicesChoice findDices(boolean mandatoryPosition, IPlayer currentPlayer, IDices dices) {
		DicesChoice foundDices = new DicesChoice();
		
		// use clones, so the originals will not be changed
		IBoard testBoard = board.clone();
		IDices testDices = dices.clone();
		
		IPositions checkPositions = null; 
		if(mandatoryPosition) {
			checkPositions = new Positions().add(getMandatoryPosition(currentPlayer, testBoard));
		} else {
			checkPositions = testBoard.createPlayerView(currentPlayer);
			applyMandatoryMoves(currentPlayer, testBoard, testDices);
		}
		IAvailableDices playableDices = new AvailableDices().initialize(testDices, false);
		
		if(testDices.isDoubleDices()) {
			// dice order has no relevance: all dices have same value
			findDicesRecursion(checkPositions, currentPlayer, testBoard, playableDices);
			foundDices.addOption(testDices);
		} else {
			findDicesRecursion(checkPositions, currentPlayer, testBoard, playableDices);
			foundDices.addOption(testDices);
			
			if(!testDices.allUsed()) { // only if we could not use all dices we check the reverse dice order
				// re-initialize all before recursion
				testBoard = board.clone();
				testDices = dices.clone();
				
				if(mandatoryPosition) {
					checkPositions = new Positions().add(getMandatoryPosition(currentPlayer, testBoard));
				} else {
					checkPositions = testBoard.createPlayerView(currentPlayer);
					applyMandatoryMoves(currentPlayer, testBoard, testDices);
				}
				playableDices = new AvailableDices().initialize(testDices, true);
				
				findDicesRecursion(checkPositions, currentPlayer, testBoard, playableDices);
				foundDices.addOption(testDices);
			}
		}
		
		return foundDices;
	}
	
	/**
	 * If dices have been played earlier while checking for mandatory moves, they have to
	 * be applied before checking for non-mandatory moves.
	 * 
	 * @param currentPlayer the {@link IPlayer} whose {@link IChecker} will be analyzed.
	 * @param testBoard  the {@link IBoard} to use for playing the moves.
	 * @param testDices the {@link IDice} {@link List} to evaluate.
	 * @return {@code true} if {@link IDice} values haven been applied, {@code false} if no
	 * action was needed.
	 */
	private boolean applyMandatoryMoves(IPlayer currentPlayer, IBoard testBoard, IDices testDices) {
		if(testDices.usedCount() > 0) {
			DicesChoice dc = new DicesChoice();
			dc.addOption(testDices.clone());
			dc.convertStatusFromTestToPlay();
			IAvailableDices replayDices = new AvailableDices().initialize(dc.getOption1(), false);
			while(replayDices.hasMoreElements()) {
				if( ! moveChecker(currentPlayer, testBoard, RELATIVE_HIT_POS, replayDices.nextElement()).isSuccess()) {
					throw new RuntimeException("Error applying madatory moves");
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reduce the provided {@link List} of {@link IDice}s by using then next {@link IDice} for each
	 * recursion. A successfully used {@link IDice} is marked as used, see
	 * {@link IDice#setIsUsedForMove(boolean)}.
	 * The recursion ends if:
	 * <ul>
	 * <li>all provided {@link IDice}s could be used for moving</li>
	 * <li>all {@link IPositions} are traversed, but not all {@link IDice}s have been used</li>
	 * </ul>
	 * 
	 * @param checkPositions the {@link IPositions} to check; may be mandatory ones.
	 * @param currentPlayer the {@link IPlayer} whose {@link IChecker} will be analyzed.
	 * @param testBoard  the {@link IBoard} to use for playing the moves.
	 * @param testDices the {@link IDice} {@link List} to evaluate.
	 */
	private void findDicesRecursion(IPositions checkPositions, IPlayer currentPlayer, IBoard testBoard, IAvailableDices testDices) {
		IDice dice = testDices.nextElement();
		
		for(Iterator<IPosition> positionIterator = checkPositions.get().iterator(); positionIterator.hasNext(); ) {
			IPosition position = positionIterator.next();
			
			if((position.hasCheckers() == false) || (position.readTopChecker().getOwner().equals(currentPlayer.getID()) == false)) {
				continue; // minimum condition not satisfied
			}
			int currentPosition = position.getIndexIn(checkPositions);
			
			IMove move = moveChecker(currentPlayer, testBoard, currentPosition, dice);
			if(move.isSuccess()) {
				if( ! testDices.hasMoreElements()) {
					return; // we are done: all dices could be played
				} else {
					// recursion: play the next dice
					findDicesRecursion(checkPositions, currentPlayer, testBoard, testDices);
					return; // we are done: maybe not all dices have been played
				}
			}
		}
	}

	/**
	 * Get the mandatory {@link IPosition}. This depends on the {@link IGame} and the {@link IPlayer}.
	 * Even if the {@link IGame} defines such a position it is checked if it is empty; an empty
	 * {@link IPosition} does not need to be considered.
	 * 
	 * @param currentPlayer the {@link IPlayer} whose mandatory {@link IPosition} is requested.
	 * @param testBoard the {@link IBoard} to use for playing the moves.
	 * @return A found mandatory {@link IPosition}, or {@code null}.
	 */
	private IPosition getMandatoryPosition(IPlayer currentPlayer, IBoard testBoard) {
		IPosition mandatoryPosition = null;
		
		if(hasPosition0()) {
			IPosition position0 = testBoard.createPlayerView(currentPlayer).get(RELATIVE_HIT_POS);
			if(position0.hasCheckers()) {
				mandatoryPosition = position0;
			}
		}
		
		return mandatoryPosition;
	}
	
	/**
	 * Create a instance dynamically, as an implementing class is not
	 * in this package but somewhere on the {@code classpath}.
	 * 
	 * @return a new {@link IPlay} instance.
	 */
	private IPlay getPlayInstance() {
		return new ClassByTypeFinder<IPlay>(IPlay.class, true, "net\\.ichmags\\.backgammon\\..*").getInstance();
	}
}
