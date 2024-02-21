package org.jetuml.diagram;

import org.jetuml.diagram.edges.ThreeLabelEdge;

public class StartLabelStrategy implements EdgeLabelStrategy {
	@Override
	public String of(ThreeLabelEdge pThreeLabelEdge) {
		return pThreeLabelEdge.getStartLabel();
	}
}
