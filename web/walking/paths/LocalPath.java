package web.walking.paths;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import web.walking.Pathfinder;
import web.walking.WalkingUtil;

/**
 * Author: Tom
 * Date: 08/04/12
 * Time: 15:57
 */
public class LocalPath extends Path {

	private       TilePath path;
	private final Tile     dest;
	private       Tile     base;

	public LocalPath(final Tile dest) {
		this.path = new TilePath(Pathfinder.findPath(Players.getLocal().getPosition(), dest));
		this.dest = dest;
		this.base = new Tile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
	}

	@Override
	public Tile[] getTiles() {
		return path.getTiles();
	}

	@Override
	public Tile getStart() {
		return path.getStart();
	}

	@Override
	public Tile getNext() {
		if (!(new Tile(Game.getBaseX(), Game.getBaseY(), Game.getPlane())).equals(base)) {
			final int[][] flags = WalkingUtil.getCollisionFlags(Game.getPlane());
			if (flags != null) {
				base = new Tile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
				final Tile[] tiles = Pathfinder.findPath(Players.getLocal().getPosition(), dest);
				if (tiles == null) {
					base = null;
					return null;
				}
				path = new TilePath(tiles);
			}
		}
		return path.getNext();
	}

	@Override
	public Tile getDestination() {
		return dest;
	}

	@Override
	public boolean step() {
		return getNext() != null && path.step();
	}

}