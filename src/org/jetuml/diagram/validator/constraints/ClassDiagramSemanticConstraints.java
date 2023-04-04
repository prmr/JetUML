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
	public static SemanticConstraint noSelfGeneralization()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == GeneralizationEdge.class && pEdge.getStart() == pEdge.getEnd());
		};
	}

	/**
	 * Self edges are not allowed for Dependency edges.
	 */
	public static SemanticConstraint noSelfDependency()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == DependencyEdge.class && pEdge.getStart() == pEdge.getEnd());
		};
	}

	/**
	 * There can't be two edges of a given type, one in each direction, between
	 * two nodes.
	 */
	public static SemanticConstraint noDirectCycles(Class<? extends Edge> pEdgeType)
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			if( pEdge.getClass() != pEdgeType )
			{
				return true;
			}
			
			int sameDirectionCount = 0;
			for( Edge edge : pDiagram.edgesConnectedTo(pEdge.getStart()) )
			{
				if( edge.getClass() == pEdgeType && edge.getEnd() == pEdge.getStart() && edge.getStart() == pEdge.getEnd() )
				{
					sameDirectionCount += 1;
				}
			}
			
			return sameDirectionCount < 2;
		};
	}

	/**
	 * There can't be both an association and an aggregation edge between two
	 * nodes.
	 */
	public static SemanticConstraint noCombinedAssociationAggregation()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			int count = getAssociationAggregationCount(pEdge.getStart(), pEdge.getEnd(), pDiagram);
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
						(edge.getStart() == pStart && edge.getEnd() == pEnd) || 
						(edge.getStart() == pEnd && edge.getEnd() == pStart))
				.count();
		// CSON: 
	}
}
