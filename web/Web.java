package web;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import web.data.Banks;
import web.data.FTPLines;
import web.data.Lumbridge;
import web.resources.banking.BANK_TYPE;
import web.resources.banking.WebBank;
import web.resources.pathfinding.*;
import web.types.Base.WebComponent;

import java.util.ArrayList;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 16:55
 */
public class Web {

	private final Graph graph;

	public Web(WebComponent[] components) {
		graph = new Graph();
		buildWeb();
		for (WebComponent component : components) {
			graph.addWebComponent(component);
		}
	}

	public Web() {
		graph = new Graph();
		buildWeb();
	}

	private void buildWeb() {
		for (WebComponent c : FTPLines.components) {
			updateWeb(c);
		}
		for(WebComponent c: Lumbridge.LUMBRIDGE_COMPS){
			updateWeb(c);
		}
	}

	public void updateWeb(WebComponent component) {
		graph.addWebComponent(component);
	}

	public Vertex getClosestVertex(Tile pos) {
		Vertex closest = null;
		double dist = -1;
		for (Vertex v : graph.getVertexes()) {
			double temp = Calculations.distance(pos, v.getTile());
			if (closest == null) {
				closest = v;
				dist = temp;
			} else if (temp < dist) {
				closest = v;
				dist = temp;
			}
		}
		return closest;
	}

	private SortedList        open   = new SortedList();
	private ArrayList<Vertex> closed = new ArrayList();

	public WebPath findPath(Tile source, Tile target) {
		open.clear();
		closed.clear();
		Vertex start = getClosestVertex(source);
		Vertex end = getClosestVertex(target);
		Vertex current = start;
		current.setCurrentCost(0);
		current.setHeuristicCost(Calculations.distance(start.getTile(), end.getTile()));
		open.add(current);
		int i = 0;
		while (!open.isEmpty()) {
			current = (Vertex) open.first();
			open.remove(current);
			closed.add(current);
			if (current.equals(end)) {
				return getPath(start, current, target);
			}
			for (Edge e : current.getEdges()) {
				if (e.getComponent().canDoAction()) {
					if (isA(current, e)) {
						double tempCost = current.getCurrentCost() + e.getWeight();
						Vertex b = getVertex(e.getComponent().getB());
						if (b != null) {
							if (closed.contains(b) && tempCost < b.getCurrentCost()) {
								e.setaToB(true);
								b.setHeuristicCost(Calculations.distance(b.getTile(), target));
								b.setCurrentCost(tempCost);
								b.setParent(e);
								continue;
							}
							if (open.contains(b) && tempCost < b.getCurrentCost()) {
								e.setaToB(true);
								b.setHeuristicCost(Calculations.distance(b.getTile(), target));
								b.setCurrentCost(tempCost);
								b.setParent(e);
								continue;
							}
						} else {
							b = graph.getVertex(new Vertex(e.getComponent().getB()));
							e.setaToB(true);
							b.setCurrentCost(tempCost);
							b.setParent(e);
							open.add(b);
						}
					} else {
						double tempCost = current.getCurrentCost() + e.getWeight();
						Vertex a = getVertex(e.getComponent().getA());
						if (a != null) {
							if (closed.contains(a) && tempCost < a.getCurrentCost()) {
								e.setaToB(false);
								a.setHeuristicCost(Calculations.distance(a.getTile(), target));
								a.setCurrentCost(tempCost);
								a.setParent(e);
								continue;
							}
							if (open.contains(a) && tempCost < a.getCurrentCost()) {
								e.setaToB(false);
								a.setHeuristicCost(Calculations.distance(a.getTile(), target));
								a.setCurrentCost(tempCost);
								a.setParent(e);
								continue;
							}
						} else {
							a = graph.getVertex(new Vertex(e.getComponent().getA()));
							e.setaToB(false);
							a.setCurrentCost(tempCost);
							a.setParent(e);
							open.add(a);
						}
					}
				}
			}
		}
		System.out.println("Failed to find path");
		return null;
	}

	public Graph getGraph() {
		return graph;
	}

	public Vertex getVertex(Tile t) {
		for (Vertex v : closed) {
			if (v.equals(new Vertex(t))) {
				return v;
			}
		}
		for (Object o : open.getList()) {
			Vertex v = (Vertex) o;
			if (v.equals(new Vertex(t))) {
				return v;
			}
		}
		return null;
	}


	public boolean isA(Vertex v, Edge e) {
		return v.getTile().equals(e.getComponent().getA());
	}

	public WebPath getPath(Vertex startNode, Vertex endNode, Tile endTile) {
		WebPath path = new WebPath(endTile);
		Vertex current = endNode;
		int i = 0;
		path.appendStep(current.getParent());
		if (current.getParent().isaToB()) {
			current = getVertex(current.getParent().getComponent().getA());
		} else {
			current = getVertex(current.getParent().getComponent().getB());
		}
		while (!current.equals(startNode)) {
			path.prependStep(current.getParent());
			if (current.getParent().isaToB()) {
				current = getVertex(current.getParent().getComponent().getA());
			} else {
				current = getVertex(current.getParent().getComponent().getB());
			}
			if (i++ == 1000) {
				break;
			}
		}
		return path;
	}

	public WebPath getNearestBankPath() {
		WebPath closest = null;
		double dist = -1;
		Tile myTile = Players.getLocal().getPosition();
		for (WebBank bank : Banks.bankArray) {
			if (bank.getType().equals(BANK_TYPE.DEPOSIT_BOX)) {
				continue;
			}
			if (bank.canAccess()) {
				WebPath temp = findPath(myTile, bank.getTile());
				if (temp != null) {
					double tempDist = temp.getTotalWeight();
					if (closest == null) {
						closest = temp;
						dist = tempDist;
					} else if (tempDist < dist) {
						closest = temp;
						dist = tempDist;
					}
				}
			}
		}
		return closest;
	}
}
