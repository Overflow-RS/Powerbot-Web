package web.walking;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.bot.h;
import org.powerbot.game.client.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 22:00
 */
public class RSWalking {

	public static abstract class Path {

		public abstract Tile[] getTiles();

		public abstract Tile getStart();

		public abstract Tile getNext();

		public abstract Tile getDestination();

		public abstract boolean step();

	}

	public static class TilePath extends Path {

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

	public static class LocalPath extends Path {

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

	public static class WalkingUtil {

		/*
								 * Temporary replacements for Walking.getCollisionFlags() and Walking.getCollisionOffset()
								 */

		public static int[][] getCollisionFlags(final int plane) {
			RSInfoGroundData obj = (RSInfoGroundData) h.m().e().getRSGroundInfo();
			Object[] data = (Object[]) obj.getRSInfoGroundData();
			RSGroundDataBlocks blocks = (RSGroundDataBlocks) data[plane];
			return (int[][]) blocks.getRSGroundDataBlocks();
		}

		public static Tile getCollisionOffset(final int plane) {
			RSInfoGroundData obj = (RSInfoGroundData) h.m().e().getRSGroundInfo();
			Object[] data = (Object[]) obj.getRSInfoGroundData();
			RSGroundDataInts ints = (RSGroundDataInts) data[plane];
			return new Tile(((RSGroundDataX) ints.getRSGroundDataInts()).getRSGroundDataX() * h.m().s.GROUNDDATA_X,
			                ((RSGroundDataY) ints.getRSGroundDataInts()).getRSGroundDataY() * h.m().s.GROUNDDATA_Y, plane);
		}

		/**
		 * Determines whether or not a given tile is displayed on the minimap.
		 *
		 * @param t The tile to check.
		 * @return <tt>true</tt> if on map, otherwise <tt>false</tt>.
		 */

		public static boolean tileOnMap(final Tile t) {
			return Calculations.distance(Players.getLocal().getPosition(), t) < 16;
		}

		/**
		 * Determines wether or not a given tile can be reached
		 *
		 * @param t        The tile to check.
		 * @param isObject <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
		 * @return <tt>true</tt> if the tile is reachable and in the loaded map region, otherwise <tt>false</tt>
		 */
		public static boolean canReach(final Tile t, final boolean isObject) {
			return isLocal(t) && pathLengthBetween(Players.getLocal().getPosition(), t, isObject) != -1;
		}

		/**
		 * Returns the length of the path generates between two Tiles.
		 *
		 * @param start        The starting tile.
		 * @param dest         The destination tile.
		 * @param destIsObject <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
		 * @return The length of the generated path, or -1 if unreachable
		 */

		public static int pathLengthBetween(final Tile start, final Tile dest, final boolean destIsObject) {
			return dijkstraDist(start.getX() - Game.getBaseX(), start.getY() - Game.getBaseY(), dest.getX() - Game.getBaseX(), dest.getY() - Game.getBaseY(),
			                    destIsObject);
		}

		/**
		 * Determines whether or not a given tile is in the loaded map area.
		 *
		 * @param tile The tile to check.
		 * @return <tt>true</tt> if local; otherwise <tt>false</tt>.
		 */
		public static boolean isLocal(final Tile tile) {
			final int[][] flags = getCollisionFlags(Game.getPlane());
			final int x = tile.getX() - Game.getBaseX();
			final int y = tile.getY() - Game.getBaseY();
			return flags != null && x >= 0 && y >= 0 && x < flags.length && y < flags.length;
		}

