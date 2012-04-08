package web.resources.pathfinding;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import web.types.Base.WebComponent;
import web.walking.RSWalking;

import java.util.ArrayList;

/**
 * Author: Tom
 * Date: 03/04/12
 * Time: 00:35
 */
public class WebPath {

	private final Tile            endTile;
	public        ArrayList<Edge> steps;

	private Edge current = null;

	public WebPath(Tile endTile) {
		this.endTile = endTile;
		steps = new ArrayList<Edge>();
	}

	public Tile getEndTile() {
		return endTile;
	}

	public int getLength() {
		return steps.size();
	}


	public Edge getStep(int index) {
		return steps.get(index);
	}

	public void appendStep(Edge node) {
		steps.add(node);
	}

	public void prependStep(Edge node) {
		steps.add(0, node);
	}


	public boolean contains(Edge node) {
		return steps.contains(node);
	}

	public double getTotalWeight() {
		double temp = 0;
		for (Edge e : steps) {
			temp += e.getWeight();
		}
		return temp;
	}

	public Edge getCurrent() {
		return current;
	}

	public boolean traverseWebPath() {
		for (Edge e : steps) {
			System.out.println(e);
			Time.sleep(Random.nextInt(300, 800));
			WebComponent component = e.getComponent();
			if (e.isaToB()) {
				if (component.hasActionAtoB()) {
					if (component.doActionAtoB()) {
						continue;
					}
					return false;
				} else {
					walkToTile(component.getB());
				}
			} else {
				if (component.hasActionBtoA()) {
					if (component.doActionBtoA()) {
						continue;
					}
					return false;
				} else {
					walkToTile(component.getA());
				}
			}
		}
		if (Calculations.distance(Players.getLocal().getPosition(), getEndTile()) > 10) {
			walkToTile(getEndTile());
		}
		return Calculations.distance(Players.getLocal().getPosition(), getEndTile()) < 10;
	}


	public boolean walkToTile(final Tile tile) {
		return RSWalking.walkToTile(tile);
	}

}
