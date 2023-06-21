package org.jetuml.diagram.validator;

import org.jetuml.diagram.Node;

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
	 * The structure of a diagram consists of the type of nodes and edges
	 * and any additional constraints on the nodes. This method does 
	 * not validate the parent-child relation between nodes because 
	 * the API for creating nodes does not allow invalid relations
	 * (see {@link Node#allowsAsChild(Node)}). This validations must thus 
	 * be done before a child is linked to a parent. Diagrams
	 * are assumed to respect the invariant that a link between a child
	 * and a parent is always valid.
	 * 
	 * @return True iff the diagram is well-formed.
	 */
	boolean hasValidStructure();
	
	/**
	 * The semantic rules validated by this method concern the legality of 
	 * edge connections.
	 * 	  
	 * @return True iff the diagram respects all required semantic validation rules.
	 * @pre hasValidStructure()
	 */
	boolean hasValidSemantics();
}
