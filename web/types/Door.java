package web.types;

import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Location;
import web.types.Base.WebAction;
import web.walking.RSWalking;

/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 22:19
 */
public abstract class Door extends WebAction{

	private final int id;

	private final String action;


	private int attempts = 0;


	protected Door(final Tile a, final Tile b, final int id,final String action) {
		super(a, b);
		this.id = id;
		this.action = action;

	}

	@Override
	public boolean doActionAtoB() {
		if(RSWalking.WalkingUtil.canReach(super.getB(),false))
			return RSWalking.walkToTile(super.getB());
		Location door = Locations.getNearest(new Filter<Location>() {

			@Override
			public boolean accept(final Location l) {
				return l.getId() == id;
			}
		});
		if (door != null) {
			attempts++;
			if (!door.isOnScreen()) {
				RSWalking.walkToTile(super.getA());
			}
			if (door.isOnScreen()) {
				door.interact(action);
			}
			if (RSWalking.WalkingUtil.canReach(super.getB(),false)) {
				attempts = 0;
				return RSWalking.walkToTile(super.getB());
			}
			if (attempts < 5) {
				return doActionAtoB();
			}
			attempts = 0;
		}
		return false;
	}

	@Override
	public boolean doActionBtoA() {
		if(RSWalking.WalkingUtil.canReach(super.getA(),false))
			return RSWalking.walkToTile(super.getA());
		Location door = Locations.getNearest(new Filter<Location>() {

			@Override
			public boolean accept(final Location l) {
				return l.getId() == id;
			}
		});
		if (door != null) {
			attempts++;
			if (!door.isOnScreen()) {
				RSWalking.walkToTile(super.getB());
			}
			if (door.isOnScreen()) {
				door.interact(action);
			}
			if (RSWalking.WalkingUtil.canReach(super.getA(),false)) {
				attempts = 0;
				return RSWalking.walkToTile(super.getA());
			}
			if (attempts < 5) {
				return doActionAtoB();
			}
			attempts = 0;
		}
		return false;
	}

	@Override
	public double getWeight() {
		return 1;
	}


}
