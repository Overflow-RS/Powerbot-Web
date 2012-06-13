package pathfinding.web.data.banking;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.data.banking.wrappers.BANK_TYPE;
import pathfinding.web.data.banking.wrappers.WebBank;

/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 20:47
 */
public class Banks {

	public static WebBank VARROCK_EAST = new WebBank("Varrock East", new Tile(3253, 3419, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank VARROCK_WEST = new WebBank("Varrock West", new Tile(3187, 3438, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank EDGEVILLE    = new WebBank("Edgeville", new Tile(3093, 3492, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank DRAYNOR      = new WebBank("Draynor", new Tile(3093, 3245, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};
	public static WebBank FALADOR_EAST = new WebBank("Falador East", new Tile(3014, 3355, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank FALADOR_WEST = new WebBank("Falador West", new Tile(2945, 3368, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank GRAND_EXCHANGE = new WebBank("Grand Exchange", new Tile(3160, 3490, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank AL_KHARID = new WebBank("Al Kharid", new Tile(3269, 3167, 0), false, BANK_TYPE.NPC) {
		@Override
		public boolean canAccess() {
			return true;
		}
	};

	public static WebBank[] bankArray =
			{VARROCK_EAST, VARROCK_WEST, EDGEVILLE, DRAYNOR, FALADOR_EAST,
			 FALADOR_WEST, GRAND_EXCHANGE,AL_KHARID};

}
