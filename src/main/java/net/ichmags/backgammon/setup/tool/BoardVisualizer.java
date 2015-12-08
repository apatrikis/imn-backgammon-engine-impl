/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.setup.tool;

import java.util.List;

import net.ichmags.backgammon.Common;
import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.setup.BoardView;
import net.ichmags.backgammon.setup.IChecker;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.IPosition;
import net.ichmags.backgammon.setup.IPositions;
import net.ichmags.backgammon.setup.PositionColor;
import net.ichmags.backgammon.setup.impl.Board;
import net.ichmags.backgammon.setup.impl.Checker;
import net.ichmags.backgammon.setup.impl.Player;
import net.ichmags.backgammon.setup.impl.Position;
import net.ichmags.backgammon.setup.impl.Positions;

/**
 * The {@code BoardVisualizer} creates a {@link System#out} suitable version
 * of the {@link Board} {@link Positions} and {@link Checker} for the current
 * {@link Player} and {@link IGame}.
 * 
 * @author Anastasios Patrikis
 */
public class BoardVisualizer {

	private IPlayer currentPlayer;
	private IGame game;
	private Board board;
	
	/**
	 * Constructor.
	 * 
	 * @param currentPlayer the current {@link Player}; if {@code null}, the {@link BoardView} will
	 * default to {@link BoardView#ABSOLUTE}.
	 * @param game the current {@link IGame}.
	 * @param board the current {@link Board}.
	 */
	public BoardVisualizer(IPlayer currentPlayer, IGame game, Board board) {
		this.currentPlayer = currentPlayer;
		this.game = game;
		this.board = board;
	}
	
	@Override
	public String toString() {
		BoardView preferedPlayerView;
		IPositions playerPositions;
		if(currentPlayer != null) {
			preferedPlayerView = currentPlayer.getDisplayPreference(game);
			playerPositions = board.createMappedView(currentPlayer.getBoardView(), preferedPlayerView);
		} else {
			preferedPlayerView = BoardView.START_TOP_RIGHT;
			playerPositions = board.createMappedView(preferedPlayerView, preferedPlayerView);
		}
		
		return dumpBoard(playerPositions, preferedPlayerView);
	}
	
	/**
	 * Main dump method, that invokes all minor dumps to generate the whole output.
	 * 
	 * @param playerPositions The {@link Positions} to dump; remember that {@link Positions} are
	 * depending on the {@link IGame} and {@link Player}.
	 * @param outputView the {@link BoardView} to use when dumping {@link Positions}.
	 * @return the {@link String} representation of the {@link Board}.
	 */
	private String dumpBoard(IPositions playerPositions, BoardView outputView) {
		StringBuilder dump = new StringBuilder();
		
		dumpHitChecker(game.getPlayerTwo(), dump);
		dumpBoardPositionLine(outputView, dump, 0, 12);
		dumpBoardColorLine(playerPositions, dump, 0, 12);
		dumpCheckerBaseLine(playerPositions, dump, 0, 12);
		dumpCheckerTopLine(playerPositions, dump, 0, 12);
		dumpSpacerLines(dump);
		dumpCheckerTopLine(playerPositions, dump, 13, 25);
		dumpCheckerBaseLine(playerPositions, dump, 13, 25);
		dumpBoardColorLine(playerPositions, dump, 13, 25);
		dumpBoardPositionLine(outputView, dump, 13, 25);
		dumpHitChecker(game.getPlayerOne(), dump);
		
		return dump.toString();
	}
	
	/**
	 * If available, dump the kicked {@link IChecker} of the player.
	 * 
	 * @param player the {@link IPlayer} to dump the kicked {@link IChecker}.
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @param the generated output.
	 */
	private void dumpHitChecker(IPlayer player, StringBuilder sb) {
		if(game.hasPosition0()) {
			IPosition position0 = board.createPlayerView(player).get(0);
			if(position0.getNrOfCheckers() > 0) {
				sb.append("          ->->-> Hit ").append(player.getName()).append(": ").append(position0).append(Common.NEWLINE);
			}
		}
	}
	
