package org.jetuml.diagram.validator;

import java.util.List;

import org.jetuml.annotations.TemplateMethod;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

/**
 * Implementation of the general scaffolding for validating a diagram.
 */
abstract class AbstractDiagramValidator implements DiagramValidator
{
	private final Diagram aDiagram;

	/**
	 * Creates a validator for pDiagram.
	 *
	 * @param pDiagram The diagram that we want to validate
	 * @pre pDiagram != null;
	 */
	protected AbstractDiagramValidator(Diagram pDiagram)
	{
		assert pDiagram != null;
		aDiagram = pDiagram;
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

	/**
	 * @return The list of all node classes allowed for this diagram.
	 */
	protected abstract List<Class<? extends Node>> validNodeTypes();

	/**
	 * @return The list of all edge classes allowed for this diagram.
	 */
	protected abstract List<Class<? extends Edge>> validEdgesTypes();

	private boolean hasValidElementTypes()
	{
		return aDiagram.allNodes().stream()
					.allMatch(node -> validNodeTypes().contains(node.getClass())) &&
			   aDiagram.edges().stream()
			   		.allMatch(edge -> validEdgesTypes().contains(edge.getClass()));
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
