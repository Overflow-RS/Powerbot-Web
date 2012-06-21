package pathfinding.web.components.base;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.astar.types.Graph;
import pathfinding.astar.types.Node;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:14
 */
public class WebNode extends Node {


	private final Tile tile;

	public WebNode(final Graph container, Tile a) {
		super(container);
		this.tile = a;
	}

	public Tile getTile() {
		return tile;
	}

	@Override
	public boolean equals(final Object o) {
		if(o instanceof WebNode){
			WebNode wb = (WebNode) o;
			return wb.getTile().equals(tile);
		}
		return false;
	}

	@Override
	public String toString(){
		return "("+tile.getX()+","+tile.getY()+","+tile.getPlane()+")";
	}
}
