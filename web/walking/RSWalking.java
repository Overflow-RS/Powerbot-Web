package web.walking;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Player;
import web.walking.paths.LocalPath;


/**
 * Author: Tom
 * Date: 07/04/12
 * Time: 22:00
 */
public class RSWalking {

	private static int enableRun = 40;

	public static boolean walkToTile(Tile t) {
		Timer timeOut = new Timer(30000);
		LocalPath path = new LocalPath(t);
		Player local = Players.getLocal();
		while (timeOut.isRunning()) {
			if (Calculations.distance(local.getPosition(), t) < 15) {
				break;
			}
			if (!local.isMoving() || Calculations.distance(Walking.getDestination(), local.getPosition()) < 6) {
				path.step();
			}
			if(!Walking.isRunEnabled()&&Walking.getEnergy()>=enableRun){
				Walking.setRun(true);
				for(int i = 0; i<10;i++){
					if(Walking.isRunEnabled())
						break;
					Time.sleep(300);
				}
			}
			Time.sleep(300);
		}
		return Calculations.distance(local.getPosition(), t) < 15;
	}

}
