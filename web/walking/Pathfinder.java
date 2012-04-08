package web.walking;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Author: Tom
 * Date: 08/04/12
 * Time: 15:59
 */
public class Pathfinder {

	public static final int WALL_NORTH_WEST = 0x1;
	public static final int WALL_NORTH      = 0x2;
	public static final int WALL_NORTH_EAST = 0x4;
	public static final int WALL_EAST       = 0x8;
	public static final int WALL_SOUTH_EAST = 0x10;
	public static final int WALL_SOUTH      = 0x20;
	public static final int WALL_SOUTH_WEST = 0x40;
	public static final int WALL_WEST       = 0x80;
	public static final int BLOCKED         = 0x100;
	public static final int WATER           = 0x1280100;
	protected static Tile    base;
	protected static int[][] flags;
	protected static int     offX, offY;

	public static Tile[] findPath(final Tile start, final Tile end) {
		return findPath(start, end, false);
	}

	protected static Tile[] findPath(final Tile start, final Tile end, boolean remote) {
		base = new Tile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
		final int base_x = base.getX(), base_y = base.getY();
		final int curr_x = start.getX() - base_x, curr_y = start.getY() - base_y;
		int dest_x = end.getX() - base_x, dest_y = end.getY() - base_y;
		final int plane = Game.getPlane();
		flags = WalkingUtil.getCollisionFlags(plane);
		final Tile offset = WalkingUtil.getCollisionOffset(plane);
		offX = offset.getX();
		offY = offset.getY();
		if (flags == null || curr_x < 0 || curr_y < 0 || curr_x >= flags.length || curr_y >= flags.length) {
			return null;
		} else if (dest_x < 0 || dest_y < 0 || dest_x >= flags.length || dest_y >= flags.length) {
			remote = true;
			if (dest_x < 0) {
				dest_x = 0;
			} else if (dest_x >= flags.length) {
				dest_x = flags.length - 1;
			}
			if (dest_y < 0) {
				dest_y = 0;
			} else if (dest_y >= flags.length) {
				dest_y = flags.length - 1;
			}
		}
		final HashSet<Node> open = new HashSet<Node>();
		final HashSet<Node> closed = new HashSet<Node>();
		Node curr = new Node(curr_x, curr_y);
		final Node dest = new Node(dest_x, dest_y);
		curr.f = heuristic(curr, dest);
		open.add(curr);
		while (!open.isEmpty()) {
			curr = lowest_f(open);
			if (curr.equals(dest)) {
				return path(curr, base_x, base_y);
			}
			open.remove(curr);
			closed.add(curr);
			for (final Node next : successors(curr)) {
				if (!closed.contains(next)) {
					final double t = curr.g + dist(curr, next);
					boolean use_t = false;
					if (!open.contains(next)) {
						open.add(next);
						use_t = true;
					} else if (t < next.g) {
						use_t = true;
					}
					if (use_t) {
						next.prev = curr;
						next.g = t;
						next.f = t + heuristic(next, dest);
					}
				}
			}
		}
		if (Calculations.distance(Players.getLocal().getPosition(), end) < 10) {
			return null;
		}
		return findPath(start, pull(end));
	}

	protected static class Node {

		public int x, y;
		public Node   prev;
		public double g, f;

		public Node(final int x, final int y) {
			this.x = x;
			this.y = y;
			g = f = 0;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Node) {
				final Node n = (Node) o;
				return x == n.x && y == n.y;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return x << 4 | y;
		}

