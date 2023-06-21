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
import org.jetuml.diagram.validator.constraints.ClassDiagramSemanticConstraints;

/**
 * Validator for class diagrams.
 */
public class ClassDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			ClassDiagramSemanticConstraints.noSelfGeneralization(),
			ClassDiagramSemanticConstraints.noSelfDependency(),
			ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(GeneralizationEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(AggregationEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(AssociationEdge.class),
			ClassDiagramSemanticConstraints.noCombinedAssociationAggregation());

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
}
