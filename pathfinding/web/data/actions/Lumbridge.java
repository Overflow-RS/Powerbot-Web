package pathfinding.web.data.actions;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.lines.WebComponent;
import pathfinding.web.components.lines.WebStair;

/**
 * Author: Tom
 * Date: 14/06/12
 * Time: 19:33
 */
public class Lumbridge {

	public static WebComponent[] LUMMY_CASTLE = {
			new WebStair(new Tile(3206, 3209, 0), new Tile(3206, 3209, 1), "Lumbridge castle 0-1", "Lumbridge castle 1-0", "Climb-up", "Climb-down", 36773,
			             36774),
			new WebStair(new Tile(3206, 3209, 1), new Tile(3206, 3209, 2), "Lumbridge castle 1-2", "Lumbridge castle 2-1", "Climb-up", "Climb-down", 36774,
			             36775),
			new WebStair(new Tile(3215, 3218, 0), new Tile(3208, 9616, 0), "Lumbridge castle 0-B", "Lumbridge castle B-0", "Climb-down", "Climb-up", 36687,
			             29355),
	};
}
