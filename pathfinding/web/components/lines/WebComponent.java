package pathfinding.web.components.lines;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 23:32
 */
public class WebComponent {

	private final Tile      tileA;
	private final Tile      tileB;
	private final WebAction actionA;
	private final WebAction actionB;

	public WebComponent(final Tile tileA, final Tile tileB, final WebAction actionA, final WebAction actionB) {
		this.tileA = tileA;
		this.tileB = tileB;
		this.actionA = actionA;
		this.actionB = actionB;
	}

	public Tile getTileA() {
		return tileA;
	}

	public Tile getTileB() {
		return tileB;
	}

	public WebAction getActionA() {
		return actionA;
	}

	public WebAction getActionB() {
		return actionB;
	}
}
