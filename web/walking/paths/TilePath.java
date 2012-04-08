package web.walking.paths;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import web.walking.WalkingUtil;

/**
 * Author: Tom
 * Date: 08/04/12
 * Time: 15:57
 */
public class TilePath extends Path {

	private final Tile[] tiles;
	private boolean end = false;

	public TilePath(final Tile[] tiles) {
		this.tiles = tiles;
	}

	@Override
	public Tile[] getTiles() {
		return tiles;
	}

	@Override
	public Tile getStart() {
		return tiles[0];
	}

	@Override
	public Tile getNext() {
		final Tile playerLoc = Players.getLocal().getPosition();
		if(tiles == null){
			return null;
		}
		for (int i = tiles.length - 1; i >= 0; --i) {
			if (WalkingUtil.tileOnMap(tiles[i])) {
				final int length = WalkingUtil.pathLengthBetween(playerLoc, tiles[i], false);
				if (length != -1 && length <= 30) {
					return tiles[i];
				}
			}
		}
		return null;
	}

	@Override
	public Tile getDestination() {
		return tiles[tiles.length - 1];
	}

	@Override
	public boolean step() {
		final Tile next = getNext();
		if (next == null) {
			return false;
		}
		if (next.equals(getDestination())) {
			if (Calculations.distance(Players.getLocal().getPosition(), next) <= 2 || next.equals(org.powerbot.game.api.methods.Walking.getDestination())) {
				return false;
			}
		}
		final Tile dest = org.powerbot.game.api.methods.Walking.getDestination();
		if ((dest != null && Players.getLocal().isMoving() && Calculations.distance(Players.getLocal().getPosition(), dest) > 4 && Calculations.distance(
				next, dest) < 5) || (dest == null && Players.getLocal().isMoving())) {
			return true;
		}
		return org.powerbot.game.api.methods.Walking.walk(next);
	}

}