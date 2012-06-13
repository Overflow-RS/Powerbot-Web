package pathfinding.web.components.base;

import pathfinding.astar.types.Edge;
import pathfinding.astar.types.Graph;
import pathfinding.web.components.actions.WebAction;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 17:14
 */
public class WebEdge extends Edge {

	private final WebAction action;

	public WebEdge(final Graph graph, final WebNode source, final WebNode target, WebAction action) {
		super(source,target);
		this.action = action;
	}

	public WebAction getAction() {
		return action;
	}

	@Override
	public boolean canUse() {
		return action.canDoAction();
	}

	@Override
	public double getCost() {
		return action.getCost();
	}

	public boolean doAction() {
		return action.doAction();
	}

	@Override
	public String toString(){
		if(getAction()!=null){
			return getAction()+" : "+ getSource().toString()+" > " + getTarget().toString();
		}
		return "Default"+" : "+ getSource().toString()+" > " + getTarget().toString();
	}

}
