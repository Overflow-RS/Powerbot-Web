package web.resources.pathfinding;

import org.powerbot.game.api.wrappers.Tile;

import java.util.ArrayList;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 23:35
 */
public class Vertex implements Comparable {

	private final Tile tile;

	private ArrayList<Edge> edges = new ArrayList<Edge>();

	private double currentCost, heuristicCost;

	private Edge parent = null;

	public Vertex(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

	public double getCurrentCost() {
		return currentCost;
	}

	public void setCurrentCost(final double currentCost) {
		this.currentCost = currentCost;
	}

	public double getHeuristicCost() {
		return heuristicCost;
	}

	public void setHeuristicCost(final double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

	public ArrayList<Edge> getEdges() {
		return edges;

	}

	public Edge getParent() {
		return parent;
	}

	public void setParent(final Edge parent) {
		this.parent = parent;
	}

	public void addEdge(Edge e) {
		edges.add(e);
	}

	@Override
	public String toString(){
		return tile.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vertex) {
			Vertex a = (Vertex) o;
			return tile.equals(a.getTile());
		}
		return false;
	}

	@Override
	public int compareTo(final Object o) {
		Vertex other = (Vertex) o;
		double f = getCurrentCost() + getHeuristicCost();
		double of = other.getCurrentCost() + other.getHeuristicCost();
		if (f < of) {
			return -1;
		} else if (f > of) {
			return 1;
		} else {
			return 0;
		}
	}
}
