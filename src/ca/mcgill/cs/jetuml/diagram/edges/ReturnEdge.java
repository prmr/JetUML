/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.edges;

import java.util.ArrayList;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle;
import ca.mcgill.cs.jetuml.views.edges.SegmentedEdgeView;
import javafx.geometry.Point2D;

/**
 *  An edge that joins two call nodes.
 */
public final class ReturnEdge extends SingleLabelEdge
{
	@Override
	protected EdgeView generateView()
	{
		return new SegmentedEdgeView(this, createSegmentationStyle2(), () -> LineStyle.DOTTED,
				() -> ArrowHead.NONE, ()->ArrowHead.V, ()->"", ()->getMiddleLabel(), ()->"");
	}
	
	private SegmentationStyle createSegmentationStyle2()
	{
		return new SegmentationStyle()
		{
			@Override
			public boolean isPossible(Edge pEdge)
			{
				assert false; // Should not be called.
				return false;
			}

			@Override
			public Point2D[] getPath(Edge pEdge, Diagram pGraph)
			{
				return getPoints(pEdge);
			}

			@Override
			public Side getAttachedSide(Edge pEdge, Node pNode)
			{
				assert false; // Should not be called
				return null;
			}
		};
	}
	
	private static Point2D[] getPoints(Edge pEdge)
	{
		ArrayList<Point2D> lReturn = new ArrayList<>();
		Rectangle start = pEdge.getStart().view().getBounds();
		Rectangle end = pEdge.getEnd().view().getBounds();
		if(pEdge.getEnd() instanceof PointNode) // show nicely in tool bar
		{
			lReturn.add(new Point2D(end.getX(), end.getY()));
			lReturn.add(new Point2D(start.getMaxX(), end.getY()));
		}      
		else if(start.getCenter().getX() < end.getCenter().getX())
		{
			lReturn.add(new Point2D(start.getMaxX(), start.getMaxY()));
			lReturn.add(new Point2D(end.getX(), start.getMaxY()));
		}
		else
		{
			lReturn.add(new Point2D(start.getX(), start.getMaxY()));
			lReturn.add(new Point2D(end.getMaxX(), start.getMaxY()));
		}
		return lReturn.toArray(new Point2D[lReturn.size()]);
	}
}
