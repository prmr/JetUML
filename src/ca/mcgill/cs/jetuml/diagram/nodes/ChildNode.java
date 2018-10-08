/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * Node that potentially has a parent node 
 * according to a diagram type-specific parent-child
 * relation. A child node is defined as a child that
 * cannot exist without its parent according to the rules
 * of UML. Child nodes are controlled by their parent nodes.
 * See ParentNode for details.
 */
public interface ChildNode extends Node
{
	/**
	 * @return True if this node type absolutely
	 * needs a parent to exist, and false if it can
	 * exist as a root node.
	 */
	boolean requiresParent();	
	
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
