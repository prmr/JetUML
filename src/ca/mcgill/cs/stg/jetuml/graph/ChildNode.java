package ca.mcgill.cs.stg.jetuml.graph;

/**
 * Node that potentially has a parent node 
 * according to a diagram type-specific parent-child
 * relation. A child node is defined as a child that
 * cannot exist without its parent according to the rules
 * of UML. Child nodes are controlled by their parent nodes.
 * See ParentNode for details.
 * 
 * @author Martin P. Robillard
 *
 */
public interface ChildNode extends Node
{
	/**
	 * @return The node that is the parent of this node.
	 */
	ParentNode getParent();
	
	/**
	 * Sets the parent of this node. This operation does 
	 * NOT set the child node's parent as this node.
	 * 
	 * @param pParentNode The node to set as parent of this node.
	 */
	void setParent(ParentNode pParentNode);
}
