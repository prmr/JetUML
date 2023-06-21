package org.jetuml.diagram.validator;

import java.util.HashSet;
import java.util.Set;

import org.jetuml.annotations.TemplateMethod;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

/**
 * Implementation of the general scaffolding for validating a diagram.
 */
abstract class AbstractDiagramValidator implements DiagramValidator
{
	private static final Set<Class<? extends Node>> UNIVERSAL_NODES_TYPES = 
			Set.of(PointNode.class, NoteNode.class);
	private static final Set<Class<? extends Edge>> UNIVERSAL_EDGES_TYPES = 
			Set.of(NoteEdge.class);
	
	private final Diagram aDiagram;
	private final Set<Class<? extends Node>> aValidNodeTypes = new HashSet<>();
	private final Set<Class<? extends Edge>> aValidEdgeTypes = new HashSet<>();

	/**
	 * Creates a validator for pDiagram.
	 *
	 * @param pDiagram The diagram that we want to validate
	 * @param pValidNodeTypes The node types valid for this diagram, in addition to the 
 	 *	universal nodes valid by default.
 	 * @param pValidEdgeTypes The edge types valid for this diagram, in addition to the 
 	 *	universal edges valid by default.
	 * @pre pDiagram != null && pValidNodeTypes != null && pValidEdgeTypes != null;
	 */
	protected AbstractDiagramValidator(Diagram pDiagram, Set<Class<? extends Node>> pValidNodeTypes, 
			Set<Class<? extends Edge>> pValidEdgeTypes)
	{
		assert pDiagram != null && pValidNodeTypes != null && pValidEdgeTypes != null;
		aDiagram = pDiagram;
		aValidNodeTypes.addAll(UNIVERSAL_NODES_TYPES);
		aValidNodeTypes.addAll(pValidNodeTypes);
		aValidEdgeTypes.addAll(UNIVERSAL_EDGES_TYPES);
		aValidEdgeTypes.addAll(pValidEdgeTypes);
	}

	@Override
	public final boolean isValid()
	{
		return hasValidStructure() && hasValidSemantics();
	}

	@Override
	@TemplateMethod
	public final boolean hasValidStructure()
	{
		return hasValidElementTypes() && hasValidNodes();
	}

	/**
	 * @return True iff the diagram respects all required semantic validation
	 * rules.
	 * @pre hasValidStructure()
	 */
	@Override
	public final boolean hasValidSemantics()
	{
		return aDiagram.edges().stream()
				.allMatch(edge -> edgeConstraints().satisfied(edge, aDiagram));
	}

	private boolean hasValidElementTypes()
	{
		return aDiagram.allNodes().stream()
					.allMatch(node -> aValidNodeTypes.contains(node.getClass())) &&
			   aDiagram.edges().stream()
			   		.allMatch(edge -> aValidEdgeTypes.contains(edge.getClass()));
	}

	/**
	 * Helper method to check whether any additional constraint on nodes (besides
	 * their type being allowed) is respected.
	 */
	protected boolean hasValidNodes()
	{
		return true;
	}

	protected abstract SemanticConstraintSet edgeConstraints();

	/**
	 * @return The diagram wrapped by this validator.
	 */
	public final Diagram diagram()
	{
		return aDiagram;
	}
}
