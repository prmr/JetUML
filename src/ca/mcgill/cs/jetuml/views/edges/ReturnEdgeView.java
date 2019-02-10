/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.edges;

import java.util.function.Supplier;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;

/**
 * Labeled, straight edge with customized code to compute connection points.
 */
public final class ReturnEdgeView extends LabeledStraightEdgeView
{	
	/**
	 * Creates a new view.
	 * 
	 * @param pEdge The edge to wrap.
	 * @param pLabelSupplier A supplier for the edge's label.
	 */
	public ReturnEdgeView(Edge pEdge, Supplier<String> pLabelSupplier)
	{
		super(pEdge, LineStyle.DOTTED, ArrowHead.V, pLabelSupplier);
	}
	
	@Override
	public Line getConnectionPoints()
	{
		Rectangle start = edge().getStart().view().getBounds();
		Rectangle end = edge().getEnd().view().getBounds();
		
		if(edge().getEnd() instanceof PointNode) // show nicely in tool bar
		{
			return new Line(new Point(end.getX(), end.getY()), new Point(start.getMaxX(), end.getY()));
		}      
		else if(start.getCenter().getX() < end.getCenter().getX())
		{
			return new Line(new Point(start.getMaxX(), start.getMaxY()), new Point(end.getX(), start.getMaxY()));
		}
		else
		{
			return new Line(new Point(start.getX(), start.getMaxY()), new Point(end.getMaxX(), start.getMaxY()));
		}
	}
}