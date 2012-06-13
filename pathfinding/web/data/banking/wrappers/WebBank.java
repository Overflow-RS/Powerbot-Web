package pathfinding.web.data.banking.wrappers;

import org.powerbot.game.api.wrappers.Tile;

/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 20:42
 */
public abstract class WebBank {

	private final String    name;
	private final Tile      tile;
	private final BANK_TYPE type;
	private final boolean   members;

	public WebBank(String name, final Tile tile, boolean members, BANK_TYPE type) {
		this.name = name;
		this.tile = tile;
		this.members = members;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Tile getTile() {
		return tile;
	}

	public BANK_TYPE getType() {
		return type;
	}

	public boolean isMembers() {
		return members;
	}

	public abstract boolean canAccess();

	@Override
	public String toString() {
		return name;
	}

}