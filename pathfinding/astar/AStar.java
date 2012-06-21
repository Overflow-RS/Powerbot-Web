package pathfinding.astar;

import pathfinding.astar.types.Edge;
import pathfinding.astar.types.Graph;
import pathfinding.astar.types.Node;
import pathfinding.astar.wrappers.NodePath;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Author: Tom
 * Date: 07/05/12
 * Time: 17:11
 */
public class AStar {

	public static NodePath findPath(Graph graph, Node start, Node end) {
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		Set<Node> closed = new HashSet<Node>();
		graph.resetNodes(end);
		open.add(start);
		while (open.size() > 0) {
			Node current = open.poll();
			closed.add(current);
			if (current.equals(end)) {
				NodePath np = new NodePath();
				while (current!=null){
					np.add(current);
					current=current.getParent();
				}
				return np;
			}
			for (Edge e :current.getEdges()) {
				if(!e.canUse()){
					continue;
				}
				double currentCost = current.getCurrentCost() + e.getCost();
				Node target = e.getTarget();
				if(closed.contains(target)){
					continue;
				}
				if (target.getParent() == null||target.getCost()>currentCost) {
					target.setParent(e);
					open.add(target);
				}
			}
		}
		return null;
	}


}
