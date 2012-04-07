package web.types.Base;

import org.powerbot.game.api.wrappers.Tile;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 16:56
 */
public interface WebComponent {

	public boolean hasActionAtoB();

	public boolean hasActionBtoA();

	public boolean canDoAction();

	public boolean doActionAtoB();

	public boolean doActionBtoA();

	public double getWeight();

	public Tile getA();

	public Tile getB();

	public boolean equals(Object o);

}
