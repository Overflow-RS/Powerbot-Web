package web.resources.pathfinding;

import web.types.Base.WebComponent;

import java.util.ArrayList;

/**
 * Author: Tom
 * Date: 02/04/12
 * Time: 23:49
 */
public class Graph {

	private final ArrayList<Vertex> vertexes;

	public Graph() {
		vertexes = new ArrayList<Vertex>();
	}

	public ArrayList<Vertex> getVertexes() {
		return vertexes;
	}

	public void addWebComponent(WebComponent component) {
		Vertex newA = new Vertex(component.getA());
		Vertex newB = new Vertex(component.getB());
		Vertex temp = getVertex(newA);
		if (temp == null) {
			newA.addEdge(new Edge(component));
			vertexes.add(newA);
		} else {
			vertexes.remove(temp);
			temp.addEdge(new Edge(component));
			vertexes.add(temp);
		}
		temp = getVertex(newB);
		if (temp == null) {
			newB.addEdge(new Edge(component));
			vertexes.add(newB);
		} else {
			vertexes.remove(temp);
			temp.addEdge(new Edge(component));
			vertexes.add(temp);
		}
	}

	public Vertex getVertex(Vertex vertex) {
		for (Vertex v : vertexes) {
			if (v.equals(vertex)) {
				return v;
			}
		}
		return null;
	}
}