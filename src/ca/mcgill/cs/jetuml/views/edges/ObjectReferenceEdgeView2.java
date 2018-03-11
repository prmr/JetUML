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

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

/**
 * An S- or C-shaped edge with an arrowhead.
 */
public class ObjectReferenceEdgeView2 extends AbstractEdgeView2
{
	private static final int ENDSIZE = 10;
	
	/**
	 * @param pEdge the edge to wrap.
	 */
	public ObjectReferenceEdgeView2(Edge pEdge)
	{
		super(pEdge);
	}
	
	@Override
	protected Shape getShape()
	{
		Line line = getConnectionPoints();

		double y1 = line.getY1();
		double y2 = line.getY2();
		double xmid = (line.getX1() + line.getX2()) / 2;
		double ymid = (line.getY1() + line.getY2()) / 2;
		GeneralPath path = new GeneralPath();
		if(isSShaped())
		{
			double x1 = line.getX1() + ENDSIZE;
			double x2 = line.getX2() - ENDSIZE;
         
			path.moveTo((float)line.getX1(), (float)y1);
			path.lineTo((float)x1, (float)y1);
			path.quadTo((float)((x1 + xmid) / 2), (float)y1, (float)xmid, (float)ymid);
			path.quadTo((float)((x2 + xmid) / 2), (float)y2, (float)x2, (float)y2);
			path.lineTo((float)line.getX2(), (float)y2);
		}
		else // reverse C shaped
		{
			double x1 = Math.max(line.getX1(), line.getX2()) + ENDSIZE;
			double x2 = x1 + ENDSIZE;
			path.moveTo((float)line.getX1(), (float)y1);
			path.lineTo((float)x1, (float)y1);
			path.quadTo((float)x2, (float)y1, (float)x2, (float)ymid);
			path.quadTo((float)x2, (float)y2, (float)x1, (float)y2);
			path.lineTo((float)line.getX2(), (float)y2);
		}
		return path;
	}
	
	@Override
	protected void fillShape(GraphicsContext pGraphics) 
	{
		// TODO Auto-generated method stub
		
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
		pGraphics.draw(getShape());
		Line line = getConnectionPoints();
		double x1;
		double x2 = line.getX2();
		double y = line.getY2();
		if (isSShaped())
		{
			x1 = x2 - ENDSIZE;
		}
		else
		{
			x1 = x2 + ENDSIZE;
		}
		ArrowHead.BLACK_TRIANGLE.view().draw(pGraphics, new Point2D.Double(x1, y), new Point2D.Double(x2, y));      
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
