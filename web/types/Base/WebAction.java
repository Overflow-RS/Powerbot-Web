package web.types.Base;

import org.powerbot.game.api.wrappers.Tile;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 17:14
 */
public abstract class WebAction implements WebComponent {

	private final Tile a;

	private final Tile b;

	public WebAction(final Tile a, final Tile b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Tile getA(){
		return a;
	}

	@Override
	public Tile getB(){
		return b;
	}

	@Override
	public boolean hasActionAtoB() {
		return true;
	}

	@Override
	public boolean hasActionBtoA() {
		return true;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof WebAction){
			WebAction component = (WebAction) o;
			return (component.getA().equals(a)&&component.getB().equals(b))||
					(component.getA().equals(b)&&component.getB().equals(a));
		}
		return false;
	}
}
