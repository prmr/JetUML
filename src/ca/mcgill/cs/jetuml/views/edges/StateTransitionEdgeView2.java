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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.edges.StateTransitionEdge;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

/**
 * An edge view specialized for state transitions.
 * 
 * @author Martin P. Robillard
 */
public class StateTransitionEdgeView2 extends AbstractEdgeView2
{
	private static final int SELF_EDGE_OFFSET = 15;
	private static final int DEGREES_5 = 5;
	private static final int DEGREES_20 = 20;
	
	/**
	 * @param pEdge The edge to wrap.
	 */
	public StateTransitionEdgeView2(StateTransitionEdge pEdge) // should be labeled edge
	{
		super(pEdge);
	}
	
	@Override
	public StateTransitionEdge edge() // fix when edge hierarchy final
	{
		return (StateTransitionEdge)super.edge();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics) {}
	
	private Rectangle2D getLabelBounds()
	{
		if( isSelfEdge() )
		{
			return getSelfEdgeLabelBounds();
		}
		else
		{
			return getNormalEdgeLabelBounds();
		}
	}
	
	/*
	 *  Gets the bounds of the label text .
	 * @return the bounds of the label text
	 */
	private Rectangle2D getNormalEdgeLabelBounds()
	{
		return null;
	}   
	
	/*
  * Positions the label above the self edge, centered
  * in the middle of it.
  * @return the bounds of the label text
  */
	private Rectangle2D getSelfEdgeLabelBounds()
	{
		return null;
	}   

	@Override
	protected Shape getShape()
	{
		if( isSelfEdge() )
		{
			return getSelfEdgeShape();
		}
		else
		{
			return getNormalEdgeShape();
		}
	}
	
	private boolean isSelfEdge()
	{
		return edge().getStart() == edge().getEnd();
	}
	
	private Shape getSelfEdgeShape()
	{
		return null;
	}
	
	/** 
	 * @return An index that represents the position in the list of
	 * edges between the same start and end nodes. 
	 * @pre getGraph() != null
	 */
	private int getPosition()
	{
		assert edge().getGraph() != null;
		int lReturn = 0;
		for( Edge edge : edge().getGraph().getEdges(edge().getStart()))
		{
			if( edge.getStart() == edge().getStart() && edge.getEnd() == edge().getEnd())
			{
				lReturn++;
			}
			if( edge == edge() )
			{
				return lReturn;
			}
		}
		assert lReturn > 0;
		return lReturn;
	}
	
	/*
	 * The connection points for the self-edge are an offset from the top-right
	 * corner.
	 */
	private Line getSelfEdgeConnectionPoints()
	{
		if( getPosition() == 1 )
		{
			Point2D.Double point1 = new Point2D.Double(edge().getStart().view().getBounds().getMaxX() - SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			Point2D.Double point2 = new Point2D.Double(edge().getStart().view().getBounds().getMaxX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			return new Line(Conversions.toPoint(point1),
					Conversions.toPoint(point2));
		}
		else
		{
			Point2D.Double point1 = new Point2D.Double(edge().getStart().view().getBounds().getX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			Point2D.Double point2 = new Point2D.Double(edge().getStart().view().getBounds().getX() + SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			return new Line(Conversions.toPoint(point1), 
					Conversions.toPoint(point2));
		}
	}
	
	private Shape getNormalEdgeShape()
	{
		return null;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return super.getBounds().add(Conversions.toRectangle(getLabelBounds()));
	}
	
	@Override
	public Line getConnectionPoints()
	{
		if(isSelfEdge())
		{
			return getSelfEdgeConnectionPoints();
		}
		else
		{
			return getNormalEdgeConnectionsPoints();
		}
	}
	
	/*
	 * The connection points are a slight offset from the center.
	 * @return
	 */
	private Line getNormalEdgeConnectionsPoints()
	{
		Rectangle start = edge().getStart().view().getBounds();
		Rectangle end = edge().getEnd().view().getBounds();
		Point startCenter = start.getCenter();
		Point endCenter = end.getCenter();
		int turn = DEGREES_5;
		if( edge().getGraph() != null && getPosition() > 1 )
		{
			turn = DEGREES_20;
		}
		Direction d1 = new Direction(startCenter, endCenter).turn(-turn);
		Direction d2 = new Direction(endCenter, startCenter).turn(turn);
		return new Line(edge().getStart().view().getConnectionPoint(d1), edge().getEnd().view().getConnectionPoint(d2));
	}	
}
