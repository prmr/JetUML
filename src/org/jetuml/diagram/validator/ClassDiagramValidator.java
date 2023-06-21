package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.AbstractPackageNode;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.TypeNode;
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

	private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(
			ClassNode.class, 
			InterfaceNode.class,
			PackageNode.class, 
			PackageDescriptionNode.class, 
			NoteNode.class, 
			PointNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
			DependencyEdge.class,
			GeneralizationEdge.class, 
			AssociationEdge.class, 
			AggregationEdge.class, 
			NoteEdge.class);

	/**
	 * Creates a new validator for one class diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public ClassDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram);
		assert pDiagram.getType() == DiagramType.CLASS;
	}

	/**
	 * All children nodes of PackageNode must be either TypeNode (ClassNode,
	 * InterfaceNode) or AbstractPackageNode.
	 *
	 */
	@Override
	protected boolean validNodeHierarchy()
	{
		boolean result = true;

		for( Node node : aDiagram.allNodes() )
		{
			if( node instanceof PackageNode container )
			{
				// if any of the child is not a valid child node, should return
				// false
				result = container.getChildren().stream().allMatch(this::validChild);
			}
		}
		return result;
	}

	private boolean validChild(Node pChild)
	{
		return pChild instanceof TypeNode || pChild instanceof AbstractPackageNode;
	}

	@Override
	public SemanticConstraintSet getEdgeConstraints()
	{
		return SEMANTIC_CONSTRAINT_SET;
	}

	@Override
	protected List<Class<? extends Node>> getValidNodeClasses()
	{
		return VALID_NODES;
	}

	@Override
	protected List<Class<? extends Edge>> getValidEdgeClasses()
	{
		return VALID_EDGES;
	}
}
