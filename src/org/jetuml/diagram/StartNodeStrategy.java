package org.jetuml.diagram;

public class StartNodeStrategy implements EdgeBoundStrategy {
	public Node of(Edge pEdge) {
		return pEdge.start();
	}
}
