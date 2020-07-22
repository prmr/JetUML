/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram;

import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.geom.Point;

/**
 * A node in a diagram.
 * 
 * Different node subclasses can potentially implement the 
 * child node and parent mixin behavior, depending on the needs
 * of the diagram type in which nodes participants.
 * 
 * Nodes that can act as child nodes can potentially be linked to a parent node. 
 * 
 * Nodes that can act as parent nodes can potentially have children nodes according 
 * to a diagram type-specific parent-child relation. Parent nodes control their
 * child nodes. If a child node has a parent, only the parent node is tracked by
 * the diagram object. If a parent node is removed from the diagram, all its children
 * are also removed. Cloning a parent node clones all the children, and similarly
 * with all other operations, including copying, translating, etc.
 */
public interface Node extends DiagramElement
{
	/**
	 * @return The position of this node. Usually corresponds to the top left corner 
	 *     of its bounding box.
	 */
	Point position();

	/**
	 * Move the position of the node to pPoint.
	 * 
	 * @param pPoint The new position of the node.
	 */
	void moveTo(Point pPoint);

	/**
     * Translates the node by a given amount.
     * @param pDeltaX the amount to translate in the x-direction
     * @param pDeltaY the amount to translate in the y-direction
	 */
	void translate(int pDeltaX, int pDeltaY);

	/**
	 * @return A clone of the node.
	 */
	Node clone();
	
	/**
	 * Attaches this node to a diagram.
	 * 
	 * @param pDiagram The diagram attached to the node.
	 * @pre pDiagram != null
	 */
	void attach(Diagram pDiagram);
	
	/**
	 * Detaches this node from its current diagram.
	 */
	void detach();
	
	/**
	 * @return The diagram this node is attached to,
	 *     or empty() if the node is not attached.
	 */
	Optional<Diagram> getDiagram();
	
	/**
	 * @return True if this node is linked to a parent node.
	 */
	boolean hasParent();
	
	/**
	 * @return true if this node type requires a parent to exist, 
	 *     and false if it can exist as a root node.
	 */
	boolean requiresParent();	
	
	/**
	 * @return The node that is the parent of this node. Never null.
	 * @pre hasParent()
	 */
	Node getParent();
	
	/**
	 * Unlinks this node from it parent node.. This operation does 
	 * NOT set the child node's parent as this node.
	 * 
	 * @pre hasParent();
	 */
	void unlink();
	
	/**
	 * Links this node to a parent node This operation does 
	 * NOT set the child node's parent as this node.
	 * 
	 * By default this operation is not supported. It is the 
	 * responsibility of the Builder class to ensure that nodes
	 * can only be linked to a parent if it respects the semantics
	 * of the diagram.
	 * 
	 * @param pParentNode The node to set as parent of this node.
	 * @pre pParentNode != null
	 */
	void link(Node pParentNode);
	
	/**
	 * Returns the children of this node. This operation is legal 
	 * on node types than cannot have children, in which case it
	 * always returns an empty list.
	 * 
	 * @return An unmodifiable list of children nodes.
	 */
	List<Node> getChildren(); 
	
	/**
	 * @return true if the type of node supports keeping track
	 *     of children nodes.
	 */
	boolean allowsChildren();
	
	/**
	 * Insert a child at the end of the list of children.
	 * This method also links the current node as parent
	 * node of pNode.
	 * @param pNode The child to insert.
	 * @pre allowsNode() == true && pNode != null
	 */
	void addChild(Node pNode);
	
	/**
	 * Insert a child node at index pIndex.
	 * This method also links the current node as parent
	 * node of pNode.
	 * @param pIndex Where to insert the child.
	 * @param pNode The child to insert.
	 * @pre pNode != null && pIndex <= getChildren().size()
	 * @pre allowsChildren()
	 */
	void addChild(int pIndex, Node pNode); 
	
	/**
	 * Remove pNode from the list of children of this node.
	 * Also unlinks the child's parent node from this node.
	 * @param pNode The child to remove.
	 * @pre getChildren().contains(pNode)
	 * @pre pNode.getParent() == this
	 */
	void removeChild(Node pNode);
	
	/**
	 * Move the child node to the last position in the list of children.
	 * @param pChild The child to move
	 * @pre pChild != null && getChildren().contains(pChild)
	 */
	void placeLast(Node pChild);
}
