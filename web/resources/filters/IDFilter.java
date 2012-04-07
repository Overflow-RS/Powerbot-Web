package web.resources.filters;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.Npc;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.Location;

import java.util.Arrays;

/**
 * Author: Tom
 * Date: 31/03/12
 * Time: 22:18
 */
public class IDFilter implements Filter {

	private int[] intArray;

	public IDFilter(int[] intArray) {
		this.intArray = intArray;
		Arrays.sort(this.intArray);
	}

	public boolean accept(final Object o) {
		if (o instanceof Npc) {
			Npc npc = (Npc) o;
			return Arrays.binarySearch(intArray,npc.getId())>=0;
		}
		else if (o instanceof Item) {
			Item item = (Item) o;
			return Arrays.binarySearch(intArray,item.getId())>=0;

		}
		else if (o instanceof Location) {
			Location location = (Location) o;
			return Arrays.binarySearch(intArray,location.getId())>=0;
		}
		else if (o instanceof GroundItem) {
			GroundItem groundItem = (GroundItem) o;
			return Arrays.binarySearch(intArray,groundItem.getGroundItem().getId())>=0;
		}
		return false;
	}
}
