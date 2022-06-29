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
package org.jetuml.viewers.edges;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.viewers.ArrowHead;
import org.jetuml.viewers.LineStyle;
import org.jetuml.viewers.ToolGraphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * A viewer for an S- or C-shaped edge with an arrowhead.
 */
public final class ObjectReferenceEdgeViewer extends AbstractEdgeViewer
{
	private static final int ENDSIZE = 10;
	
	public ObjectReferenceEdgeViewer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	protected Shape getShape(Edge pEdge)
	{
		if(isSShaped(pEdge))
		{
			return getSShape(getConnectionPoints(pEdge));
		}
		else
		{
			return getCShape(getConnectionPoints(pEdge));
		}			
	}
	
	private static Path getSShape(Line pConnectionPoints)
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
	
	private static Path getCShape(Line pConnectionPoints)
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
	private boolean isSShaped(Edge pEdge)
	{
		Rectangle b = RenderingFacade.getBounds(pEdge.getEnd());
		Point p = parent().getConnectionPoints(pEdge.getStart(), Direction.EAST);
		return b.getX() >= p.getX() + 2 * ENDSIZE;
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		Edge edge = (Edge) pElement;
		ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(edge), LineStyle.SOLID);
		Line connectionPoints = getConnectionPoints(edge);
		
		if(isSShaped(edge))
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
	public Line getConnectionPoints(Edge pEdge)
	{
		Point point = parent().getConnectionPoints(pEdge.getStart(), Direction.EAST);
		if (isSShaped(pEdge))
		{
			return new Line(point, parent().getConnectionPoints(pEdge.getEnd(), Direction.WEST));
		}
		else
		{
			return new Line(point, parent().getConnectionPoints(pEdge.getEnd(), Direction.EAST));
		}
	}
	
	@Override
	public Canvas createIcon(DiagramType pType, DiagramElement pElement)
	{   //CSOFF: Magic numbers
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(0.6, 0.6);
		Path path = getCShape(new Line(new Point(5, 5), new Point(15,25)));
		ToolGraphics.strokeSharpPath(graphics, path, LineStyle.SOLID);
		ArrowHead.BLACK_TRIANGLE.view().draw(graphics, new Point(20,25), new Point(15, 25));
		return canvas;
	}
}
