package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;

/**
 * Constraint implementations for class diagrams.
 */
public final class ClassDiagramSemanticConstraints
{
	private ClassDiagramSemanticConstraints() {}

	/**
	 * Self edges are not allowed for Generalization edges.
	 */
	public static EdgeConstraint noSelfGeneralization()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == GeneralizationEdge.class && pEdge.start() == pEdge.end());
		};
	}

	/**
	 * Self edges are not allowed for Dependency edges.
	 */
	public static EdgeConstraint noSelfDependency()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == DependencyEdge.class && pEdge.start() == pEdge.end());
		};
	}

	/**
	 * There can't be two edges of a given type, one in each direction, between
	 * two DIFFERENT nodes.
	 */
	public static EdgeConstraint noDirectCycles(Class<? extends Edge> pEdgeType)
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			if( pEdge.getClass() != pEdgeType || pEdge.start() == pEdge.end() )
			{
				return true;
			}
			
			int sameDirectionCount = 0;
			for( Edge edge : pDiagram.edgesConnectedTo(pEdge.start()) )
			{
				if( edge.getClass() == pEdgeType && edge.end() == pEdge.start() && edge.start() == pEdge.end() )
				{
					sameDirectionCount += 1;
				}
			}
			
			return sameDirectionCount == 0;
		};
	}

	/**
	 * There can't be both an association and an aggregation edge between two
	 * nodes.
	 */
	public static EdgeConstraint noCombinedAssociationAggregation()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			int count = getAssociationAggregationCount(pEdge.start(), pEdge.end(), pDiagram);
			return count <= 1;
		};
	}

	/**
	 * Return a count of the number of association/aggregation edge between
	 * pStart and pEnd regardless of the direction of the edge.
	 *
	 * @param pStart starting node
	 * @param pEnd ending node
	 * @param pDiagram the diagram instance where pStart pEnd are in
	 * @return a count of the number of association/aggregation edge between
	 * pStart and pEnd
	 */
	private static int getAssociationAggregationCount(Node pStart, Node pEnd, Diagram pDiagram)
	{   // CSOFF:
		return (int) pDiagram.edges().stream()
				.filter(edge -> (edge.getClass() == AssociationEdge.class || edge.getClass() == AggregationEdge.class) &&
						(edge.start() == pStart && edge.end() == pEnd) || 
						(edge.start() == pEnd && edge.end() == pStart))
				.count();
		// CSON: 
	}
}
