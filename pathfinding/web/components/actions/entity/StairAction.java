package pathfinding.web.components.actions.entity;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 00:05
 */
public class StairAction extends WebAction {

	private final String action;
	private final int[]  stairIds;

	public StairAction(final String name, final Tile source, final Tile target, final String action, final int... stairIds) {
		super(name, source, target);
		this.action = action;
		this.stairIds = stairIds;
	}

	@Override
	public boolean canDoAction() {
		return true;
	}

	@Override
	public boolean doAction() {
		for (int i = 0; i < 5; i++) {
			SceneObject sc = SceneEntities.getNearest(stairIds);
			if (sc != null && clickSceneEntity(sc, action, null)) {
				for (int x = 0; x < 10; x++) {
					if (Players.getLocal().getLocation().getPlane()==target.getPlane()&& Calculations.distanceTo(target)<10) {
						return true;
					}
					Time.sleep(500,1000);
				}
			}
		}
		return false;
	}

	@Override
	public double getCost() {
		return 2;
	}
}
