package pathfinding.web.components.actions.basic;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:54
 */
public class WalkAction extends WebAction {

	public WalkAction(final Tile source, final Tile target) {
		super("Walk",source, target);
	}

	@Override
	public boolean canDoAction() {
		return true;
	}

	@Override
	public boolean doAction() {
		for (int i = 0; i < 5; i++) {
			if (walkToTile(target)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getCost() {
		return Calculations.distance(source, target);
	}
}
