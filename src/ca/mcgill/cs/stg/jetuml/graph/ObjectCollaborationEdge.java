/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

/**
 * @author Martin P. Robillard
 */

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  An edge that that represents a dynamic collaboration between
 *  two objects in an object diagram, with optional labels.
 */
public class ObjectCollaborationEdge extends ClassRelationshipEdge
{
	/**
	 * Creates an association edge.
	 */
	public ObjectCollaborationEdge()
	{}
	
	@Override
	public ArrayList<Point2D> getPoints()
	{
		return BentStyle.STRAIGHT.getPath(getStart().getBounds(), getEnd().getBounds());
   }
}
