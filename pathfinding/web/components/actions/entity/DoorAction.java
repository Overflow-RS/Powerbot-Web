package pathfinding.web.components.actions.entity;

import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:39
 */
public class DoorAction extends WebAction {
	private final int[]  doorId;

	public DoorAction(final String name,final Tile source, final Tile target, final int... doorId) {
		super(name,source,target);
		this.doorId = doorId;
	}

	@Override
	public boolean canDoAction() {
		return true;
	}

	@Override
	public boolean doAction() {
		if (target.canReach()) {
			return true;
		}
		for (int i = 0; i < 5; i++) {
			SceneObject sc = SceneEntities.getNearest(doorId);
			if (sc != null && clickSceneEntity(sc, "Open", null) && target.canReach()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getCost() {
		return 1;
	}
}
