package org.jetuml.diagram;

public class EndNodeStrategy implements EdgeBoundStrategy {
	public Node of(Edge pEdge) {
		return pEdge.end();
	}
}
