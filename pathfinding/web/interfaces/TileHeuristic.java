package pathfinding.web.interfaces;

import org.powerbot.game.api.methods.Calculations;
import pathfinding.astar.interfaces.Heuristic;
import pathfinding.web.components.base.WebNode;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 00:09
 */
public class TileHeuristic implements Heuristic<WebNode>{
	@Override
	public double getCost(final WebNode source, final WebNode target) {
		return Calculations.distance(source.getTile(),target.getTile());
	}
}
