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
package ca.mcgill.cs.jetuml.viewers.nodes;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a initial or final node.
 */
public final class CircularStateNodeViewer extends AbstractNodeViewer
{
	private static final int DIAMETER = 20;
	private final boolean aFinal;
	
	/**
	 * @param pFinal true if this is a final node, false if it is an initial node.
	 */
	public CircularStateNodeViewer(boolean pFinal)
	{
		aFinal = pFinal;
	}

	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds(pNode);
		if( aFinal )
		{
			ViewUtils.drawCircle(pGraphics, bounds.getX(), bounds.getY(), DIAMETER, Color.WHITE, true);
			int innerDiameter = DIAMETER/2;
			ViewUtils.drawCircle(pGraphics, bounds.getX() + innerDiameter/2, 
					bounds.getY() + innerDiameter/2, innerDiameter, Color.BLACK, false);
		}
		else
		{
			ViewUtils.drawCircle(pGraphics, bounds.getX(), bounds.getY(), DIAMETER, Color.BLACK, true);
		}
	}
	
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		final Rectangle bounds = getBounds(pNode);
		double a = bounds.getWidth() / 2;
		double b = bounds.getHeight() / 2;
		double x = pDirection.getX();
		double y = pDirection.getY();
		double cx = bounds.getCenter().getX();
		double cy = bounds.getCenter().getY();
      
		if(a != 0 && b != 0 && !(x == 0 && y == 0))
		{
			double t = Math.sqrt((x * x) / (a * a) + (y * y) / (b * b));
			return new Point( (int) Math.round(cx + x / t), (int) Math.round(cy + y / t));
		}
		else
		{
			return new Point((int) Math.round(cx), (int) Math.round(cy));
		}
	}   	 

	@Override
	public Rectangle getBounds(Node pNode)
	{
		return new Rectangle(pNode.position().getX(), pNode.position().getY(), DIAMETER, DIAMETER);
	}
}
