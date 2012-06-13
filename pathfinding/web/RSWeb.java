package pathfinding.web;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import pathfinding.astar.AStar;
import pathfinding.astar.types.Graph;
import pathfinding.astar.types.Node;
import pathfinding.astar.wrappers.NodePath;
import pathfinding.web.components.base.WebEdge;
import pathfinding.web.components.base.WebNode;
import pathfinding.web.components.base.WebPath;
import pathfinding.web.components.lines.WebComponent;
import pathfinding.web.components.lines.WebTeleport;
import pathfinding.web.data.FTPLines;
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
		for(WebTeleport[] arr: Teleports.ALL_TELEPORTS){
			for(WebTeleport wt:arr){
				addWebTeleport(wt);
			}
		}
		debugMessage("Web built");
	}

	public WebPath<WebEdge> getClosestBankPath(){
		WebPath<WebEdge> best = null;
		WebBank bank = null;
		double cost = Integer.MAX_VALUE;
		for(WebBank b: Banks.bankArray){
			WebPath<WebEdge> path = getWebPath(b.getTile());
			double temp;
			if(path!=null&&(temp=path.getCost())<cost){
				bank=b;
				best=path;
				cost=temp;
			}
		}
		debugMessage("Closest bank: "+bank.toString());
		return best;
	}

	public WebPath<WebEdge> getPathToBank(WebBank bank){
		return getWebPath(bank.getTile());
	}

	public void addWebEdge(WebComponent line) {
		WebNode a = getNode(line.getTileA());
		WebNode b = getNode(line.getTileB());
		a.addEdge(new WebEdge(this, a, b, line.getActionA()));
		b.addEdge(new WebEdge(this, b, a, line.getActionB()));
	}

	public void addWebTeleport(WebTeleport line) {
		Tile target = line.getTileA();
		WebNode nearest = getNearestNode(target);
		debugMessage("Adding teleport: "+line.getTileA()+" > "+nearest.toString());
		for(Node n:nodes){
			n.addEdge(new WebEdge(this,(WebNode)n,nearest,line.getActionA()));
		}
	}

	private WebNode getNode(Tile tile) {
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

	private WebNode getNearestNode(Tile tile) {
		WebNode nearest = null;
		double dist = Integer.MAX_VALUE;
		for (Node n : getNodes()) {
			WebNode node = (WebNode) n;
			double temp = Calculations.distance(node.getTile(), tile);
			if (temp < dist) {
				nearest = node;
				dist = temp;
			}
		}
		return nearest;
	}

	private NodePath<WebNode> getPath(Tile source, Tile target) {
		WebNode start = getNearestNode(source);
		WebNode end = getNearestNode(target);
		if (start == null || end == null) {
			debugMessage("Failed to find start or end node");
			return null;
		}
		return AStar.findPath(this, start, end);
	}

	private WebPath<WebEdge> buildWebPath(NodePath<WebNode> nodes,Tile finalTile) {
		WebPath<WebEdge> edges = new WebPath<WebEdge>(finalTile);
		WebNode current = nodes.removeLast();
		while (nodes.size() > 0) {
			WebNode temp = nodes.removeLast();
			edges.add((WebEdge) current.getEdge(temp));
			current = temp;
		}
		return edges;
	}

	public WebPath<WebEdge> getWebPath(Tile source, Tile target) {
		NodePath<WebNode> nodes = getPath(source, target);
		return nodes == null ? null : buildWebPath(nodes,target);
	}

	public WebPath<WebEdge> getWebPath(Tile target){
		return getWebPath(Players.getLocal().getLocation(),target);
	}
}
