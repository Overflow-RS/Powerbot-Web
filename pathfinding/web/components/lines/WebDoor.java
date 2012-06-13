package pathfinding.web.components.lines;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.entity.DoorAction;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:52
 */
public class WebDoor extends WebComponent{
	public WebDoor(final String name,final Tile tileA, final Tile tileB,final int doorId) {
		super(tileA, tileB, new DoorAction(name,tileA,tileB,doorId),new DoorAction(name,tileB,tileA,doorId));
	}
}
