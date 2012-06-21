package pathfinding.astar.types;

import org.powerbot.game.api.ActiveScript;
import pathfinding.astar.interfaces.Heuristic;

import java.util.ArrayList;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 22:28
 */
public abstract class Graph {

	private final ActiveScript script;
	private final String       name;

	private boolean debugWeb = false;

	public Graph(final ActiveScript script, final String name) {
		this.script = script;
		this.name = name;
	}

	private final ArrayList<Node> nodes = new ArrayList<Node>();

	private Heuristic heuristic;

	public final Heuristic getHeuristic() {
		return heuristic;
	}

	public final boolean isDebugOn() {
		return debugWeb;
	}

	public final void setDebug(final boolean debugWeb) {
		this.debugWeb = debugWeb;
	}

	public final void debugMessage(final String string) {
		if (debugWeb) {
			StringBuilder message = new StringBuilder("[" + name + "] ");
			message.append(string);
			if (script != null) {
				script.log.info(message.toString());
			} else {
				System.out.println(message);
			}
		}
	}

	public final void addNode(final Node node) {
		nodes.add(node);
	}

	public final void setHeuristic(Heuristic h) {
		this.heuristic = h;
	}

	public final ArrayList<Node> getNodes() {
		return nodes;
	}

	public final void resetNodes(final Node target) {
		for (Node n : nodes) {
			n.reset(target);
		}
	}
}
