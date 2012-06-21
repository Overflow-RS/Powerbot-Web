package pathfinding.web.components.actions.teleports;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 16:08
 */

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;
import pathfinding.web.components.actions.WebAction;


public class LodestoneAction extends WebAction {

	public static boolean canUse = true;
	public static double lodestoneCost = 50;

	private static final int LODESTONE_INTERFACE = 1092;
	private static final int TELEPORT_ID         = 16385;
	private final int CHILD_ID;

	private static Widget getMainInterface() {
		return Widgets.get(LODESTONE_INTERFACE);
	}

	public LodestoneAction(final String name,final Tile target, final int childId) {
		super(name,null, target);
		this.CHILD_ID = childId;
	}

	@Override
	public final boolean canDoAction() {
		return canUse;
	}

	@Override
	public final boolean doAction() {
		ActiveScript script = Context.get().getActiveScript();
		int count = 0;
		Tile current = Players.getLocal().getLocation();
		while (script.isRunning() && count++ < 10) {
			if (Calculations.distanceTo(current) < 50) {
				Widget main = getMainInterface();
				if (main == null || !main.validate()) {
					if (!openLodestoneInterface()) {
						continue;
					}
				}
				WidgetChild child = main.getChild(CHILD_ID);
				if (child != null && child.validate()) {
					child.interact("Teleport");
					if (sleepUntil(new HasTeleported(), new TeleportBreak(), 20000)) {
						return true;
					}
				}
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getCost() {
		return lodestoneCost;
	}

	private static boolean openLodestoneInterface() {
		if (!Tabs.MAGIC.isOpen()) {
			Tabs.MAGIC.open();
		}
		WidgetChild ht = Widgets.get(192, 24);
		WidgetChild scrollbar = Widgets.get(192, 94);
		if (ht == null || !ht.validate()) {
			return false;
		}
		Widgets.scroll(ht, scrollbar);
		ht.interact("Cast");
		return sleepUntil(interfaceCondition, 5000);
	}

	private static boolean sleepUntil(Condition condition, long timeOut) {
		Timer failsafe = new Timer(timeOut);
		while (failsafe.isRunning()) {
			if (condition.conditionMet()) {
				return true;
			}
			Time.sleep((int) (timeOut / 10));
		}
		return condition.conditionMet();
	}

	private static boolean sleepUntil(Condition condition, Condition breakCondition, long timeOut) {
		Timer failsafe = new Timer(timeOut);
		while (failsafe.isRunning()) {
			if (breakCondition.conditionMet()) {
				return false;
			}
			if (condition.conditionMet()) {
				return true;
			}
			Time.sleep((int) (timeOut / 10));
		}
		return condition.conditionMet();
	}

	private static Condition interfaceCondition = new Condition() {

		@Override
		public boolean conditionMet() {
			Widget wc = getMainInterface();
			return wc != null && wc.validate();
		}
	};

	private static class HasTeleported implements Condition {

		Tile current = null;

		@Override
		public boolean conditionMet() {
			if (current == null) {
				current = Players.getLocal().getLocation();
				return false;
			}
			return Calculations.distanceTo(current) > 30;
		}
	};

	private static class TeleportBreak implements Condition {

		int i = 0;

		@Override
		public boolean conditionMet() {
			if (Players.getLocal().getAnimation() == TELEPORT_ID) {
				i = 0;
			} else {
				i++;
			}
			return i > 5;
		}

	};

	public interface Condition {
		public boolean conditionMet();
	}

}
