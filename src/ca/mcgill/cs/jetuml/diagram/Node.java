/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram;

import java.util.Optional;

import ca.mcgill.cs.jetuml.geom.Point;

/**
  * A node in a diagram.
  */
public interface Node extends DiagramElement
{
	/**
	 * @return The position of this node. Usually corresponds to the top left corner 
	 * of its bounding box.
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
	 * or empty() if the node is not attached.
	 */
	Optional<Diagram> getDiagram();
}
