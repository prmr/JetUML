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
package ca.mcgill.cs.jetuml.viewers.edges;

import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge.Directionality;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;

/**
 * A straight solid line.
 */
public final class AssociationEdgeViewer extends SegmentedEdgeViewer
{	
	/**
	 * Creates a viewer for DependencyEdge instances.
	 */
	public AssociationEdgeViewer()
	{
		super(SegmentationStyleFactory.createHVHStrategy(),
				e -> LineStyle.SOLID, 
				e -> getStartArrowHead((AssociationEdge)e), 
				e -> getEndArrowHead((AssociationEdge)e),
				e -> ((AssociationEdge)e).getStartLabel(), 
				e -> ((AssociationEdge)e).getMiddleLabel(), 
				e -> ((AssociationEdge)e).getEndLabel());
	}
	
	/**
	 * @return The arrow end at the start of this edge.
	 */
	private static ArrowHead getStartArrowHead(AssociationEdge pEdge)
	{
		if( pEdge.getDirectionality() == Directionality.Bidirectional )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
	
	/**
	 * @return The arrow end at the end of this edge.
	 */
	private static ArrowHead getEndArrowHead(AssociationEdge pEdge)
	{
		if( pEdge.getDirectionality() == Directionality.Bidirectional || pEdge.getDirectionality() == Directionality.Unidirectional )
		{
			return ArrowHead.V;
		}
		else
		{
			return ArrowHead.NONE;
		}
	}
}