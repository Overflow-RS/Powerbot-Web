package pathfinding.astar.types;

import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 22:25
 */
public abstract class Node implements Comparable<Node> {

	private final Graph container;
	private LinkedList<Edge> edges = new LinkedList<Edge>();

	private Node parent;

	private double heuristicCost;

	public Node(final Graph container) {
		this.container = container;
	}

	public Graph getContainer() {
		return container;
	}

	public LinkedList<Edge> getEdges() {
		return edges;
	}

	public Edge getEdge(Node node) {
		for (Edge e : edges) {
			if (e.getTarget().equals(node)) {
				return e;
			}
		}
		return null;
	}

	public void addEdge(Edge e) {
		edges.add(e);
	}

	public void reset(Node target) {
		heuristicCost = container.getHeuristic().getCost(this, target);
		parent = null;
	}

	public double getCurrentCost() {
		return parent == null ? 0 : parent.getCurrentCost();
	}

	public double getHeuristicCost() {
		return heuristicCost;
	}

	public double getCost() {
		return getHeuristicCost() + getCurrentCost();
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(final Edge edge) {
		this.parent = edge.getSource();
	}

	@Override
	public int compareTo(final Node o) {
		double dist = getCost() - o.getCost();
		return dist == 0 ? 0 : dist < 0 ? -1 : 1;
	}

	@Override
	public abstract boolean equals(Object o);


}
