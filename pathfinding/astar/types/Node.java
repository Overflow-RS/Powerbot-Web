package pathfinding.astar.types;

import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 22:25
 */
public abstract class Node implements Comparable<Node> {

	private final Graph container;
	private final LinkedList<Edge> edges = new LinkedList<Edge>();

	private Node parent;

	private double heuristicCost;

	public Node(final Graph container) {
		this.container = container;
	}

	public final Graph getContainer() {
		return container;
	}

	public final LinkedList<Edge> getEdges() {
		return edges;
	}

	public final Edge getEdge(final Node node) {
		for (Edge e : edges) {
			if (e.getTarget().equals(node)) {
				return e;
			}
		}
		return null;
	}

	public final void addEdge(final Edge e) {
		edges.add(e);
	}

	public final void reset(final Node target) {
		heuristicCost = container.getHeuristic().getCost(this, target);
		parent = null;
	}

	public final double getCurrentCost() {
		return parent == null ? 0 : parent.getCurrentCost();
	}

	public final double getHeuristicCost() {
		return heuristicCost;
	}

	public final double getCost() {
		return getHeuristicCost() + getCurrentCost();
	}

	public final Node getParent() {
		return parent;
	}

	public final void setParent(final Edge edge) {
		this.parent = edge.getSource();
	}

	@Override
	public final int compareTo(final Node o) {
		double dist = getCost() - o.getCost();
		return dist == 0 ? 0 : dist < 0 ? -1 : 1;
	}

	@Override
	public abstract boolean equals(final Object o);


}
