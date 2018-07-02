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

package ca.mcgill.cs.jetuml.diagram.builder;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Point;

public class DiagramBuilder
{
	protected final Diagram aDiagram;
	
	public DiagramBuilder( Diagram pDiagram )
	{
		aDiagram = pDiagram;
	}
	
	/**
	 * True by default. Override to provide cases where this should be false.
	 * 
	 * @param pNode The node to add if possible. 
	 * @param pRequestedPosition The requested position for the node.
	 * @return True if it is possible to add pNode at position pPoint.
	 */
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		return true;
	}
}
