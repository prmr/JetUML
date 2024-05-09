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
package org.jetuml.rendering.edges;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.ToolGraphics;

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
public final class ObjectReferenceEdgeRenderer extends AbstractEdgeRenderer
{
	private static final int ENDSIZE = 10;
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public ObjectReferenceEdgeRenderer(DiagramRenderer pParent)
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
		final int x1 = pConnectionPoints.x1() + ENDSIZE;
		final int y1 = pConnectionPoints.y1();
		final int x2 = pConnectionPoints.x2() - ENDSIZE;
		final int y2 = pConnectionPoints.y2();
		final int xmid = (pConnectionPoints.x1() + pConnectionPoints.x2()) / 2;
		final int ymid = (pConnectionPoints.y1() + pConnectionPoints.y2()) / 2;
     
		MoveTo moveTo = new MoveTo(pConnectionPoints.x1(), y1);
		LineTo lineTo1 = new LineTo(x1, y1);
		QuadCurveTo quadTo1 = new QuadCurveTo((x1 + xmid) / 2, y1, xmid, ymid);
		QuadCurveTo quadTo2 = new QuadCurveTo((x2 + xmid) / 2, y2, x2, y2);
		LineTo lineTo2 = new LineTo(pConnectionPoints.x2(), y2);
		
		Path path = new Path();
		path.getElements().addAll(moveTo, lineTo1, quadTo1, quadTo2, lineTo2);
		return path;
	}
	
	private static Path getCShape(Line pConnectionPoints)
	{
		final int x1 = Math.max(pConnectionPoints.x1(), pConnectionPoints.x2()) + ENDSIZE;
		final int y1 = pConnectionPoints.y1();
		final int x2 = x1 + ENDSIZE;
		final int y2 = pConnectionPoints.y2();
		final int ymid = (pConnectionPoints.y1() + pConnectionPoints.y2()) / 2;
		
		MoveTo moveTo = new MoveTo(pConnectionPoints.x1(), y1);
		LineTo lineTo1 = new LineTo(x1, y1);
		QuadCurveTo quadTo1 = new QuadCurveTo(x2, y1, x2, ymid);
		QuadCurveTo quadTo2 = new QuadCurveTo(x2, y2, x1, y2);
		LineTo lineTo2 = new LineTo(pConnectionPoints.x2(), y2);
		
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
		Rectangle b = parent().getBounds(pEdge.end());
		Point p = parent().getConnectionPoints(pEdge.start(), Direction.EAST);
		return b.x() >= p.x() + 2 * ENDSIZE;
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		Edge edge = (Edge) pElement;
		ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(edge), LineStyle.SOLID);
		Line connectionPoints = getConnectionPoints(edge);
		
		if(isSShaped(edge))
		{
			ArrowHeadRenderer.draw(pGraphics, ArrowHead.BLACK_TRIANGLE,
					new Point(connectionPoints.x2() - ENDSIZE, connectionPoints.y2()), 
					new Point(connectionPoints.x2(), connectionPoints.y2()));      
		}
		else
		{
			ArrowHeadRenderer.draw(pGraphics, ArrowHead.BLACK_TRIANGLE,
					new Point(connectionPoints.x2() + ENDSIZE, connectionPoints.y2()), 
					new Point(connectionPoints.x2(), connectionPoints.y2()));      
		}
	}

	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		Point point = parent().getConnectionPoints(pEdge.start(), Direction.EAST);
		if(isSShaped(pEdge))
		{
			return new Line(point, parent().getConnectionPoints(pEdge.end(), Direction.WEST));
		}
		else
		{
			return new Line(point, parent().getConnectionPoints(pEdge.end(), Direction.EAST));
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
		ArrowHeadRenderer.draw(graphics, ArrowHead.BLACK_TRIANGLE, new Point(20,25), new Point(15, 25));
		return canvas;
	}
}
