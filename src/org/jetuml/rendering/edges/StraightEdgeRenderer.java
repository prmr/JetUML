/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.GraphicsRenderingContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Can draw an edge as a straight line between the connection
 * points of the start and end nodes that are closest to each
 * other. The LineStyle and end ArrowHead can be customized. The 
 * start ArrowHead is NONE. There is no label.
 */
public class StraightEdgeRenderer extends AbstractEdgeRenderer
{	
	private final LineStyle aLineStyle;
	private final ArrowHead aArrowHead;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead.
	 * 
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 */
	public StraightEdgeRenderer(DiagramRenderer pParent, LineStyle pLineStyle, ArrowHead pArrowHead)
	{
		super(pParent);
		aLineStyle = pLineStyle;
		aArrowHead = pArrowHead;
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		Edge edge = (Edge) pElement;
		Path shape = (Path) getShape(edge);
		pContext.strokePath(shape, ColorScheme.get().stroke(), aLineStyle);
		Line connectionPoints = getConnectionPoints(edge);
		ArrowHeadRenderer.draw(pContext, aArrowHead, connectionPoints);
	}
	
	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		Rectangle bounds = super.getBounds(pElement);
		Edge edge = (Edge) pElement;
		if( aArrowHead != ArrowHead.NONE )
		{
			bounds = bounds.add(ArrowHeadRenderer.getBounds(aArrowHead, getConnectionPoints(edge)));
		}
		return bounds;
	}
	
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		GraphicsRenderingContext context = new GraphicsRenderingContext(canvas.getGraphicsContext2D());
		context.strokePath(path, ColorScheme.get().stroke(), aLineStyle);
		ArrowHeadRenderer.draw(context, aArrowHead, 
				new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		return canvas;
	}
}
