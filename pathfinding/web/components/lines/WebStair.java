package pathfinding.web.components.lines;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.entity.StairAction;

/**
 * Author: Tom
 * Date: 14/06/12
 * Time: 19:14
 */
public class WebStair extends WebComponent{
	public WebStair(final Tile tileA, final Tile tileB,final String nameA,final String nameB,final String actionA,final String actionB,final int stairIdA, final int stairIdb) {
		super(tileA, tileB, new StairAction(nameA,tileA,tileB,actionA,stairIdA), new StairAction(nameB,tileB,tileA,actionB,stairIdb));
	}
}
