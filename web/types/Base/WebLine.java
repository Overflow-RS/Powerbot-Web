package web.types.Base;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.wrappers.Tile;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 16:56
 */
public class WebLine implements WebComponent {

	private final Tile a;

	private final Tile b;

	public WebLine(final Tile a, final Tile b) {
		this.a = a;
		this.b = b;
	}

	public Tile getA() {
		return a;
	}

	public Tile getB() {
		return b;
	}

	@Override
	public boolean hasActionAtoB() {
		return false;
	}

	@Override
	public boolean hasActionBtoA() {
		return false;
	}

	@Override
	public boolean canDoAction() {
		return true;
	}

	@Override
	public boolean doActionAtoB() {
		return false;
	}

	@Override
	public boolean doActionBtoA() {
		return false;
	}

	@Override
	public double getWeight() {
		return Calculations.distance(a,b);
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof WebLine){
			WebLine component = (WebLine) o;
			return (component.getA().equals(a)&&component.getB().equals(b))||
					(component.getA().equals(b)&&component.getB().equals(a));
		}
		return false;
	}
}
