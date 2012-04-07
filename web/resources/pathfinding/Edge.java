package web.resources.pathfinding;

import web.types.Base.WebComponent;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 23:38
 */
public class Edge {

	private final WebComponent component;

	private boolean aToB = true;

	public Edge(final WebComponent component){
		this.component = component;
	}

	public WebComponent getComponent(){
		return component;
	}

	public double getWeight(){
		return component.getWeight();
	}

	public boolean isaToB() {
		return aToB;
	}

	public void setaToB(final boolean aToB) {
		this.aToB = aToB;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Edge){
			Edge edge = (Edge) o;
			return component.equals(edge.getComponent());
		}
		return false;
	}

	@Override
	public String toString(){
		return "Component: "+component.getA()+"<>"+component.getB()+"  Weight: "+component.getWeight()+" Can do: "+component.canDoAction()+" Direction: "+aToB;
	}
}
