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

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * An S- or C-shaped edge with an arrowhead.
 */
public final class ObjectReferenceEdgeView extends AbstractEdgeView
{
	private static final int ENDSIZE = 10;
	
	/**
	 * @param pEdge the edge to wrap.
	 */
	public ObjectReferenceEdgeView(Edge pEdge)
	{
		super(pEdge);
	}
	
	@Override
	protected Shape getShape()
	{
		if(isSShaped())
		{
			return getSShape(getConnectionPoints());
		}
		else
		{
			return getCShape(getConnectionPoints());
		}			
	}
	
	private Path getSShape(Line pConnectionPoints)
	{
		final int x1 = pConnectionPoints.getX1() + ENDSIZE;
		final int y1 = pConnectionPoints.getY1();
		final int x2 = pConnectionPoints.getX2() - ENDSIZE;
		final int y2 = pConnectionPoints.getY2();
		final int xmid = (pConnectionPoints.getX1() + pConnectionPoints.getX2()) / 2;
		final int ymid = (pConnectionPoints.getY1() + pConnectionPoints.getY2()) / 2;
     
		MoveTo moveTo = new MoveTo(pConnectionPoints.getX1(), y1);
		LineTo lineTo1 = new LineTo(x1, y1);
		QuadCurveTo quadTo1 = new QuadCurveTo((x1 + xmid) / 2, y1, xmid, ymid);
		QuadCurveTo quadTo2 = new QuadCurveTo((x2 + xmid) / 2, y2, x2, y2);
		LineTo lineTo2 = new LineTo(pConnectionPoints.getX2(), y2);
		
		Path path = new Path();
		path.getElements().addAll(moveTo, lineTo1, quadTo1, quadTo2, lineTo2);
		return path;
	}
	
	private Path getCShape(Line pConnectionPoints)
	{
		final int x1 = Math.max(pConnectionPoints.getX1(), pConnectionPoints.getX2()) + ENDSIZE;
		final int y1 = pConnectionPoints.getY1();
		final int x2 = x1 + ENDSIZE;
		final int y2 = pConnectionPoints.getY2();
		final int ymid = (pConnectionPoints.getY1() + pConnectionPoints.getY2()) / 2;
		
		MoveTo moveTo = new MoveTo(pConnectionPoints.getX1(), y1);
		LineTo lineTo1 = new LineTo(x1, y1);
		QuadCurveTo quadTo1 = new QuadCurveTo(x2, y1, x2, ymid);
		QuadCurveTo quadTo2 = new QuadCurveTo(x2, y2, x1, y2);
		LineTo lineTo2 = new LineTo(pConnectionPoints.getX2(), y2);
		
		Path path = new Path();
		path.getElements().addAll(moveTo, lineTo1, quadTo1, quadTo2, lineTo2);
		return path;
	}
	
	/**
     * 	Tests whether the node should be S- or C-shaped.
     * 	@return true if the node should be S-shaped
	 */
	private boolean isSShaped()
	{
		Rectangle b = edge().getEnd().view().getBounds();
		Point p = edge().getStart().view().getConnectionPoint(Direction.EAST);
		return b.getX() >= p.getX() + 2 * ENDSIZE;
	}

	@Override
	public void draw(GraphicsContext pGraphics)
	{
		ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(), LineStyle.SOLID);
		Line connectionPoints = getConnectionPoints();
		
		if(isSShaped())
		{
			ArrowHead.BLACK_TRIANGLE.view().draw(pGraphics, 
					new Point(connectionPoints.getX2() - ENDSIZE, connectionPoints.getY2()), 
					new Point(connectionPoints.getX2(), connectionPoints.getY2()));      
		}
		else
		{
			ArrowHead.BLACK_TRIANGLE.view().draw(pGraphics, 
					new Point(connectionPoints.getX2() + ENDSIZE, connectionPoints.getY2()), 
					new Point(connectionPoints.getX2(), connectionPoints.getY2()));      
		}
	}

	@Override
	public Line getConnectionPoints()
	{
		Point point = edge().getStart().view().getConnectionPoint(Direction.EAST);
		if (isSShaped())
		{
			return new Line(point, edge().getEnd().view().getConnectionPoint(Direction.WEST));
		}
		else
		{
			return new Line(point, edge().getEnd().view().getConnectionPoint(Direction.EAST));
		}
	}
}
