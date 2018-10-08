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
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle;
import ca.mcgill.cs.jetuml.views.edges.SegmentedEdgeView;
import javafx.geometry.Point2D;

/**
 *   An edge that joins two call nodes.
 */
public final class CallEdge extends SingleLabelEdge
{
	private boolean aSignal;
	
	/**
	 * Creates a non-signal edge.
	 */
	public CallEdge()
	{
		setSignal(false);
	}
	
	@Override
	protected EdgeView generateView()
	{
		return new SegmentedEdgeView(this, createSegmentationStyle(), () -> LineStyle.SOLID,
				() -> ArrowHead.NONE, ()->getEndArrowHead(), ()->"", ()->getMiddleLabel(), ()->"");
	}
	
	/**
	 * @return The end arrow head for the edge. By default
	 * there is no arrow head.
	 */
	private ArrowHead getEndArrowHead()
	{
		if(aSignal)
		{
			return ArrowHead.HALF_V;
		}
		else
		{
			return ArrowHead.V;
		}
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("signal", () -> aSignal, pSignal -> aSignal = (boolean) pSignal);
	}
	
	/**
     * Gets the signal property.
     * @return true if this is a signal edge
	 */
	public boolean isSignal() 
	{ return aSignal; }

	/**
     * Sets the signal property.
     * @param pNewValue true if this is a signal edge
     */      
	public void setSignal(boolean pNewValue) 
	{ 
		aSignal = pNewValue; 
	}
	
	private SegmentationStyle createSegmentationStyle()
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
		ArrayList<Point2D> points = new ArrayList<>();
		Rectangle start = pEdge.getStart().view().getBounds();
		Rectangle end = pEdge.getEnd().view().getBounds();
      
		if(pEdge.getEnd() instanceof CallNode && ((CallNode)pEdge.getEnd()).getParent() == 
				((CallNode)pEdge.getStart()).getParent())
		{
			Point2D p = new Point2D(start.getMaxX(), end.getY() - CallNode.CALL_YGAP / 2);
			Point2D q = new Point2D(end.getMaxX(), end.getY());
			Point2D s = new Point2D(q.getX() + end.getWidth(), q.getY());
			Point2D r = new Point2D(s.getX(), p.getY());
			points.add(p);
			points.add(r);
			points.add(s);
			points.add(q);
		}
		else if(pEdge.getEnd() instanceof PointNode) // show nicely in tool bar
		{
			points.add(new Point2D(start.getMaxX(), start.getY()));
			points.add(new Point2D(end.getX(), start.getY()));
		}
		else     
		{
			Direction direction = new Direction(start.getX() - end.getX(), 0);
			Point endPoint = pEdge.getEnd().view().getConnectionPoint(direction);
         
			if(start.getCenter().getX() < endPoint.getX())
			{
				points.add(new Point2D(start.getMaxX(), endPoint.getY()));
			}
			else
			{
				points.add(new Point2D(start.getX(), endPoint.getY()));
			}
			points.add(Conversions.toPoint2D(endPoint));
		}
		return points.toArray(new Point2D[points.size()]);
	}
}


