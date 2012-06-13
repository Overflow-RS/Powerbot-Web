package pathfinding.astar.types;

/**
 * Author: Tom
 * Date: 11/06/12
 * Time: 22:26
 */
public abstract class Edge {

	private final Node source;
	private final Node target;

	protected Edge(final Node source, final Node target) {
		this.source = source;
		this.target = target;
	}

	public abstract boolean canUse();
	public abstract double getCost();

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

}
