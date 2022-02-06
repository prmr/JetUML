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

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Can draw an edge as a straight line between the connection
 * points of the start and end nodes that are closest to each
 * other. The LineStyle and end ArrowHead can be customized. The 
 * start ArrowHead is NONE. There is no label.
 */
public class StraightEdgeViewer extends AbstractEdgeViewer
{	
	private final LineStyle aLineStyle;
	private final ArrowHead aArrowHead;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead.
	 * 
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 */
	public StraightEdgeViewer(LineStyle pLineStyle, ArrowHead pArrowHead)
	{
		aLineStyle = pLineStyle;
		aArrowHead = pArrowHead;
	}
	
	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		Path shape = (Path) getShape(pEdge);
		ToolGraphics.strokeSharpPath(pGraphics, shape, aLineStyle);
		Line connectionPoints = getConnectionPoints(pEdge);
		aArrowHead.view().draw(pGraphics, connectionPoints.getPoint1(), connectionPoints.getPoint2());
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		Rectangle bounds = super.getBounds(pEdge);
		if( aArrowHead != ArrowHead.NONE )
		{
			Line connectionPoints = getConnectionPoints(pEdge);
			bounds = bounds.add(Conversions.toRectangle(aArrowHead.view().getPath(connectionPoints.getPoint1(), 
					connectionPoints.getPoint2()).getBoundsInLocal()));
		}
		return bounds;
	}
	
	@Override
	public Canvas createIcon(Edge pEdge)
	{
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		ToolGraphics.strokeSharpPath(canvas.getGraphicsContext2D(), path, aLineStyle);
		aArrowHead.view().draw(canvas.getGraphicsContext2D(), new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		return canvas;
	}
}
