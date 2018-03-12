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

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
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
		return null;
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
	public void draw(GraphicsContext pGraphics) {}

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
