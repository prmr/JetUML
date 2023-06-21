package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.validator.constraints.ClassDiagramSemanticConstraints;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

/**
 * Validator for class diagrams.
 */
public class ClassDiagramValidator extends AbstractDiagramValidator
{
	private static final SemanticConstraintSet SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
			EdgeSemanticConstraints.noteEdgeToPointMustStartWithNote(), 
			EdgeSemanticConstraints.noteNode(),
			EdgeSemanticConstraints.maxEdges(1), 
			ClassDiagramSemanticConstraints.noSelfGeneralization(),
			ClassDiagramSemanticConstraints.noSelfDependency(),
			ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(GeneralizationEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(AggregationEdge.class),
			ClassDiagramSemanticConstraints.noDirectCycles(AssociationEdge.class),
			ClassDiagramSemanticConstraints.noCombinedAssociationAggregation()
			);

	private static final Set<Class<? extends Node>> VALID_NODES_TYPES = Set.of(
			ClassNode.class, 
			InterfaceNode.class,
			PackageNode.class, 
			PackageDescriptionNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES_TYPES = Arrays.asList(
			DependencyEdge.class,
			GeneralizationEdge.class, 
			AssociationEdge.class, 
			AggregationEdge.class, 
			NoteEdge.class);

	/**
	 * Creates a new validator for a class diagram.
	 *
	 * @param pDiagram The diagram to validate
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public ClassDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODES_TYPES);
		assert pDiagram.getType() == DiagramType.CLASS;
	}

	@Override
	public SemanticConstraintSet edgeConstraints()
	{
		return SEMANTIC_CONSTRAINT_SET;
	}

	@Override
	protected List<Class<? extends Edge>> validEdgesTypes()
	{
		return VALID_EDGES_TYPES;
	}
}
