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

/**
 * Implementation of the general scaffolding for validating a diagram.
 */
abstract class AbstractDiagramValidator implements DiagramValidator
{
	private static final Set<Class<? extends Node>> UNIVERSAL_NODES_TYPES = 
			Set.of(PointNode.class, NoteNode.class);
	private static final Set<Class<? extends Edge>> UNIVERSAL_EDGES_TYPES = 
			Set.of(NoteEdge.class);
	private static final Set<EdgeConstraint> UNIVERSAL_CONSTRAINTS =
			Set.of(AbstractDiagramValidator::constraintValidNoteEdge);
	
	private final Diagram aDiagram;
	private final Set<Class<? extends Node>> aValidNodeTypes = new HashSet<>();
	private final Set<Class<? extends Edge>> aValidEdgeTypes = new HashSet<>();
	private final Set<EdgeConstraint> aConstraints = new HashSet<>();

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
			Set<Class<? extends Edge>> pValidEdgeTypes, Set<EdgeConstraint> pEdgeConstraints)
	{
		assert pDiagram != null && pValidNodeTypes != null && pValidEdgeTypes != null;
		
		aDiagram = pDiagram;
		
		aValidNodeTypes.addAll(UNIVERSAL_NODES_TYPES);
		aValidNodeTypes.addAll(pValidNodeTypes);
		
		aValidEdgeTypes.addAll(UNIVERSAL_EDGES_TYPES);
		aValidEdgeTypes.addAll(pValidEdgeTypes);
		
		aConstraints.addAll(UNIVERSAL_CONSTRAINTS);
		aConstraints.addAll(pEdgeConstraints);
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
				.allMatch(edge -> allConstraintsSatistifed(edge));
	}
	
	private boolean allConstraintsSatistifed(Edge pEdge)
	{
		return aConstraints.stream()
				.allMatch(constraint -> constraint.satisfied(pEdge, aDiagram));
	}

	private boolean hasValidElementTypes()
	{
		return aDiagram.allNodes().stream()
					.allMatch(node -> aValidNodeTypes.contains(node.getClass())) &&
			   aDiagram.edges().stream()
			   		.allMatch(edge -> aValidEdgeTypes.contains(edge.getClass()));
	}

	@TemplateMethod
	private boolean hasValidNodes()
	{
		return hasValidPointNodes() && hasValidDiagramNodes();
	}
	
	/**
	 * @return Point nodes must be connected to an edge
	 */
	private boolean hasValidPointNodes()
	{
		return diagram().rootNodes().stream()
			.filter(PointNode.class::isInstance)
			.allMatch(node -> diagram().edgesConnectedTo(node).iterator().hasNext());
	}
	
	/**
	 * Step method in the template method design pattern to allow 
	 * processing diagram-specific node validation.
	 */
	protected boolean hasValidDiagramNodes()
	{
		return true;
	}

	/**
	 * @return The diagram wrapped by this validator.
	 */
	public final Diagram diagram()
	{
		return aDiagram;
	}
	
	/*
	 * Validates that a note edge is semantically correct. A note edge can come in 
	 * two flavors:
	 * 1. From a note node to a point node
	 * 2. From any node except a note node or a point node to a note node
	 */
	private static boolean constraintValidNoteEdge(Edge pEdge, Diagram pDiagram)
	{
		if( pEdge.getClass() != NoteEdge.class )
		{
			return true;
		}
		if( pEdge.end().getClass() == PointNode.class )
		{
			return pEdge.start().getClass() == NoteNode.class;
		}
		return pEdge.start().getClass() != PointNode.class && pEdge.start().getClass() != NoteNode.class &&
				pEdge.end().getClass() == NoteNode.class;
	}
	
	/**
	 * Only pNumber of edges of the same type are allowed in one direction
	 * between two nodes.
	 */
	public static boolean constraintMaxOneEdgeOfGivenTypeBetweenNodes(Edge pEdge, Diagram pDiagram)
	{
		return numberOfEdges(pEdge, pDiagram) <= 1;
	}
	
	/*
	 * Returns the number of edges of type pType between pStart and pEnd
	 */
	private static int numberOfEdges(Edge pEdge, Diagram pDiagram)
	{
		assert pEdge != null && pDiagram != null;
		int result = 0;
		for( Edge edge : pDiagram.edges() )
		{
			if( edge.getClass() == pEdge.getClass() && edge.start() == pEdge.start() && edge.end() == pEdge.end() )
			{
				result++;
			}
		}
		return result;
	}
}
