/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * A strategy for drawing a segmented line between two nodes.
 * 
 * @author Martin P. Robillard
 *
 */
public interface SegmentationStyle
{
	/**
     * Gets the points at which a line joining two nodes
     * is bent according to this strategy.
     * @param pStart the starting node
     * @param pEnd the ending node
     * @return an array list of points at which to bend the
     * segmented line joining the two nodes
	 */
	Point2D[] getPath(Node pStart, Node pEnd);
}
