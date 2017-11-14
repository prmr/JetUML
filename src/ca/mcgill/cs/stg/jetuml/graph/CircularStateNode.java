/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 * An initial or final node (bull's eye) in a state or activity diagram.
 */
public class CircularStateNode extends RectangularNode
{
	private static final int DEFAULT_DIAMETER = 14;
	private static final int DEFAULT_GAP = 3;   
	
	private boolean aFinalState; 
	   
	/**
	 * Construct a node with a default size.
	 */
	public CircularStateNode()
	{     
		setBounds(new Rectangle(0, 0, DEFAULT_DIAMETER, DEFAULT_DIAMETER));      
	}
   
	/**
	 * @return true if this represents a final state.
	 */
	public boolean isFinal()
	{
		return aFinalState; 
	}
   
	/**
	 * @param pFinalState true to set this object to represent a final state.
	 */
	public void setFinal(boolean pFinalState)
	{
		aFinalState = pFinalState;
		if(aFinalState)
		{
			setBounds(new Rectangle(getBounds().getX(), getBounds().getY(),
		               DEFAULT_DIAMETER + 2 * DEFAULT_GAP, DEFAULT_DIAMETER + 2 * DEFAULT_GAP));
		}
		else
		{
			setBounds(new Rectangle(getBounds().getX(), getBounds().getY(),
		               DEFAULT_DIAMETER, DEFAULT_DIAMETER));
		}
	}
   
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle bounds = getBounds();
		double a = bounds.getWidth() / 2;
		double b = bounds.getHeight() / 2;
		double x = pDirection.getX();
		double y = pDirection.getY();
		double cx = bounds.getCenter().getX();
		double cy = bounds.getCenter().getY();
      
		if(a != 0 && b != 0 && !(x == 0 && y == 0))
		{
			double t = Math.sqrt((x * x) / (a * a) + (y * y) / (b * b));
			return new Point(cx + x / t, cy + y / t);
		}
		else
		{
			return new Point(cx, cy);
		}
	}   	 

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Ellipse2D circle = new Ellipse2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight());
      
      	if(aFinalState)
      	{
      		Rectangle bounds = getBounds();
      		Ellipse2D inside = new Ellipse2D.Double( bounds.getX() + DEFAULT_GAP, 
      				bounds.getY() + DEFAULT_GAP, bounds.getWidth() - 2 * DEFAULT_GAP, bounds.getHeight() - 2 * DEFAULT_GAP);
      		pGraphics2D.fill(inside);
      		pGraphics2D.draw(circle);
      	}
		else
		{
			pGraphics2D.fill(circle);
		}      
	}
   
	@Override
	public Shape getShape()
	{
		return new Ellipse2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth() - 1, getBounds().getHeight() - 1);
	}
}

