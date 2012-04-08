package web.walking;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.h;
import org.powerbot.game.client.*;

/**
 * Author: Tom
 * Date: 08/04/12
 * Time: 15:58
 */
	public class WalkingUtil {

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