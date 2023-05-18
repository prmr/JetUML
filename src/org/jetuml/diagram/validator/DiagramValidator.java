package org.jetuml.diagram.validator;

/**
 * A type that allows to check the Diagram's semantic validity.
 */
public interface DiagramValidator
{
	/**
	 * Should always return hasValidStructure() && hasValidSemantics().
	 * 
	 * @return True if the content of the diagram does not violate
	 * any structural or semantic rules. 
	 */
	boolean isValid();
	
	/**
	 * @return True iff the diagram is well-formed.
	 */
	boolean hasValidStructure();
	
	/**
	 * @return True iff the diagram respects all required semantic validation rules.
	 * @pre hasValidStructure()
	 */
	boolean hasValidSemantics();
}