		public Tile toTile(final int baseX, final int baseY) {
			return new Tile(x + baseX, y + baseY, Game.getPlane());
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	private static double dist(final Node start, final Node end) {
		if (start.x != end.x && start.y != end.y) {
			return 1.41421356;
		} else {
			return 1.0;
		}
	}

	private static double heuristic(final Node start, final Node end) {
		double dx = start.x - end.x;
		double dy = start.y - end.y;
		if (dx < 0) {
			dx = -dx;
		}
		if (dy < 0) {
			dy = -dy;
		}
		return dx < dy ? dy : dx;
	}

	private static Node lowest_f(final Set<Node> open) {
		Node best = null;
		for (final Node t : open) {
			if (best == null || t.f < best.f) {
				best = t;
			}
		}
		return best;
	}

	private static Tile[] path(final Node end, final int base_x, final int base_y) {
		final LinkedList<Tile> path = new LinkedList<Tile>();
		Node p = end;
		while (p != null) {
			path.addFirst(p.toTile(base_x, base_y));
			p = p.prev;
		}
		return path.toArray(new Tile[path.size()]);
	}

	private static Tile pull(final Tile tile) {
		final Tile p = Players.getLocal().getPosition();
		int x = tile.getX(), y = tile.getY();
		if (p.getX() < x) {
			x -= 2;
		} else if (p.getX() > x) {
			x += 2;
		}
		if (p.getY() < y) {
			y -= 2;
		} else if (p.getY() > y) {
			y += 2;
		}
		return new Tile(x, y, Game.getPlane());
	}

	private static java.util.List<Node> successors(final Node t) {
		final LinkedList<Node> tiles = new LinkedList<Node>();
		final int x = t.x, y = t.y;
		final int f_x = x - offX, f_y = y - offY;
		final int here = flags[f_x][f_y];
		final int upper = flags.length - 1;
		if (f_y > 0 && (here & Pathfinder.WALL_SOUTH) == 0 && (flags[f_x][f_y - 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0) {
			tiles.add(new Node(x, y - 1));
		}
		if (f_x > 0 && (here & Pathfinder.WALL_WEST) == 0 && (flags[f_x - 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0) {
			tiles.add(new Node(x - 1, y));
		}
		if (f_y < upper && (here & Pathfinder.WALL_NORTH) == 0 && (flags[f_x][f_y + 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0) {
			tiles.add(new Node(x, y + 1));
		}
		if (f_x < upper && (here & Pathfinder.WALL_EAST) == 0 && (flags[f_x + 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0) {
			tiles.add(new Node(x + 1, y));
		}
		if (f_x > 0 && f_y > 0 && (here & (Pathfinder.WALL_SOUTH_WEST | Pathfinder.WALL_SOUTH | Pathfinder.WALL_WEST)) == 0 &&
				(flags[f_x - 1][f_y - 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0 &&
				(flags[f_x][f_y - 1] & (Pathfinder.BLOCKED | Pathfinder.WALL_WEST)) == 0 &&
				(flags[f_x - 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WALL_SOUTH)) == 0) {
			tiles.add(new Node(x - 1, y - 1));
		}
		if (f_x > 0 && f_y < upper && (here & (Pathfinder.WALL_NORTH_WEST | Pathfinder.WALL_NORTH | Pathfinder.WALL_WEST)) == 0 &&
				(flags[f_x - 1][f_y + 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0 &&
				(flags[f_x][f_y + 1] & (Pathfinder.BLOCKED | Pathfinder.WALL_WEST)) == 0 &&
				(flags[f_x - 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WALL_NORTH)) == 0) {
			tiles.add(new Node(x - 1, y + 1));
		}
		if (f_x < upper && f_y > 0 && (here & (Pathfinder.WALL_SOUTH_EAST | Pathfinder.WALL_SOUTH | Pathfinder.WALL_EAST)) == 0 &&
				(flags[f_x + 1][f_y - 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0 &&
				(flags[f_x][f_y - 1] & (Pathfinder.BLOCKED | Pathfinder.WALL_EAST)) == 0 &&
				(flags[f_x + 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WALL_SOUTH)) == 0) {
			tiles.add(new Node(x + 1, y - 1));
		}
		if (f_x > 0 && f_y < upper && (here & (Pathfinder.WALL_NORTH_EAST | Pathfinder.WALL_NORTH | Pathfinder.WALL_EAST)) == 0 &&
				(flags[f_x + 1][f_y + 1] & (Pathfinder.BLOCKED | Pathfinder.WATER)) == 0 &&
				(flags[f_x][f_y + 1] & (Pathfinder.BLOCKED | Pathfinder.WALL_EAST)) == 0 &&
				(flags[f_x + 1][f_y] & (Pathfinder.BLOCKED | Pathfinder.WALL_NORTH)) == 0) {
			tiles.add(new Node(x + 1, y + 1));
		}
		return tiles;
	}
}