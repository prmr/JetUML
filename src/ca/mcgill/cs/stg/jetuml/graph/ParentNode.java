package ca.mcgill.cs.stg.jetuml.graph;

import java.util.List;

/**
 * Node that potentially has children nodes
 * according to a diagram type-specific parent-child
 * relation. Parent nodes control their child nodes.
 * If a ChildNode has a parent, only the parent node
 * is tracked by the graph object. If a parent node is 
 * removed from the graph, all its children are also removed. 
 * Cloning a parent node clones all the children, and similarly
 * with all other operations, including copying, translating, etc.
 * 
 * @author Martin P. Robillard
 *
 */
public interface ParentNode extends Node
{
	/**
	 * @return A list of the children of this node.
	 */
	List<ChildNode> getChildren(); 
	
	/**
	 * Insert a child node at index pIndex.
	 * @param pIndex Where to insert the child.
	 * @param pNode The child to insert.
	 */
	void addChild(int pIndex, ChildNode pNode); 
	
	/**
	 * Insert a child at the end of the list of children.
	 * @param pNode The child to insert.
	 */
	void addChild(ChildNode pNode);
	
	/**
	 * Remove pNode from the list of children of this node.
	 * @param pNode The child to remove.
	 */
	void removeChild(ChildNode pNode);
}
