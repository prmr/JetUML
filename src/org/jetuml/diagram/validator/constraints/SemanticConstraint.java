package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;

public interface SemanticConstraint 
{
	boolean satisfied(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram);
}
