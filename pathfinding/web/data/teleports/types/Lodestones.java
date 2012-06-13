package pathfinding.web.data.teleports.types;

import org.powerbot.game.api.wrappers.Tile;
import pathfinding.web.components.actions.teleports.LodestoneAction;
import pathfinding.web.components.lines.WebTeleport;

/**
 * Author: Tom
 * Date: 12/06/12
 * Time: 16:00
 */
public class Lodestones {

	public static WebTeleport LUMBRIDGE_LODESTONE = new WebTeleport(new Tile(3233, 3211, 0), new LodestoneAction("Lumbridge lodestone",new Tile(3233, 3211, 0), 47));
	public static WebTeleport FALADOR_LODESTONE = new WebTeleport(new Tile(2965,3403, 0), new LodestoneAction("Falador lodestone",new Tile(2965,3403, 0), 46));
	public static WebTeleport AL_KHARID_LODESTONE = new WebTeleport(new Tile(3297,3184, 0), new LodestoneAction("Al-Kharid lodestone",new Tile(3297,3184, 0), 40));
	public static WebTeleport DRAYNOR_LODESTONE = new WebTeleport(new Tile(3105,3298, 0), new LodestoneAction("Draynor lodestone",new Tile(3105,3298, 0), 44));
	public static WebTeleport VARROCK_LODESTONE = new WebTeleport(new Tile(3214,3376, 0), new LodestoneAction("Varrock lodestone",new Tile(3214,3376, 0), 51));

	public static WebTeleport[] ARRAY = {LUMBRIDGE_LODESTONE,FALADOR_LODESTONE,AL_KHARID_LODESTONE,DRAYNOR_LODESTONE,VARROCK_LODESTONE};

}
