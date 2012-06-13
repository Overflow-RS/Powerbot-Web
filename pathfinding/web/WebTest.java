package pathfinding.web;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.astar.types.Edge;
import pathfinding.web.components.base.WebEdge;

import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 00:38
 */
public class WebTest {

	public static void main(String[] args) {
		RSWeb web = new RSWeb(null);
		web.setDebug(true);
		System.out.println("Web added ");
		System.out.println("Nodes: " + web.getNodes().size());
		Tile start = new Tile(3295, 3177, 0);
		Tile end = new Tile(2949, 3439, 0);
		long tick = System.currentTimeMillis();
		int testCount = 1;
		for (int i = 0; i < testCount; i++) {
			LinkedList<WebEdge> edges = web.getWebPath(start, end);
			if (edges != null) {
				web.debugMessage("Path found in " + (System.currentTimeMillis() - tick) + "ms");
				for(Edge e: edges){
					web.debugMessage(e.toString());
				}
			}
		}
		web.debugMessage(testCount + " Paths generated in: " + (System.currentTimeMillis() - tick) + "ms");
	}
}
