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
package org.jetuml.rendering.nodes;

import org.jetuml.diagram.Node;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramElementRenderer;

/**
 * Abstract strategy that describes objects that can draw and
 * compute various geometric properties of nodes.
 */
public interface NodeRenderer extends DiagramElementRenderer
{
   	/**
     * Get the best connection point to connect this node 
     * with another node. This should be a point on the boundary
     * of the shape of this node.
     * @param pNode The target node.
     * @param pDirection the direction from the center 
     *     of the bounding rectangle towards the boundary 
     * @return the recommended connection point
     * @pre pNode != null && pDirection != null
	 */
	Point getConnectionPoint(Node pNode, Direction pDirection);
	
	/**
	 * Activates the NodeStorage.
	 */
	void activateNodeStorage();
	
	/**
	 * Deactivates and clears the NodeStorage. 
	 */
	void deactivateAndClearNodeStorage();
}
