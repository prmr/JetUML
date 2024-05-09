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
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.PointNode;
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

/**
 * Viewer for a labeled, straight edge with customized code to
 * compute connection points.
 */
public final class ReturnEdgeRenderer extends LabeledStraightEdgeRenderer
{	
	/**
	 * Creates a new viewer.
	 */
	public ReturnEdgeRenderer(DiagramRenderer pParent)
	{
		super(pParent, LineStyle.DOTTED, ArrowHead.V, edge -> ((ReturnEdge)edge).getMiddleLabel());
	}
	
	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		Rectangle start = parent().getBounds(pEdge.start());
		Rectangle end = parent().getBounds(pEdge.end());
		
		if(pEdge.end() instanceof PointNode) // show nicely in tool bar
		{
			return new Line(new Point(end.x(), end.y()), new Point(start.maxX(), end.y()));
		}      
		else if(start.center().x() < end.center().x())
		{
			return new Line(new Point(start.maxX(), start.maxY()), new Point(end.x(), start.maxY()));
		}
		else
		{
			return new Line(new Point(start.x(), start.maxY()), new Point(end.maxX(), start.maxY()));
		}
	}
	
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		final float scale = 0.6f;
		final int offset = 25;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().scale(scale, scale);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(1, offset), new LineTo(BUTTON_SIZE*(1/scale)-1, offset));
		ToolGraphics.strokeSharpPath(graphics, path, LineStyle.DOTTED);
		ArrowHeadRenderer.draw(graphics, ArrowHead.V, new Point((int)(BUTTON_SIZE*(1/scale)-1), offset), new Point(1, offset));
		return canvas;
	}
}