		/**
		 * @param startX   the startX (0 < startX < 104)
		 * @param startY   the startY (0 < startY < 104)
		 * @param destX    the destX (0 < destX < 104)
		 * @param destY    the destY (0 < destY < 104)
		 * @param isObject if it's an object, it will find path which touches it.
		 * @return The distance of the shortest path to the destination; or -1 if no valid path to the destination was found.
		 */
		private static int dijkstraDist(final int startX, final int startY, final int destX, final int destY, final boolean isObject) {
			try {
				final int[][] prev = new int[104][104];
				final int[][] dist = new int[104][104];
				final int[] path_x = new int[4000];
				final int[] path_y = new int[4000];
				for (int xx = 0; xx < 104; xx++) {
					for (int yy = 0; yy < 104; yy++) {
						prev[xx][yy] = 0;
						dist[xx][yy] = 99999999;
					}
				}
				int curr_x = startX;
				int curr_y = startY;
				prev[startX][startY] = 99;
				dist[startX][startY] = 0;
				int path_ptr = 0;
				int step_ptr = 0;
				path_x[path_ptr] = startX;
				path_y[path_ptr++] = startY;
				final int blocks[][] = WalkingUtil.getCollisionFlags(Game.getPlane());
				final int pathLength = path_x.length;
				boolean foundPath = false;
				while (step_ptr != path_ptr) {
					curr_x = path_x[step_ptr];
					curr_y = path_y[step_ptr];
					if (Math.abs(curr_x - destX) + Math.abs(curr_y - destY) == (isObject ? 1 : 0)) {
						foundPath = true;
						break;
					}
					step_ptr = (step_ptr + 1) % pathLength;
					final int cost = dist[curr_x][curr_y] + 1;
					// south
					if (curr_y > 0 && prev[curr_x][curr_y - 1] == 0 && (blocks[curr_x + 1][curr_y] & 0x1280102) == 0) {
						path_x[path_ptr] = curr_x;
						path_y[path_ptr] = curr_y - 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x][curr_y - 1] = 1;
						dist[curr_x][curr_y - 1] = cost;
					}
					// west
					if (curr_x > 0 && prev[curr_x - 1][curr_y] == 0 && (blocks[curr_x][curr_y + 1] & 0x1280108) == 0) {
						path_x[path_ptr] = curr_x - 1;
						path_y[path_ptr] = curr_y;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x - 1][curr_y] = 2;
						dist[curr_x - 1][curr_y] = cost;
					}
					// north
					if (curr_y < 104 - 1 && prev[curr_x][curr_y + 1] == 0 && (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0) {
						path_x[path_ptr] = curr_x;
						path_y[path_ptr] = curr_y + 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x][curr_y + 1] = 4;
						dist[curr_x][curr_y + 1] = cost;
					}
					// east
					if (curr_x < 104 - 1 && prev[curr_x + 1][curr_y] == 0 && (blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0) {
						path_x[path_ptr] = curr_x + 1;
						path_y[path_ptr] = curr_y;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x + 1][curr_y] = 8;
						dist[curr_x + 1][curr_y] = cost;
					}
					// south west
					if (curr_x > 0 && curr_y > 0 && prev[curr_x - 1][curr_y - 1] == 0 && (blocks[curr_x][curr_y] & 0x128010e) == 0 &&
							(blocks[curr_x][curr_y + 1] & 0x1280108) == 0 && (blocks[curr_x + 1][curr_y] & 0x1280102) == 0) {
						path_x[path_ptr] = curr_x - 1;
						path_y[path_ptr] = curr_y - 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x - 1][curr_y - 1] = 3;
						dist[curr_x - 1][curr_y - 1] = cost;
					}
					// north west
					if (curr_x > 0 && curr_y < 104 - 1 && prev[curr_x - 1][curr_y + 1] == 0 && (blocks[curr_x][curr_y + 2] & 0x1280138) == 0 &&
							(blocks[curr_x][curr_y + 1] & 0x1280108) == 0 && (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0) {
						path_x[path_ptr] = curr_x - 1;
						path_y[path_ptr] = curr_y + 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x - 1][curr_y + 1] = 6;
						dist[curr_x - 1][curr_y + 1] = cost;
					}
					// south east
					if (curr_x < 104 - 1 && curr_y > 0 && prev[curr_x + 1][curr_y - 1] == 0 && (blocks[curr_x + 2][curr_y] & 0x1280183) == 0 &&
							(blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0 && (blocks[curr_x + 1][curr_y] & 0x1280102) == 0) {
						path_x[path_ptr] = curr_x + 1;
						path_y[path_ptr] = curr_y - 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x + 1][curr_y - 1] = 9;
						dist[curr_x + 1][curr_y - 1] = cost;
					}
					// north east
					if (curr_x < 104 - 1 && curr_y < 104 - 1 && prev[curr_x + 1][curr_y + 1] == 0 && (blocks[curr_x + 2][curr_y + 2] & 0x12801e0) == 0 &&
							(blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0 && (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0) {
						path_x[path_ptr] = curr_x + 1;
						path_y[path_ptr] = curr_y + 1;
						path_ptr = (path_ptr + 1) % pathLength;
						prev[curr_x + 1][curr_y + 1] = 12;
						dist[curr_x + 1][curr_y + 1] = cost;
					}
				}
				return foundPath ? dist[curr_x][curr_y] : -1;
			} catch (Exception e) {
				return -1;
			}

		}
	}

	public static class Pathfinder {

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

		protected static Tile[] findPath(final Tile start, final Tile end) {
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

			if (!remote || Calculations.distance(Players.getLocal().getPosition(), end) < 10) {
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

	public static boolean walkToTile(Tile t) {
		Timer timeOut = new Timer(60000);
		LocalPath path = new LocalPath(t);
		Player local = Players.getLocal();
		path.step();
		while(timeOut.isRunning()){
			if(Walking.getDestination().getX()==-1||Calculations.distance(Walking.getDestination(),local.getPosition())<5){
				path.step();
			}
			if(Calculations.distance(local.getPosition(),t)<10)
				break;
			Time.sleep(300);
		}
		return Calculations.distance(local.getPosition(),t)<15;
	}
}
