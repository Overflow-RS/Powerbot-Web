package pathfinding.web;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.map.Path;
import org.powerbot.game.bot.Context;
import pathfinding.astar.AStar;
import pathfinding.astar.types.Graph;
import pathfinding.astar.types.Node;
import pathfinding.astar.wrappers.NodePath;
import pathfinding.web.components.actions.teleports.LodestoneAction;
import pathfinding.web.components.base.WebEdge;
import pathfinding.web.components.base.WebNode;
import pathfinding.web.components.base.WebPath;
import pathfinding.web.components.lines.WebComponent;
import pathfinding.web.components.lines.WebTeleport;
import pathfinding.web.data.FTPLines;
import pathfinding.web.data.actions.Lumbridge;
import pathfinding.web.data.banking.Banks;
import pathfinding.web.data.banking.wrappers.WebBank;
import pathfinding.web.data.teleports.Teleports;
import pathfinding.web.interfaces.TileHeuristic;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:10
 */
public class RSWeb extends Graph {

	public RSWeb(ActiveScript script) {
		super(script, "RSWeb");
		debugMessage("Building web");
		setHeuristic(new TileHeuristic());
		for (WebComponent wc : FTPLines.componentArray) {
			this.addWebEdge(wc);
		}
		for (WebComponent wc : Lumbridge.LUMMY_CASTLE) {
			this.addWebEdge(wc);
		}
		for (WebTeleport[] arr : Teleports.ALL_TELEPORTS) {
			for (WebTeleport wt : arr) {
				addWebTeleport(wt);
			}
		}
		debugMessage("Web built");
	}

	public final void setLodestoneUsage(final boolean bool) {
		LodestoneAction.canUse = bool;
	}

	public final void setLodestoneCost(final double cost) {
		LodestoneAction.lodestoneCost = cost;
	}

	public final WebPath<WebEdge> getClosestBankPath() {
		WebPath<WebEdge> best = null;
		WebBank bank = null;
		double cost = Integer.MAX_VALUE;
		for (WebBank b : Banks.bankArray) {
			WebPath<WebEdge> path = getWebPath(b.getTile());
			double temp;
			if (path != null && (temp = path.getCost()) < cost) {
				bank = b;
				best = path;
				cost = temp;
			}
		}
		debugMessage(bank==null?"Unable to find bank":"Closest bank: " + bank.toString());
		return best;
	}

	public final WebPath<WebEdge> getPathToBank(final WebBank bank) {
		return getWebPath(bank.getTile());
	}

	public final void addWebEdge(final WebComponent line) {
		WebNode a = getNode(line.getTileA());
		WebNode b = getNode(line.getTileB());
		a.addEdge(new WebEdge(this, a, b, line.getActionA()));
		b.addEdge(new WebEdge(this, b, a, line.getActionB()));
	}

	public final void addWebTeleport(final WebTeleport line) {
		addWebTeleport(line,false);
	}

	public final void addWebTeleport(final WebTeleport line,final  boolean newNode) {
		Tile target = line.getTileA();
		WebNode nearest;
		if (newNode) {
			nearest = getNode(target);
		} else {
			nearest = getNearestNode(target);
		}
		for (Node n : getNodes()) {
			n.addEdge(new WebEdge(this, (WebNode) n, nearest, line.getActionA()));
		}
	}

	private WebNode getNode(final Tile tile) {
		WebNode targ = new WebNode(this, tile);
		for (Node n : getNodes()) {
			WebNode temp = (WebNode) n;
			if (n.equals(targ)) {
				return temp;
			}
		}
		addNode(targ);
		return targ;
	}

	public final WebNode getNearestNode(final Tile tile) {
		WebNode nearest = null;
		double dist = Integer.MAX_VALUE;
		for (Node n : getNodes()) {
			WebNode node = (WebNode) n;
			if (node.getTile().getPlane() != tile.getPlane()) {
				continue;
			}
			double temp = Calculations.distance(node.getTile(), tile);
			if (temp < dist) {
				nearest = node;
				dist = temp;
			}
		}
		return nearest;
	}

	private NodePath<WebNode> getPath(final Tile source,final Tile target) {
		WebNode start = getNearestNode(source);
		WebNode end = getNearestNode(target);
		if (start == null || end == null) {
			debugMessage("Failed to find start or end node");
			return null;
		}
		return AStar.findPath(this, start, end);
	}

	private WebPath<WebEdge> buildWebPath(final NodePath<WebNode> nodes,final  Tile finalTile) {
		WebPath<WebEdge> edges = new WebPath<WebEdge>(finalTile);
		WebNode current = nodes.removeLast();
		while (nodes.size() > 0) {
			WebNode temp = nodes.removeLast();
			edges.add((WebEdge) current.getEdge(temp));
			current = temp;
		}
		return edges;
	}

	public final WebPath<WebEdge> getWebPath(final Tile source,final  Tile target) {
		NodePath<WebNode> nodes = getPath(source, target);
		return nodes == null ? null : buildWebPath(nodes, target);
	}

	public final WebPath<WebEdge> getWebPath(final Tile target) {
		return getWebPath(Players.getLocal().getLocation(), target);
	}

	public final boolean walkToTile(final Tile target) {
		if (target.canReach()) {
			return walkTo(target);
		}
		WebPath path = getWebPath(target);
		return path != null && path.traverse();
	}

	private boolean walkTo(final Tile target) {
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

}
