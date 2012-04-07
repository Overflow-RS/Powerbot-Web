package web.data;

import org.powerbot.game.api.wrappers.Tile;
import web.types.Base.WebComponent;
import web.types.Base.WebLine;
import web.types.Stairs;

/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 23:11
 */
public class Lumbridge {

	private static Stairs CASTLE_G_1 = new Stairs(new Tile(3207,3210,0),new Tile(3206,3209,1),36773,36774,"up","down",0,1) {
		@Override
		public boolean canDoAction() {
			return true;
		}
	};

	private static Stairs CASTLE_1_2 = new Stairs(new Tile(3206,3209,1),new Tile(3206,3210,2),36774,36775,"up","down",1,2) {
		@Override
		public boolean canDoAction() {
			return true;
		}
	};

	private static WebLine TO_BANK = new WebLine(new Tile(3206,3210,2), new Tile(3208, 3219, 2));


	public static WebComponent[] LUMBRIDGE_COMPS = {CASTLE_G_1,CASTLE_1_2,TO_BANK};
}
