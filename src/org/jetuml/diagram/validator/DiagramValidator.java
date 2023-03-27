package org.jetuml.diagram.validator;

/**
 * A type that allows to check the Diagram's semantic validity.
 */
public interface DiagramValidator
{
	/**
	 * @return True if the content of the diagram does not violate
	 * any structural or semantic rules.
	 */
	boolean isDiagramValid();
}
