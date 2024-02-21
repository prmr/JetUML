package org.jetuml.diagram;

import org.jetuml.diagram.edges.ThreeLabelEdge;

public class EndLabelStrategy implements EdgeLabelStrategy {
	@Override
	public String of(ThreeLabelEdge pThreeLabelEdge) {
		return pThreeLabelEdge.getEndLabel();
	}
}
