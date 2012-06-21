package pathfinding.web.components.base;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.map.LocalPath;
import org.powerbot.game.bot.Context;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 14:59
 */
public class WebPath<T extends WebEdge> extends LinkedList<T> {

	public final Tile endTile;
	public String status = "";

	public WebPath(final Tile endTile) {
		this.endTile = endTile;
	}

	public boolean traverse() {
		Iterator<T> a = this.iterator();
		while (a.hasNext()) {
			T next = a.next();
			status = next.toString();
			LocalPath p = Walking.findPath(endTile);
			if (p != null && p.validate()) {
				break;
			}
			if (!next.doAction()) {
				return false;
			}
		}
		return walkPath(endTile);
	}

	public double getCost() {
		double cur = 0;
		Iterator<T> a = this.iterator();
		while (a.hasNext()) {
			cur += a.next().getCost();
		}
		return cur;
	}

	public boolean walkPath(Tile target) {
		Timer failSafe = new Timer(30000);
		ActiveScript script = Context.get().getActiveScript();
		if (script == null) {
			return false;
		}
		LocalPath p = Walking.findPath(target);
		while (script.isRunning() && failSafe.isRunning()) {
			if (!Players.getLocal().isMoving() || (Calculations.distanceTo(Walking.getDestination()) < 8 && Calculations.distance(Walking.getDestination(),
			                                                                                                                      target) > 3)) {
				if (p == null) {
					Walking.walk(target);
					p = Walking.findPath(target);
				} else {
					p.traverse();
				}
			}
			Time.sleep(200, 500);
			if (Calculations.distanceTo(target) < 5) {

				return true;
			}
		}
		return false;
	}

	public String getStatus() {
		return status;
	}
}
