package web.walking.paths;

import org.powerbot.game.api.wrappers.Tile;

/**
 * Author: Tom
 * Date: 08/04/12
 * Time: 15:56
 */
public abstract class Path {

	public abstract Tile[] getTiles();

	public abstract Tile getStart();

	public abstract Tile getNext();

	public abstract Tile getDestination();

	public abstract boolean step();

}