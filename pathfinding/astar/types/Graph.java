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
	private final String name;

	private boolean      debugWeb = false;

	public Graph(ActiveScript script, String name) {
		this.script = script;
		this.name = name;
	}

	protected ArrayList<Node> nodes = new ArrayList<Node>();

	protected Heuristic heuristic;

	public Heuristic getHeuristic() {

		return heuristic;
	}

	public boolean isDebugOn() {
		return debugWeb;
	}

	public void setDebug(final boolean debugWeb) {
		this.debugWeb = debugWeb;
	}

	public void debugMessage(String string) {
		if (debugWeb) {
			StringBuilder message = new StringBuilder("["+name+"] ");
			message.append(string);
			if (script != null) {
				script.log.info(message.toString());
			} else {
				System.out.println(message);
			}
		}
	}

	public void addNode(Node node) {
		nodes.add(node);
	}

	public void setHeuristic(Heuristic h) {
		this.heuristic = h;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void resetNodes(Node target) {
		for (Node n : nodes) {
			n.reset(target);
		}
	}
}