	/**
	 * Dump the {@link Position} numbers, depending on the {@link BoardView}.
	 * 
	 * @param view the {@link BoardView} to generate.
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @param from the starting {@link Position} for output generation.
	 * @param to the ending {@link Position} for output generation.
	 * @return the generated output.
	 */
	private StringBuilder dumpBoardPositionLine(BoardView view, StringBuilder sb, int from, int to) {
		List<Integer> labels = board.getAbsolutePositions(view);
		
		sb.append("|");
		int halfTime = from + ((to-from)/2) - 1;
		for(int index = from; index < to; index++) {
			sb.append(" ").append(String.format("%3d", labels.get(index))).append(" ");
			if(index == halfTime) { sb.append("||"); }
		}
		
		return sb.append("|").append(Common.NEWLINE);
	}
	
	/**
	 * Dump the {@link PositionColor} of the {@link Positions}.
	 * 
	 * @param positions the {@link Positions} for generating the output.
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @param from the starting {@link Position} for output generation.
	 * @param to the ending {@link Position} for output generation.
	 * @return the generated output.
	 */
	private StringBuilder dumpBoardColorLine(IPositions positions, StringBuilder sb, int from, int to) {
		sb.append("|");
		int halfTime = from + ((to-from)/2) - 1;
		for(int index = from; index < to; index++) {
			IPosition pos = positions.get(index);
			sb.append("-").append(pos.getColor()).append(pos.getColor()).append(pos.getColor()).append("-");
			if(index == halfTime) { sb.append("||"); }
		}
		sb.append("|");
		sb.append("-out-");
		
		return sb.append(Common.NEWLINE);
	}
	
	/**
	 * Dump the first {@link Checker} on a {@link Position}.
	 * 
	 * @param positions the {@link Positions} for generating the output.
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @param from the starting {@link Position} for output generation.
	 * @param to the ending {@link Position} for output generation.
	 * @return the generated output.
	 */
	private StringBuilder dumpCheckerBaseLine(IPositions positions, StringBuilder sb, int from, int to) {
		sb.append("|");
		int halfTime = from + ((to-from)/2) - 1;
		for(int index = from; index <= to; index++) {
			IPosition pos = positions.get(index);
			IChecker baseChecker = pos.readChecker(0);
			if(index == to) {
				sb.append("|");
			}
			if(baseChecker != null) {
				sb.append("  1").append(baseChecker).append(" ");
			} else {
				sb.append("     ");
			}
			if(index == halfTime) { sb.append("||"); }
		}
		
		return sb.append(Common.NEWLINE);
	}
	
	/**
	 * Dump all but the first {@link Checker} on a {@link Position}, reduced to a singele {@link Checker} and a count.
	 * This can be done because all {@link Checker} above the first one will be owned by the same {@link Player}.
	 * 
	 * @param positions the {@link Positions} for generating the output.
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @param from the starting {@link Position} for output generation.
	 * @param to the ending {@link Position} for output generation.
	 * @return the generated output.
	 */
	private StringBuilder dumpCheckerTopLine(IPositions positions, StringBuilder sb, int from, int to) {
		sb.append("|");
		int halfTime = from + ((to-from)/2) - 1;
		for(int index = from; index <= to; index++) {
			IPosition pos = positions.get(index);
			IChecker baseChecker = pos.readChecker(1);
			if(index == to) {
				sb.append("|");
			}
			if(baseChecker != null) {
				sb.append(" ").append(String.format("%2d", pos.getNrOfCheckers()-1)).append(baseChecker).append(" ");
			} else {
				sb.append("     ");
			}
			if(index == halfTime) { sb.append("||"); }
		}

		return sb.append(Common.NEWLINE);
	}
	
	/**
	 * Dump a spacer to divide the first 12 {@link Positions} from the second 12.
	 * 
	 * @param sb the {@link StringBuilder} to write the generated output.
	 * @return the spacer.
	 */
	private StringBuilder dumpSpacerLines(StringBuilder sb) {
		sb.append("|                              ||                              |").append(Common.NEWLINE);
		sb.append("|------------------------------||------------------------------|").append(Common.NEWLINE);
		sb.append("|                              ||                              |").append(Common.NEWLINE);
		return sb;
	}
}
