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

import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge.Type;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;

/**
 * A straight solid line with diamond arrow decoration.
 */
public final class AggregationEdgeViewer extends SegmentedEdgeViewer
{	
	/**
	 * Creates a viewer for AggregationEdge instances.
	 */
	public AggregationEdgeViewer()
	{
		super(SegmentationStyleFactory.createHVHStrategy(),
				e -> LineStyle.SOLID, e -> getStartArrowHead((AggregationEdge)e), e -> ArrowHead.NONE,
				e -> ((AggregationEdge)e).getStartLabel(), 
				e -> ((AggregationEdge)e).getMiddleLabel(), 
				e -> ((AggregationEdge)e).getEndLabel());
	}
	
	/**
	 * @return The start arrow head
	 */
	private static ArrowHead getStartArrowHead(AggregationEdge pEdge)
	{
		if( pEdge.getType() == Type.Composition )
		{
			return ArrowHead.BLACK_DIAMOND;
		}
		else
		{
			return ArrowHead.DIAMOND;
		}
	}
}