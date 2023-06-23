package org.jetuml.diagram.validator;

import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;

/**
 * Validator for class diagrams.
 */
public class ClassDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			AbstractDiagramValidator.createConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(1),
			AbstractDiagramValidator.createConstraintNoSelfEdgeForEdgeType(GeneralizationEdge.class),
			AbstractDiagramValidator.createConstraintNoSelfEdgeForEdgeType(DependencyEdge.class),
			AbstractDiagramValidator.createConstraintNoDirectCyclesForEdgeType(DependencyEdge.class),
			AbstractDiagramValidator.createConstraintNoDirectCyclesForEdgeType(GeneralizationEdge.class),
			AbstractDiagramValidator.createConstraintNoDirectCyclesForEdgeType(AggregationEdge.class),
			AbstractDiagramValidator.createConstraintNoDirectCyclesForEdgeType(AssociationEdge.class),
			ClassDiagramValidator::constraintNoCombinedAssociationAggregation);

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ClassNode.class, 
			InterfaceNode.class,
			PackageNode.class, 
			PackageDescriptionNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			DependencyEdge.class,
			GeneralizationEdge.class, 
			AssociationEdge.class, 
			AggregationEdge.class);

	/**
	 * Creates a new validator for a class diagram.
	 *
	 * @param pDiagram The diagram to validate
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public ClassDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.CLASS;
	}
	
	/**
	 * There can't be both an association and an aggregation edge between two
	 * nodes.
	 */
	public static boolean constraintNoCombinedAssociationAggregation(Edge pEdge, Diagram pDiagram)
	{
		return pDiagram.edges().stream()
				.filter(ClassDiagramValidator::isAssociationOrAggregation)
				.filter(edge -> isBetweenSameNodes(edge, pEdge))
				.count() <= 1;
	}
	
	/*
	 * Aggregation edges and association edges are in the same category
	 */
	private static boolean isAssociationOrAggregation(Edge pEdge)
	{
		return pEdge.getClass() == AssociationEdge.class || pEdge.getClass() == AggregationEdge.class;
	}
	
	/*
	 * Irrespective of direction
	 */
	private static boolean isBetweenSameNodes(Edge pEdge1, Edge pEdge2)
	{
		return pEdge1.start() == pEdge2.start() && pEdge1.end() == pEdge2.end() ||
				pEdge1.start() == pEdge2.end() && pEdge1.end() == pEdge2.start();
	}
}
