package pathfinding.web.components.actions;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.map.Path;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.bot.Context;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:25
 */
public abstract class WebAction {

	protected WebAction(final String name, final Tile source, final Tile target) {
		this.name = name;
		this.source = source;
		this.target = target;
	}

	protected final Tile   source;
	protected final Tile   target;
	protected final String name;

	public abstract boolean canDoAction();

	public abstract boolean doAction();

	public abstract double getCost();

	public Tile getSource() {
		return source;
	}

	public Tile getTarget() {
		return target;
	}

	public boolean clickSceneEntity(SceneObject io, String action, String name) {
		Timer failSafe = new Timer(10000);
		ActiveScript script = Context.get().getActiveScript();
		if (script == null) {
			return false;
		}
		while (script.isRunning() && failSafe.isRunning()) {
			if (io.isOnScreen()) {
				if (io.interact(action, name)) {
					for (int i = 0; i < 10; i++) {
						if (Players.getLocal().getAnimation() == -1) {
							break;
						}
						Time.sleep(100, 300);
					}
					return true;
				}
			} else {
				if (Players.getLocal().isMoving()) {
					Time.sleep(100, 300);
				} else {
					Walking.walk(io);
					for (int i = 0; i < 10; i++) {
						if (Players.getLocal().isMoving()) {
							break;
						}
						Time.sleep(100, 300);
					}
				}
			}
		}
		return false;
	}

	public boolean walkToTile(Tile target) {
		Timer failSafe = new Timer(30000);
		ActiveScript script = Context.get().getActiveScript();
		if (script == null) {
			return false;
		}
		Path p = Walking.findPath(target);
		while (script.isRunning() && failSafe.isRunning()) {
			if (!Players.getLocal().isMoving() || (Calculations.distanceTo(Walking.getDestination()) < 8 && Calculations.distance(Walking.getDestination(),
			                                                                                                                      target) > 5)) {
				if (p == null || !p.validate()) {
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

	@Override
	public String toString() {
		return name;
	}
}
