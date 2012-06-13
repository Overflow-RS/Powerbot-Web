package pathfinding.web.components.lines;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 14:53
 */
public class WebTeleport extends WebComponent{

	public WebTeleport (final Tile tileA, final WebAction actionA) {
		super(tileA, null, actionA, null);
	}
}
