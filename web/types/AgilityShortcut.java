package web.types;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Location;
import web.types.Base.WebAction;

/**
 * Author: Tom
 * Date: 03/04/12
 * Time: 01:23
 */
public class AgilityShortcut extends WebAction {

	private final int AGILITY = 0;

	private final int levelRequirement;

	private final int idA;
	private final int idB;

	private final String actionA;
	private final String actionB;

	private int attempts = 0;

	public AgilityShortcut(final Tile a, final Tile b, final int levelRequirement, final int id, final String action) {
		super(a, b);
		this.levelRequirement = levelRequirement;
		this.idA = id;
		this.idB = id;
		this.actionA = action;
		this.actionB = action;
	}

	public AgilityShortcut(final Tile a, final Tile b, final int levelRequirement, final int idA, final int idB, final String actionA, final String actionB) {
		super(a, b);
		this.levelRequirement = levelRequirement;
		this.idA = idA;
		this.idB = idB;
		this.actionA = actionA;
		this.actionB = actionB;
	}

	@Override
	public boolean canDoAction() {
		return Skills.getLevels()[AGILITY] >= levelRequirement;
	}

	@Override
	public boolean doActionAtoB() {
		Location shortcut = Locations.getNearest(new Filter<Location>() {

			@Override
			public boolean accept(final Location l) {
				return l.getId() == idA;
			}
		});
		if (shortcut != null) {
			attempts++;
			if (!shortcut.isOnScreen()) {
				//Walk to shortcut
			}
			if (shortcut.isOnScreen()) {
				shortcut.interact(actionA);
			}
			if (Players.getLocal().getPosition().equals(super.getB())) {
				attempts = 0;
				return true;
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
		Location shortcut = Locations.getNearest(new Filter<Location>() {

			@Override
			public boolean accept(final Location l) {
				return l.getId() == idB;
			}
		});
		if (shortcut != null) {
			attempts++;
			if (!shortcut.isOnScreen()) {
				//Walk to shortcut
			}
			if (shortcut.isOnScreen()) {
				shortcut.interact(actionB);
			}
			if (Players.getLocal().getPosition().equals(super.getA())) {
				attempts = 0;
				return true;
			}
			if (attempts < 5) {
				return doActionBtoA();
			}
			attempts = 0;
		}
		return false;
	}

	@Override
	public double getWeight() {
		return Calculations.distance(super.getA(), super.getB());
	}
}
