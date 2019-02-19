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
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

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
	
	@Override
	public Canvas createIcon()
	{
		final float scale = 0.6f;
		final int offset = 25;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().scale(scale, scale);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(1, offset), new LineTo(BUTTON_SIZE*(1/scale)-1, offset));
		ToolGraphics.strokeSharpPath(graphics, path, LineStyle.DOTTED);
		ArrowHead.V.view().draw(graphics, new Point((int)(BUTTON_SIZE*(1/scale)-1), offset), new Point(1, offset));
		return canvas;
	}
}