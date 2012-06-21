package pathfinding.web;

import org.powerbot.game.api.wrappers.Tile;
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
		Tile start = new Tile(3295, 3177, 0);
		Tile end = new Tile(2949, 3439, 0);
		LinkedList<WebEdge> edges = web.getWebPath(start, end);
		if (edges != null) {
			web.debugMessage("With lodestones enabled");
			for (WebEdge e : edges) {
				web.debugMessage(e.toString());
			}
		}
		web.setLodestoneUsage(false);
		edges = web.getWebPath(start, end);
		if (edges != null) {
			web.debugMessage("With lodestones disabled");
			for (WebEdge e : edges) {
				web.debugMessage(e.toString());
			}
		}

	}

}
